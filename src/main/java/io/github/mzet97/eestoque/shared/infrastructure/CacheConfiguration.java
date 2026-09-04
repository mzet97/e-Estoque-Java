package io.github.mzet97.eestoque.shared.infrastructure;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Spring Cache + Redis para leituras de referência estáveis (ADR-009):
 * apenas GetById de Categories/Companies/Customers/Taxes, TTL 15 min,
 * evict nas escritas. Se o Redis estiver indisponível — ou falhar qualquer
 * operação de cache — a aplicação segue direto ao banco (erro de cache
 * NUNCA derruba a request).
 */
@Configuration
@EnableCaching
public class CacheConfiguration implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    public static final String CATEGORY_BY_ID = "reference:category";
    public static final String COMPANY_BY_ID = "reference:company";
    public static final String CUSTOMER_BY_ID = "reference:customer";
    public static final String TAX_BY_ID = "reference:tax";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("io.github.mzet97.eestoque")
                                .allowIfSubType("java.util")
                                .allowIfSubType("java.time")
                                .build(),
                        ObjectMapper.DefaultTyping.EVERYTHING,
                        JsonTypeInfo.As.PROPERTY);
        var serializer = new GenericJackson2JsonRedisSerializer(mapper);

        var base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base)
                .withCacheConfiguration(CATEGORY_BY_ID, base)
                .withCacheConfiguration(COMPANY_BY_ID, base)
                .withCacheConfiguration(CUSTOMER_BY_ID, base)
                .withCacheConfiguration(TAX_BY_ID, base)
                // Evict/put só AFTER_COMMIT: evita recache do valor antigo no PUT (race evict vs commit)
                .transactionAware()
                .build();
    }

    /**
     * Falhas do Redis são registradas e ignoradas: leituras/escritas seguem
     * para o banco. (SimpleCacheErrorHandler relança — por isso o override.)
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
                log.warn("cache get falhou (seguindo ao banco): cache={} key={}", cache.getName(), key);
            }

            @Override
            public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
                log.warn("cache put falhou: cache={} key={}", cache.getName(), key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
                log.warn("cache evict falhou: cache={} key={}", cache.getName(), key);
            }

            @Override
            public void handleCacheClearError(RuntimeException ex, Cache cache) {
                log.warn("cache clear falhou: cache={}", cache.getName());
            }
        };
    }
}
