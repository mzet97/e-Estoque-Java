# API PARITY MATRIX

Status final da migração. "I" = integração Testcontainers executada em CI (exige Docker). "Test" = nível mínimo de teste que comprova a linha.

| ID | .NET endpoint | Java endpoint | Auth | Request | Response | Test | Status |
|---|---|---|---|---|---|---|---|
| API-AUTH-001 | POST /api/Auth/register | idem | anon | RegisterUserCommand | Token snake_case | I | ✅ (U/W/A; I em CI) |
| API-AUTH-002 | POST /api/Auth/login | idem | anon | LoginUserCommand | Token snake_case | I | ✅ (U/W/A; I em CI) |
| API-AUTH-003 | POST /api/Auth/refresh_token | idem | anon | RefreshTokenCommand | Token snake_case | I | ✅ (U/W/A; I em CI) |
| API-CAT-001 | GET /api/Categories | idem | JWT | query | BaseResultList | I | ✅ (U/W/A; I em CI) |
| API-CAT-002 | GET /api/Categories/gridify | idem | JWT | gridify | BaseResultList | I | ✅ (U/W/A; I em CI) |
| API-CAT-003 | GET /api/Categories/{id} | idem | JWT | — | BaseResult | I | ✅ (U/W/A; I em CI) |
| API-CAT-004 | POST /api/Categories | idem | JWT+Create | Create | 201+Location | I | ✅ (U/W/A; I em CI) |
| API-CAT-005 | PUT /api/Categories/{id} | idem | JWT+Create | Update | BaseResult | I | ✅ (U/W/A; I em CI) |
| API-CAT-006 | DELETE /api/Categories/{id} | idem | JWT+Create | — | {} | I | ✅ (U/W/A; I em CI) |
| API-PROD-001..006 | /api/Products… | idem | idem | idem | idem | I | ✅ (U/W/A; I em CI) |
| API-COMP-001..006 | /api/Companies… | idem | idem | idem | idem | I | ✅ (U/W/A; I em CI) |
| API-CUST-001..006 | /api/Customers… | idem | idem | idem | idem | I | ✅ (U/W/A; I em CI) |
| API-INV-001..006 | /api/Inventories… | idem | idem | idem | idem | I | ✅ (U/W/A; I em CI) |
| API-TAX-001..006 | /api/Taxs… | idem | idem | idem | idem | I | ✅ (U/W/A; I em CI) |
| API-SALE-001..006 | /api/Sales… | idem | idem | idem | idem | I | ✅ (U/W/A; I em CI) |
| API-ODATA-001..007 | /odata/{Entities}… | idem (subset adapter) | JWT | $filter… | JSON | I | ✅ (U/W/A; I em CI) |
| API-INFRA-001 | GET /health | /health (compat) | anon | — | UP | W | ✅ (U/W/A; I em CI) |

Nenhum endpoint será removido sem justificativa em MIGRATION-DIFFERENCES.
