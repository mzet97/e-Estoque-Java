package io.github.mzet97.eestoque;

import org.junit.jupiter.api.Test;

import org.springframework.modulith.core.ApplicationModules;

/**
 * Verificação arquitetural: falha o build se as fronteiras dos módulos
 * forem violadas (NFR-ARCH-001).
 */
class ModulithVerificationTest {

    static final ApplicationModules modules = ApplicationModules.of(EEstoqueApplication.class);

    @Test
    void verifiesModuleBoundaries() {
        modules.verify();
    }
}
