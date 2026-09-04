package io.github.mzet97.eestoque.identity.application;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-AUTH-001. */
public record RegisterUserCommand(
        String username,
        String password,
        String confirmPassword,
        String email,
        String firstName,
        String lastName) implements Command<TokenResponse> {
}
