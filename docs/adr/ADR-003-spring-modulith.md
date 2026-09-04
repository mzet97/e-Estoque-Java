# ADR-003 — Spring Modulith 2.1.1

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** Modular monolith com bounded contexts claros e possibilidade futura de extrair serviços.
**Decisão:** Spring Modulith 2.1.1: `ApplicationModules.of(EEstoqueApplication.class).verify()` falha o build em violação; módulos = bounded contexts (product, inventory, sales, customer, company, tax, identity, shared); documentação de módulos gerada em teste; comunicação entre módulos por eventos de aplicação.
**Consequências:** Pacotes internos não podem ser importados cross-module; eventos verificados por `@ApplicationModuleTest`.
