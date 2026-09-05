# 11 — MIGRATION PLAN (vertical slices)

Loop por slice: SPEC → TEST → IMPLEMENT → VERIFY (`./mvnw test`) → REFACTOR → DOCUMENT → COMMIT.

| Slice | Escopo | Entregáveis |
|---|---|---|
| 1 | Bootstrap + shared kernel | pom (Boot 4.1.1, Java 25), wrapper, Modulith verify, shared.domain/application (Entity, eventos, exceções, envelope, PagedResult), CI base |
| 2 | Security + Company | resource server, roles converter, Company vertical completa, Auth proxy (Keycloak), tenant behavior tests |
| 3 | Categories | CRUD+search+gridify, eventos, testes parity |
| 4 | Products | CRUD+search+gridify, referências Category/Company |
| 5 | Inventory | CRUD, referência Product |
| 6 | Customers | CRUD |
| 7 | Taxes | CRUD |
| 8 | Sales | Sale+SaleProduct, soft delete, referências |
| 9 | Events/RabbitMQ + Registry | tabela "EVENT_PUBLICATION" (Flyway V2), Event Publication Registry do Modulith + externalização AMQP, wire-format parity, resubmission |
| 10 | Cache Redis | Spring Cache seletivo + fallback |
| 11 | OData/Gridify compat | adapters de filtering (subset OData v4 + Gridify syntax) |
| 12 | Observability | actuator, micrometer tracing, structured logs, métricas de negócio |
| 13 | Docker/CI | Dockerfile multi-stage, compose completo, GitHub Actions |
| 14 | Full parity validation | API-PARITY.md final, relatório, frontend smoke |

Ordem deliberada: cada slice atravessa HTTP→Application→Domain→Persistence→Test.
