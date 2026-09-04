package io.github.mzet97.eestoque.identity.application;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.NotBlank;

/** FR-AUTH-002 — email usado como username do password grant. */
public record LoginUserCommand(
        @NotBlank String email,
        @NotBlank String password) implements Command<TokenResponse> {
}
