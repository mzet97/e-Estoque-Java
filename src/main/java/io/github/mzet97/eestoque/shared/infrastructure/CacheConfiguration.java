package io.github.mzet97.eestoque.shared.infrastructure;

import java.time.Duration;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Spring Cache + Redis para leituras de referência estáveis (ADR-009):
 * apenas GetById de Categories/Companies/Customers/Taxes, TTL 15 min,
 * evict nas escritas. Se o Redis estiver indisponível, a aplicação segue
 * direto ao banco (erro de cache nunca derruba a request).
 */
@Configuration
@EnableCaching
public class CacheConfiguration implements CachingConfigurer {

    public static final String CATEGORY_BY_ID = "reference:category";
    public static final String COMPANY_BY_ID = "reference:company";
    public static final String CUSTOMER_BY_ID = "reference:customer";
    public static final String TAX_BY_ID = "reference:tax";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                        .fromSerializer(new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base)
                .withCacheConfiguration(CATEGORY_BY_ID, base)
                .withCacheConfiguration(COMPANY_BY_ID, base)
                .withCacheConfiguration(CUSTOMER_BY_ID, base)
                .withCacheConfiguration(TAX_BY_ID, base)
                .build();
    }

    /**
     * Falhas do Redis são registradas e ignoradas: leituras/escritas seguem
     * para o banco (ADR-009 — cache nunca derruba a request).
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }
}
