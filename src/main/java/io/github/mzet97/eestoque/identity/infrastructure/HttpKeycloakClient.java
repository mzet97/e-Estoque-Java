package io.github.mzet97.eestoque.identity.infrastructure;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import io.github.mzet97.eestoque.identity.application.KeycloakClient;
import io.github.mzet97.eestoque.identity.application.TokenResponse;

/**
 * Cliente HTTP do Keycloak (password/refresh/client_credentials grants +
 * Admin API). Nenhum segredo é logado (NFR-SEC-003).
 */
@Component
public class HttpKeycloakClient implements KeycloakClient {

    private static final Logger log = LoggerFactory.getLogger(HttpKeycloakClient.class);

    private final RestClient restClient;
    private final KeycloakProperties properties;

    public HttpKeycloakClient(KeycloakProperties properties) {
        // Boot 4 modularizado não auto-configura RestClient.Builder; criamos direto.
        this.restClient = RestClient.create();
        this.properties = properties;
    }

    @Override
    public Optional<TokenResponse> login(String username, String password) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "password");
        form.add("client_id", properties.clientId());
        form.add("username", username);
        form.add("password", password);
        form.add("client_secret", properties.clientSecret());
        return post(form);
    }

    @Override
    public Optional<TokenResponse> refresh(String refreshToken) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", properties.clientId());
        form.add("refresh_token", refreshToken);
        form.add("client_secret", properties.clientSecret());
        return post(form);
    }

    @Override
    public Optional<TokenResponse> systemLogin() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        return post(form);
    }

    @Override
    public boolean createUser(String systemToken, String username, String password, String email, String firstName,
                              String lastName) {
        // Map.of não aceita null; o payload pode vir com campos null (JSON omite)
        var user = new java.util.HashMap<String, Object>();
        user.put("username", username);
        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("emailVerified", true);
        user.put("enabled", true);
        user.put("attributes", java.util.Map.of("attribute_key", "client"));
        user.put("credentials", new Object[] {java.util.Map.of(
                "type", "password",
                "value", password,
                "temporary", false)});
        user.values().removeIf(java.util.Objects::isNull);

        try {
            var response = restClient.post()
                    .uri(properties.usersEndpoint())
                    .header("Authorization", "Bearer " + systemToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(user)
                    .retrieve()
                    .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientResponseException ex) {
            log.warn("Keycloak user creation failed with status {}", ex.getStatusCode().value());
            return false;
        }
    }

    private Optional<TokenResponse> post(LinkedMultiValueMap<String, String> form) {
        try {
            var token = restClient.post()
                    .uri(properties.tokenEndpoint())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);
            return Optional.ofNullable(token);
        } catch (RestClientResponseException ex) {
            // 400/401 do Keycloak = credenciais/refresh inválidos.
            return Optional.empty();
        }
    }
}
