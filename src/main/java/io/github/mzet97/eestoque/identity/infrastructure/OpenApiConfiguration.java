package io.github.mzet97.eestoque.identity.infrastructure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expõe o esquema Bearer JWT no OpenAPI para que o Swagger UI mostre o
 * botão "Authorize" (cole apenas o token, sem o prefixo Bearer).
 */
@Configuration
public class OpenApiConfiguration {

    private static final String SCHEME = "bearer-jwt";

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("e-Estoque API")
                        .description("Migração Java/Spring da e-Estoque-API (.NET 8). "
                                + "Escritas (POST/PUT/DELETE) exigem a realm role Create do Keycloak; "
                                + "obtenha o token em POST /api/Auth/login.")
                        .version("1.0.0"))
                .components(new Components().addSecuritySchemes(SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME));
    }
}
