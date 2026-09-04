# 12 — TRACEABILITY MATRIX

Atualizada a cada slice concluída. Test types: U=unit, W=web (MockMvc), I=integration (Testcontainers), A=architecture.

| Requirement | Domain | Application | Controller (web) | Persistence | Tests | Status |
|---|---|---|---|---|---|---|
| FR-AUTH-001 | — | identity/RegisterUserUseCase | AuthController | — (Keycloak) | U,W,I | ⏳ |
| FR-AUTH-002 | — | identity/LoginUserUseCase | AuthController | — | U,W,I | ⏳ |
| FR-AUTH-003 | — | identity/RefreshTokenUseCase | AuthController | — | U,W,I | ⏳ |
| FR-AUTH-004 | — | identity/SystemLoginUseCase | (interno) | — | U,I | ⏳ |
| FR-CAT-001..006 | product.domain.Category | *Category handlers | CategoryController | CategoryJpa | U,W,I,A | ⏳ |
| FR-PROD-001..006 | product.domain.Product | *Product handlers | ProductController | ProductJpa | U,W,I,A | ⏳ |
| FR-COMPANY-001..006 | company.domain.Company | *Company handlers | CompanyController | CompanyJpa | U,W,I,A | ⏳ |
| FR-CUSTOMER-001..006 | customer.domain.Customer | *Customer handlers | CustomerController | CustomerJpa | U,W,I,A | ⏳ |
| FR-INV-001..006 | inventory.domain.Inventory | *Inventory handlers | InventoryController | InventoryJpa | U,W,I,A | ⏳ |
| FR-TAX-001..006 | tax.domain.Tax | *Tax handlers | TaxController | TaxJpa | U,W,I,A | ⏳ |
| FR-SALE-001..006 | sales.domain.Sale/SaleProduct | *Sale handlers | SaleController | SaleJpa | U,W,I,A | ⏳ |
| FR-ODATA-001..007 | — | shared.odata adapters | OData*Controller | repositories | W,I | ⏳ |
| FR-HTTP-001/002 | shared envelope | — | ApiExceptionHandler, health | — | W,I | ⏳ |
| NFR-SEC-001..003 | — | — | SecurityConfiguration | — | W,I | ⏳ |
| NFR-DATA-001/002/003 | — | — | — | Flyway V1, converters | I | ⏳ |
| NFR-REL-001 | events | outbox dispatcher | — | outbox table | I | ⏳ |
| NFR-OBS-001 | — | — | actuator | — | W | ⏳ |
| NFR-ARCH-001/002 | — | — | — | — | A (Modulith/ArchUnit) | ⏳ |

Legend: ⏳ pendente · 🚧 em progresso · ✅ implementado e verificado.
