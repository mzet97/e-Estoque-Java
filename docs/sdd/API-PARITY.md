# API PARITY MATRIX

Preenchida ao final de cada slice; estado inicial abaixo. "Test" = nível mínimo de teste que comprova a linha.

| ID | .NET endpoint | Java endpoint | Auth | Request | Response | Test | Status |
|---|---|---|---|---|---|---|---|
| API-AUTH-001 | POST /api/Auth/register | idem | anon | RegisterUserCommand | Token snake_case | I | ⏳ |
| API-AUTH-002 | POST /api/Auth/login | idem | anon | LoginUserCommand | Token snake_case | I | ⏳ |
| API-AUTH-003 | POST /api/Auth/refresh_token | idem | anon | RefreshTokenCommand | Token snake_case | I | ⏳ |
| API-CAT-001 | GET /api/Categories | idem | JWT | query | BaseResultList | I | ⏳ |
| API-CAT-002 | GET /api/Categories/gridify | idem | JWT | gridify | BaseResultList | I | ⏳ |
| API-CAT-003 | GET /api/Categories/{id} | idem | JWT | — | BaseResult | I | ⏳ |
| API-CAT-004 | POST /api/Categories | idem | JWT+Create | Create | 201+Location | I | ⏳ |
| API-CAT-005 | PUT /api/Categories/{id} | idem | JWT+Create | Update | BaseResult | I | ⏳ |
| API-CAT-006 | DELETE /api/Categories/{id} | idem | JWT+Create | — | {} | I | ⏳ |
| API-PROD-001..006 | /api/Products… | idem | idem | idem | idem | I | ⏳ |
| API-COMP-001..006 | /api/Companies… | idem | idem | idem | idem | I | ⏳ |
| API-CUST-001..006 | /api/Customers… | idem | idem | idem | idem | I | ⏳ |
| API-INV-001..006 | /api/Inventories… | idem | idem | idem | idem | I | ⏳ |
| API-TAX-001..006 | /api/Taxs… | idem | idem | idem | idem | I | ⏳ |
| API-SALE-001..006 | /api/Sales… | idem | idem | idem | idem | I | ⏳ |
| API-ODATA-001..007 | /odata/{Entities}… | idem (subset adapter) | JWT | $filter… | JSON | I | ⏳ |
| API-INFRA-001 | GET /health | /health (compat) | anon | — | UP | W | ⏳ |

Nenhum endpoint será removido sem justificativa em MIGRATION-DIFFERENCES.
