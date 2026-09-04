package io.github.mzet97.eestoque.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.github.mzet97.eestoque.shared.domain.ForbiddenAccessException;

class AuthHandlersTest {

    private final KeycloakClient keycloak = mock(KeycloakClient.class);
    private final AuthHandlers.LoginUserHandler loginHandler = new AuthHandlers.LoginUserHandler(keycloak);
    private final AuthHandlers.SystemLoginHandler systemLoginHandler = new AuthHandlers.SystemLoginHandler(keycloak);
    private final AuthHandlers.RefreshTokenHandler refreshHandler = new AuthHandlers.RefreshTokenHandler(keycloak);
    private final AuthHandlers.RegisterUserHandler registerHandler =
            new AuthHandlers.RegisterUserHandler(keycloak, loginHandler, systemLoginHandler);

    private static TokenResponse token(String accessToken) {
        return new TokenResponse(accessToken, 300, "refresh", 1800, "Bearer", 0, "session", "openid");
    }

    @Test
    void loginReturnsToken() {
        when(keycloak.login("a@b.c", "pass")).thenReturn(Optional.of(token("jwt")));

        var response = loginHandler.handle(new LoginUserCommand("a@b.c", "pass"));

        assertThat(response.accessToken()).isEqualTo("jwt");
        assertThat(response.refreshToken()).isEqualTo("refresh");
    }

    @Test
    void loginWithBadCredentialsThrows403WithDotNetMessage() {
        when(keycloak.login("a@b.c", "wrong")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginHandler.handle(new LoginUserCommand("a@b.c", "wrong")))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void refreshWithInvalidTokenThrows403() {
        when(keycloak.refresh("bad")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshHandler.handle(new RefreshTokenCommand("bad")))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void registerCreatesUserThenLogsIn() {
        when(keycloak.systemLogin()).thenReturn(Optional.of(token("system")));
        when(keycloak.createUser(eq("system"), eq("bob"), any(), any(), any(), any())).thenReturn(true);
        when(keycloak.login("bob", "pass")).thenReturn(Optional.of(token("user-jwt")));

        var response = registerHandler.handle(new RegisterUserCommand("bob", "pass", "pass", "b@b.c", "Bob",
                "Silva"));

        assertThat(response.accessToken()).isEqualTo("user-jwt");
    }

    @Test
    void registerFailureThrows403ErrorCreatingUser() {
        when(keycloak.systemLogin()).thenReturn(Optional.of(token("system")));
        when(keycloak.createUser(any(), any(), any(), any(), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> registerHandler.handle(new RegisterUserCommand("bob", "pass", "pass", "b@b.c",
                "Bob", "Silva")))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessage("Error creating user");
    }
}
