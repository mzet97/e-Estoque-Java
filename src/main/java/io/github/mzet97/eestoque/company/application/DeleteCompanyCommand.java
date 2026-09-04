package io.github.mzet97.eestoque.company.application;

import java.util.UUID;

import io.github.mzet97.eestoque.shared.application.Command;

/** FR-COMPANY-003 — hard delete. */
public record DeleteCompanyCommand(UUID id) implements Command<Void> {
}
