package io.github.mzet97.eestoque.identity.application;

import java.util.Optional;

/** Porta do Keycloak (token endpoint + Admin API). */
public interface KeycloakClient {

    /** Password grant; vazio = credenciais inválidas. */
    Optional<TokenResponse> login(String username, String password);

    /** Refresh token grant; vazio = refresh inválido. */
    Optional<TokenResponse> refresh(String refreshToken);

    /** Client credentials grant (system token). */
    Optional<TokenResponse> systemLogin();

    /** Cria o usuário na Admin API; false = falha (403 no contrato). */
    boolean createUser(String systemToken, String username, String password, String email, String firstName,
                       String lastName);
}
