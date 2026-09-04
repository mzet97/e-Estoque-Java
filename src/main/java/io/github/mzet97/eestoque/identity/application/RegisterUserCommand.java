package io.github.mzet97.eestoque.identity.application;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.NotBlank;

/** FR-AUTH-001. */
public record RegisterUserCommand(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String confirmPassword,
        @NotBlank String email,
        @NotBlank String firstName,
        @NotBlank String lastName) implements Command<TokenResponse> {
}
