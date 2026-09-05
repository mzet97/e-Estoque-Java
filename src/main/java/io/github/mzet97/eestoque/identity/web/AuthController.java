package io.github.mzet97.eestoque.identity.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.identity.application.AuthHandlers.LoginUserHandler;
import io.github.mzet97.eestoque.identity.application.AuthHandlers.RefreshTokenHandler;
import io.github.mzet97.eestoque.identity.application.AuthHandlers.RegisterUserHandler;
import io.github.mzet97.eestoque.identity.application.LoginUserCommand;
import io.github.mzet97.eestoque.identity.application.RefreshTokenCommand;
import io.github.mzet97.eestoque.identity.application.RegisterUserCommand;
import io.github.mzet97.eestoque.identity.application.TokenResponse;
import jakarta.validation.Valid;

/** FR-AUTH-001..003 (anônimo; proxy para o Keycloak). */
@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    private final RegisterUserHandler registerHandler;
    private final LoginUserHandler loginHandler;
    private final RefreshTokenHandler refreshTokenHandler;

    public AuthController(RegisterUserHandler registerHandler, LoginUserHandler loginHandler,
                          RefreshTokenHandler refreshTokenHandler) {
        this.registerHandler = registerHandler;
        this.loginHandler = loginHandler;
        this.refreshTokenHandler = refreshTokenHandler;
    }

    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody RegisterUserCommand command) {
        return registerHandler.handle(command);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginUserCommand command) {
        return loginHandler.handle(command);
    }

    @PostMapping("/refresh_token")
    public TokenResponse refreshToken(@Valid @RequestBody RefreshTokenCommand command) {
        return refreshTokenHandler.handle(command);
    }
}
