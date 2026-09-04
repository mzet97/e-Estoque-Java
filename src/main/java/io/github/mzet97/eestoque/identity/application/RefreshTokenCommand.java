package io.github.mzet97.eestoque.identity.application;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-AUTH-003. */
public record RefreshTokenCommand(String token) implements Command<TokenResponse> {
}
