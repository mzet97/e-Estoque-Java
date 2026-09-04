# ADR-005 — PostgreSQL + Spring Data JPA

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** EF Core + Npgsql no .NET; banco existente deve continuar válido.
**Decisão:** Spring Data JPA + Hibernate + HikariCP; `ddl-auto: validate`; fetch joins/EntityGraph para os includes do .NET (evitar N+1); BigDecimal para decimal; Instant UTC para timestamps; conversor para o quirk `inventories.dateOrder` varchar.
**Consequências:** Parity de schema obrigatória (DATABASE-PARITY.md); queries de busca via Specifications.
