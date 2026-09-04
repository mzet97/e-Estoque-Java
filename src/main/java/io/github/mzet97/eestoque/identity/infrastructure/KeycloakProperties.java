package io.github.mzet97.eestoque.identity.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração do Keycloak (IdP externo) usada pelo resource server e pelo
 * proxy de autenticação (/api/Auth). Nenhum secret é commitado (MD-03).
 */
@ConfigurationProperties(prefix = "eestoque.keycloak")
public record KeycloakProperties(
        String baseUrl,
        String realm,
        String clientId,
        String clientSecret) {

    public String issuer() {
        return baseUrl + "/realms/" + realm;
    }

    public String tokenEndpoint() {
        return issuer() + "/protocol/openid-connect/token";
    }

    public String usersEndpoint() {
        return baseUrl + "/admin/realms/" + realm + "/users";
    }
}
