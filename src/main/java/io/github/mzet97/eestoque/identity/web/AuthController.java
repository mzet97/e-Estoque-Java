package io.github.mzet97.eestoque.identity.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.mzet97.eestoque.identity.application.LoginUserCommand;
import io.github.mzet97.eestoque.identity.application.RefreshTokenCommand;
import io.github.mzet97.eestoque.identity.application.RegisterUserCommand;
import io.github.mzet97.eestoque.identity.application.TokenResponse;
import io.github.mzet97.eestoque.shared.application.CommandBus;
import jakarta.validation.Valid;

/** FR-AUTH-001..003 (anônimo; proxy para o Keycloak). */
@RestController
@RequestMapping("/api/Auth")
public class AuthController {

    private final CommandBus commands;

    public AuthController(CommandBus commands) {
        this.commands = commands;
    }

    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody RegisterUserCommand command) {
        return commands.dispatch(command);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginUserCommand command) {
        return commands.dispatch(command);
    }

    @PostMapping("/refresh_token")
    public TokenResponse refreshToken(@Valid @RequestBody RefreshTokenCommand command) {
        return commands.dispatch(command);
    }
}
