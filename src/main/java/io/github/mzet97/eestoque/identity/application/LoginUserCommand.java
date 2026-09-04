package io.github.mzet97.eestoque.identity.application;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-AUTH-002 — email usado como username do password grant. */
public record LoginUserCommand(
        String email,
        String password) implements Command<TokenResponse> {
}
