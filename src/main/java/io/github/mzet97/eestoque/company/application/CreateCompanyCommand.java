package io.github.mzet97.eestoque.company.application;

import java.util.UUID;

import io.github.mzet97.eestoque.company.domain.Address;
import io.github.mzet97.eestoque.shared.application.Command;

/** FR-COMPANY-001. */
public record CreateCompanyCommand(
        String name,
        String docId,
        String email,
        String description,
        String phoneNumber,
        Address companyAddress) implements Command<UUID> {
}
