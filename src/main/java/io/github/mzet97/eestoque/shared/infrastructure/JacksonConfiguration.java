package io.github.mzet97.eestoque.shared.infrastructure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Garante que o JSON HTTP seja servido pelo Jackson 3 (tools.jackson,
 * java.time embutido → Instant ISO-8601). Com Jackson 2 no classpath
 * (via springdoc), o Boot 4 pode montar um converter Jackson 2 sem
 * jsr310, quebrando a serialização de Instant.
 */
@Configuration
public class JacksonConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JacksonConfiguration.class);

    @Bean
    JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter() {
        return new JacksonJsonHttpMessageConverter();
    }

    @Bean
    WebMvcConfigurer jackson3FirstConfigurer(JacksonJsonHttpMessageConverter converter) {
        return new WebMvcConfigurer() {
            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.add(0, converter);
                log.info("HTTP message converters (ordem efetiva): {}",
                        converters.stream().map(c -> c.getClass().getSimpleName()).toList());
            }
        };
    }
}
