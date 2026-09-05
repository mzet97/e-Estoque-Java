package io.github.mzet97.eestoque.identity.application;

import org.springframework.stereotype.Component;

import io.github.mzet97.eestoque.shared.application.CommandHandler;
import io.github.mzet97.eestoque.shared.domain.ForbiddenAccessException;

/** Handlers de Auth (proxy Keycloak — mensagens .NET exatas). */
public final class AuthHandlers {

    private AuthHandlers() {
    }

    @Component
    public static class LoginUserHandler implements CommandHandler<LoginUserCommand, TokenResponse> {

        private final KeycloakClient keycloak;

        public LoginUserHandler(KeycloakClient keycloak) {
            this.keycloak = keycloak;
        }

        @Override
        public TokenResponse handle(LoginUserCommand command) {
            return keycloak.login(command.email(), command.password())
                    .filter(TokenResponse::hasAccessToken)
                    .orElseThrow(() -> new ForbiddenAccessException("Invalid username or password"));
        }
    }

    @Component
    public static class RefreshTokenHandler implements CommandHandler<RefreshTokenCommand, TokenResponse> {

        private final KeycloakClient keycloak;

        public RefreshTokenHandler(KeycloakClient keycloak) {
            this.keycloak = keycloak;
        }

        @Override
        public TokenResponse handle(RefreshTokenCommand command) {
            return keycloak.refresh(command.token())
                    .orElseThrow(() -> new ForbiddenAccessException("Invalid refresh token"));
        }
    }

    @Component
    public static class SystemLoginHandler implements CommandHandler<SystemLoginCommand, TokenResponse> {

        private final KeycloakClient keycloak;

        public SystemLoginHandler(KeycloakClient keycloak) {
            this.keycloak = keycloak;
        }

        @Override
        public TokenResponse handle(SystemLoginCommand command) {
            return keycloak.systemLogin()
                    .orElseThrow(() -> new ForbiddenAccessException("Invalid username or password"));
        }
    }

    @Component
    public static class RegisterUserHandler implements CommandHandler<RegisterUserCommand, TokenResponse> {

        private final KeycloakClient keycloak;
        private final CommandHandler<LoginUserCommand, TokenResponse> loginHandler;
        private final CommandHandler<SystemLoginCommand, TokenResponse> systemLoginHandler;

        public RegisterUserHandler(KeycloakClient keycloak, LoginUserHandler loginHandler,
                                   SystemLoginHandler systemLoginHandler) {
            this.keycloak = keycloak;
            this.loginHandler = loginHandler;
            this.systemLoginHandler = systemLoginHandler;
        }

        @Override
        public TokenResponse handle(RegisterUserCommand command) {
            var systemToken = systemLoginHandler.handle(new SystemLoginCommand());

            var created = keycloak.createUser(systemToken.accessToken(), command.username(), command.password(),
                    command.email(), command.firstName(), command.lastName());
            if (!created) {
                throw new ForbiddenAccessException("Error creating user");
            }

            return loginHandler.handle(new LoginUserCommand(command.username(), command.password()));
        }
    }
}
