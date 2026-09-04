package io.github.mzet97.eestoque.identity.application;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-AUTH-004 — client_credentials (uso interno do registro). */
public record SystemLoginCommand() implements Command<TokenResponse> {
}
