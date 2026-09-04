package io.github.mzet97.eestoque.identity.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Resource server OAuth2 (JWT do realm e-estoque).
 *
 * Regras equivalentes ao .NET: consultas exigem apenas token válido;
 * escritas (POST/PUT/DELETE) exigem a realm role "Create"; /health e os
 * endpoints de probing ficam anônimos.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/actuator/health", "/actuator/health/**", "/actuator/info")
                        .permitAll()
                        // Scrape do Prometheus era público no .NET (MapPrometheusScrapingEndpoint)
                        .requestMatchers("/actuator/prometheus")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/Auth/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**", "/odata/**")
                        .authenticated()
                        .requestMatchers("/api/**", "/odata/**")
                        .hasRole("Create")
                        .anyRequest()
                        .denyAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(new KeycloakRealmRolesConverter())));
        return http.build();
    }
}
