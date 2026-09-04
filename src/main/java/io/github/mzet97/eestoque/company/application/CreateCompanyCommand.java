package io.github.mzet97.eestoque.company.application;

import java.util.UUID;

import io.github.mzet97.eestoque.company.domain.Address;
import io.github.mzet97.eestoque.shared.application.Command;
import jakarta.validation.constraints.Size;

/** FR-COMPANY-001. */
public record CreateCompanyCommand(
        @Size(min = 3, max = 80) String name,
        @Size(min = 3, max = 80) String docId,
        @Size(min = 3, max = 250) String email,
        @Size(min = 3, max = 250) String description,
        @Size(min = 3, max = 80) String phoneNumber,
        Address companyAddress) implements Command<UUID> {
}
