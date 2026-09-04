package io.github.mzet97.eestoque.identity.application;

import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.NotBlank;

/** FR-AUTH-003. */
public record RefreshTokenCommand(@NotBlank String token) implements Command<TokenResponse> {
}
