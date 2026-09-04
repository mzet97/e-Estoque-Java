package io.github.mzet97.eestoque.identity.application;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resposta do token endpoint do Keycloak, devolvida em snake_case
 * (contrato original da .NET API, FR-AUTH).
 */
public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("refresh_expires_in") Integer refreshExpiresIn,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("not-before-policy") Integer notBeforePolicy,
        @JsonProperty("session_state") String sessionState,
        @JsonProperty("scope") String scope) {

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isBlank();
    }
}
