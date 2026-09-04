# ADR-001 — Java 25

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** Migração .NET 8 → Java moderna; master prompt exige Java 25.
**Decisão:** Java 25 (LTS, JDK Microsoft 25.0.4). `<java.version>25</java.version>`. Uso idiomático: records (DTOs/VOs), sealed interfaces (Command/Query/events), switch expressions, pattern matching, text blocks, `List.of`/`Map.of`, `Optional` só como retorno, `java.time`/`Instant`, UUID como identidade.
**Consequências:** Sem preview features; virtual threads avaliados em ADR-008; toolchain exige JDK 25 (Maven Enforcer).
