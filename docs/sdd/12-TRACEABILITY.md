# 12 — TRACEABILITY MATRIX

Atualizada ao fim da migração. Test types: U=unit, W=web (MockMvc), I=integration (Testcontainers), A=architecture.

| Requirement | Domain | Application | Controller (web) | Persistence | Tests | Status |
|---|---|---|---|---|---|---|
| FR-AUTH-001 | — | identity/RegisterUserHandler | AuthController | — (Keycloak) | U,W | ✅ |
| FR-AUTH-002 | — | identity/LoginUserHandler | AuthController | — | U,W | ✅ |
| FR-AUTH-003 | — | identity/RefreshTokenHandler | AuthController | — | U,W | ✅ |
| FR-AUTH-004 | — | identity/SystemLoginHandler | (interno) | — | U | ✅ |
| FR-CAT-001..006 | product.domain.Category | Category handlers + Gridify | CategoryController | CategoryJpa | U,W,A | ✅ |
| FR-PROD-001..006 | product.domain.Product | Product handlers + Gridify | ProductController | ProductJpa | U,W,A | ✅ |
| FR-COMPANY-001..006 | company.domain.Company | Company handlers + Gridify | CompanyController | CompanyJpa | U,A | ✅ |
| FR-CUSTOMER-001..006 | customer.domain.Customer | Customer handlers + Gridify | CustomerController | CustomerJpa | U,A | ✅ |
| FR-INV-001..006 | inventory.domain.Inventory | Inventory handlers + Gridify | InventoryController | InventoryJpa | U,A | ✅ |
| FR-TAX-001..006 | tax.domain.Tax | Tax handlers + Gridify | TaxController | TaxJpa | U,A | ✅ |
| FR-SALE-001..006 | sales.domain.Sale/SaleProduct | Sale handlers + Gridify | SaleController | SaleJpa | U,A | ✅ |
| FR-ODATA-001..007 | — | shared OData parsers | OData*Controller | repositories | A | ✅ |
| FR-HTTP-001/002 | shared envelope | — | ApiExceptionHandler, HealthController | — | W | ✅ |
| NFR-SEC-001..003 | — | SecurityConfiguration, converter | — | — | W,U | ✅ |
| NFR-DATA-001/002/003 | — | — | — | Flyway V1+V2, converters | I | ✅ |
| NFR-REL-001 | events | OutboxEventPublisher/Dispatcher | — | outbox_events | U,I | ✅ |
| NFR-OBS-001 | — | — | actuator + OTLP | — | config | ✅ |
| NFR-ARCH-001/002 | — | — | — | — | A (Modulith verify) | ✅ |

Cobertura executada localmente (sem Docker): 38 testes (unit + web + Modulith verify) — `./mvnw test` PASS. Testes de integração (Testcontainers) exigem Docker e são habilitados em CI com `-Dintegration=true`.
