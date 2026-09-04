# 05 — API CONTRACT

Base: rotas `.NET` em `[Route("api/[controller]")]`; prefixo `/api` + nome do controller (PascalCase, plural conforme escrito, incluindo `Taxs`). Autenticação: `JWT` = bearer do realm `e-estoque` (audience `e-estoque-client`). Role `Create` exigida onde marcado. Envelope: `{ data, success, message, pagedResult? }` (camelCase). Erros: `{"error": "..."}`.

Colunas: ID | Method | Route | Auth | Query/Request | Response | Status | Regra | .NET Source | Java Target

## Auth (anônimo)

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-AUTH-001 | POST | /api/Auth/register | anon | RegisterUserCommand {username,password,confirmPassword,email,firstName,lastName} | Token JSON snake_case | 200/403 | FR-AUTH-001 | AuthController.Register | identity/AuthController |
| API-AUTH-002 | POST | /api/Auth/login | anon | {email,password} | Token JSON snake_case | 200/403 | FR-AUTH-002 | AuthController.Login | identity/AuthController |
| API-AUTH-003 | POST | /api/Auth/refresh_token | anon | {token} | Token JSON snake_case | 200/403 | FR-AUTH-003 | AuthController.RefreshToken | identity/AuthController |

## Categories

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-CAT-001 | GET | /api/Categories | JWT | query: name,description,shortDescription,id,createdAt,updatedAt,deletedAt,order,pageIndex,pageSize | BaseResultList<CategoryViewModel> | 200 | FR-CAT-005 | CategoriesController.Get | product/CategoryController |
| API-CAT-002 | GET | /api/Categories/gridify | JWT | filter,orderBy,page,pageSize | BaseResultList | 200 | FR-CAT-006 | CategoriesController.GetWithGridify | idem |
| API-CAT-003 | GET | /api/Categories/{id} | JWT | — | BaseResult<CategoryViewModel> | 200/404 | FR-CAT-004 | idem | idem |
| API-CAT-004 | POST | /api/Categories | JWT+Create | {name,description,shortDescription} | BaseResult<CategoryViewModel> + **201 + Location** | 201/400 | FR-CAT-001 | idem | idem |
| API-CAT-005 | PUT | /api/Categories/{id} | JWT+Create | corpo = update | BaseResult | 200/400/404 | FR-CAT-002 | idem | idem |
| API-CAT-006 | DELETE | /api/Categories/{id} | JWT+Create | — | `{}` | 200/404 | FR-CAT-003 (hard) | idem | idem |

## Products

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-PROD-001 | GET | /api/Products | JWT | name,description,shortDescription,price,weight,height,length,idCategory,idCompany,id,createdAt,updatedAt,deletedAt,order,pageIndex,pageSize | BaseResultList<ProductViewModel> | 200 | FR-PROD-005 | ProductsController | product/ProductController |
| API-PROD-002 | GET | /api/Products/gridify | JWT | filter,orderBy,page,pageSize | BaseResultList | 200 | FR-PROD-006 | idem | idem |
| API-PROD-003 | GET | /api/Products/{id} | JWT | — | BaseResult<ProductViewModel> (category, company+address) | 200/404 | FR-PROD-004 | idem | idem |
| API-PROD-004 | POST | /api/Products | JWT+Create | {name,description,shortDescription,price,weight,height,length,image,idCategory,idCompany} | BaseResult<ProductViewModel> | 200/404/400 | FR-PROD-001 | idem | idem |
| API-PROD-005 | PUT | /api/Products/{id} | JWT+Create | idem create | BaseResult | 200/404/400 | FR-PROD-002 | idem | idem |
| API-PROD-006 | DELETE | /api/Products/{id} | JWT+Create | — | `{}` | 200/404 | FR-PROD-003 (hard) | idem | idem |

## Companies

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-COMP-001 | GET | /api/Companies | JWT | name,docId,email,description,phoneNumber,id,createdAt,updatedAt,deletedAt,order,pageIndex,pageSize | BaseResultList<CompanyViewModel> | 200 | FR-COMPANY-005 | CompaniesController | company/CompanyController |
| API-COMP-002 | GET | /api/Companies/gridify | JWT | filter,orderBy,page,pageSize | BaseResultList | 200 | FR-COMPANY-006 | idem | idem |
| API-COMP-003 | GET | /api/Companies/{id} | JWT | — | BaseResult<CompanyViewModel> | 200/404 | FR-COMPANY-004 | idem | idem |
| API-COMP-004 | POST | /api/Companies | JWT+Create | {name,docId,email,description,phoneNumber,companyAddress{...10 campos}} | BaseResult | 200/400 | FR-COMPANY-001 | idem | idem |
| API-COMP-005 | PUT | /api/Companies/{id} | JWT+Create | idem | BaseResult | 200/404/400 | FR-COMPANY-002 | idem | idem |
| API-COMP-006 | DELETE | /api/Companies/{id} | JWT+Create | — | `{}` | 200/404 | FR-COMPANY-003 (hard) | idem | idem |

## Customers

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-CUST-001..006 | GET/POST/PUT/DELETE | /api/Customers[/gridify|/{id}] | idêntico a Companies | {…, customerAddress{…}} | idêntico | idêntico | FR-CUSTOMER-001..006 | CustomersController | customer/CustomerController |

## Inventories

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-INV-001 | GET | /api/Inventories | JWT | quantity,dateOrder,idProduct,id,createdAt,updatedAt,deletedAt,order,pageIndex,pageSize | BaseResultList<InventoryViewModel> | 200 | FR-INV-005 | InventoriesController | inventory/InventoryController |
| API-INV-002 | GET | /api/Inventories/gridify | JWT | filter,orderBy,page,pageSize | BaseResultList | 200 | FR-INV-006 | idem | idem |
| API-INV-003 | GET | /api/Inventories/{id} | JWT | — | BaseResult<InventoryViewModel> (product) | 200/404 | FR-INV-004 | idem | idem |
| API-INV-004 | POST | /api/Inventories | JWT+Create | {quantity,dateOrder,idProduct} | BaseResult | 200/404/400 | FR-INV-001 | idem | idem |
| API-INV-005 | PUT | /api/Inventories/{id} | JWT+Create | idem | BaseResult | 200/404/400 | FR-INV-002 | idem | idem |
| API-INV-006 | DELETE | /api/Inventories/{id} | JWT+Create | — | `{}` | 200/404 | FR-INV-003 (hard) | idem | idem |

## Taxes (rota mantida `Taxs`)

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-TAX-001 | GET | /api/Taxs | JWT | name,description,percentage,idCategory,id,createdAt,updatedAt,deletedAt,order,pageIndex,pageSize | BaseResultList<TaxViewModel> | 200 | FR-TAX-005 | TaxsController | tax/TaxController |
| API-TAX-002 | GET | /api/Taxs/gridify | JWT | filter,orderBy,page,pageSize | BaseResultList | 200 | FR-TAX-006 | idem | idem |
| API-TAX-003 | GET | /api/Taxs/{id} | JWT | — | BaseResult<TaxViewModel> (category) | 200/404 | FR-TAX-004 | idem | idem |
| API-TAX-004 | POST | /api/Taxs | JWT+Create | {name,description,percentage,idCategory} | BaseResult | 200/400 | FR-TAX-001 | idem | idem |
| API-TAX-005 | PUT | /api/Taxs/{id} | JWT+Create | idem | BaseResult | 200/404/400 | FR-TAX-002 | idem | idem |
| API-TAX-006 | DELETE | /api/Taxs/{id} | JWT+Create | — | `{}` | 200/404 | FR-TAX-003 (hard) | idem | idem |

## Sales

| ID | Method | Route | Auth | Request | Response | Status | Regra | .NET Source | Java Target |
|---|---|---|---|---|---|---|---|---|---|
| API-SALE-001 | GET | /api/Sales | JWT | quantity,totalPrice,totalTax,saleType,paymentType,deliveryDate,saleDate,paymentDate,idCustomer,idProduct,id,createdAt,updatedAt,deletedAt,order,pageIndex,pageSize | BaseResultList<SaleViewModel> | 200 | FR-SALE-005 | SalesController | sales/SaleController |
| API-SALE-002 | GET | /api/Sales/gridify | JWT | filter,orderBy,page,pageSize | BaseResultList | 200 | FR-SALE-006 | idem | idem |
| API-SALE-003 | GET | /api/Sales/{id} | JWT | — | BaseResult<SaleViewModel> (customer, products) | 200/404 | FR-SALE-004 | idem | idem |
| API-SALE-004 | POST | /api/Sales | JWT+Create | {quantity,totalPrice,totalTax,saleType,paymentType,deliveryDate,saleDate,paymentDate,idCustomer,idsProducts[]} | BaseResult<SaleViewModel> | 200/404/400 | FR-SALE-001 | idem | idem |
| API-SALE-005 | PUT | /api/Sales/{id} | JWT+Create | idem (**id da rota prevalece**) | BaseResult | 200/404/400 | FR-SALE-002 | idem | idem |
| API-SALE-006 | DELETE | /api/Sales/{id} | JWT+Create | — | `{}` | 200/404 | FR-SALE-003 (**soft**) | idem | idem |

## OData (JWT, sem role)

| ID | Route | Regra |
|---|---|---|
| API-ODATA-001..007 | `/odata/{Categories,Companies,Customers,Inventories,Products,Sales,Taxs}` + `/$count` + `/({key})` | $filter/$orderby/$select/$expand/$top/$skip/$count (maxTop 1000) |

## Utilitários

| ID | Route | Auth | Observação |
|---|---|---|---|
| API-INFRA-001 | GET /health | anon | compat: alias de health (usado por LB/compose) |
| API-INFRA-002 | GET /actuator/health, /actuator/info, /actuator/metrics, /actuator/prometheus | anon (health/info) ou restrito | Java-only (FRAMEWORK-DIFFERENCE) |
| API-INFRA-003 | /swagger-ui + /v3/api-docs | dev/test | Java-only |

**Total: 65 endpoints .NET (3 auth + 8×6 REST + 7×2 OData) + utilitários.**
