# 00 — SOURCE INVENTORY

Inventário do código real analisado na Fase 0 (Discovery). Caminhos relativos à raiz de cada repositório.

## Repositório primário — `mzet97/e-Estoque-API` (clone local: `D:\TI\git\e-Estoque\e-Estoque-API`)

Solution `e-Estoque-API.sln` com 4 projetos + 2 de teste.

### e-Estoque-API.API (ASP.NET Core 8)

| Categoria | Arquivos |
|---|---|
| Controllers REST | `AuthController`, `CategoriesController`, `CompaniesController`, `CustomersController`, `InventoriesController`, `ProductsController`, `SalesController`, `TaxsController`, `MainController` (CustomResponse) |
| Controllers OData | `OData/{Categories,Companies,Customers,Inventories,Products,Sales,Taxs}Controller` (ODataController, EnableQuery, MaxExpansionDepth=10) |
| Configuração | `CorsConfig` (Development: any; Production: GET https://mzet97.dev), `KeycloakConfig`, `KeycloakClaimsTransformation` (realm_access.roles → role claims), `OpenIdConfig` (WSO2 — desativado), `SwaggerConfig`, `ObservabilityConfig` (OTLP + Prometheus) |
| Extensões | `Extensions/Auth/HasScopeHandler` + `HasScopeRequirement` (WSO2 — desativado), `ExceptionMiddleware` (mapeia exceções → status HTTP), `CustomExceptionHandler` (IExceptionHandler — registrado? não, o middleware é usado) |
| Teste | `TestAuthenticationHandler` (ambiente Testing: roles "Create" e "User") |
| Bootstrap | `Program.cs` — Serilog+Loki, OData route "odata" (Select/Filter/OrderBy/Expand/Count, MaxTop 1000), audience `e-estoque-client`, CORS por ambiente, `/health`, Prometheus scraping endpoint |

### e-Estoque-API.Application (MediatR + FluentValidation)

| Contexto | Commands | Queries | ViewModels |
|---|---|---|---|
| Auth | `RegisterUserCommand`, `LoginUserCommand`, `RefreshTokenCommand`, `SystemLoginCommand` (+handlers; proxy HTTP Keycloak) | — | `TokenViewModel` (snake_case), `UserDto` |
| Categories | Create/Update/Delete | GetById, Search, Gridify | `CategoryViewModel` |
| Companies | Create/Update/Delete | GetById, Search, Gridify | `CompanyViewModel` |
| Customers | Create/Update/Delete (arquivos com nome `*Company*` — copy/paste do .NET) | GetById, Search, Gridify | `CustomerViewModel` |
| Inventories | Create/Update/Delete | GetById, Search, Gridify | `InventoryViewModel` |
| Products | Create/Update/Delete | GetById, Search, Gridify | `ProductViewModel` |
| Sales | Create/Update/Delete | GetById, Search, Gridify | `SaleViewModel` |
| Taxes | Create/Update/Delete | GetById, Search, Gridify | `TaxViewModel` |
| Common | `Behaviours/{ValidationBehaviour, UnhandledExceptionBehaviour, ForbiddenAccessException}`, `Notifications/{ErrorNotification, ErrorNotificationHandler}`, `GridifyExtensions`, `Extensions/ExtensionsConfig.ToDashCase` | | `Dtos/InputModels/{BaseSearch, GridifySearchQuery}`, `Dtos/ViewModels/BaseViewModel` |

### e-Estoque-API.Core (Domínio)

- `Entities/`: `Entity` (Id, CreatedAt, UpdatedAt, DeletedAt, IsDeleted, Events, Validate, Disabled/Activate/Update), `AggregateRoot`, `Category`, `Company`, `Customer`, `Inventory`, `Product` (AggregateRoot), `Sale` (AggregateRoot), `SaleProduct`, `Tax`.
- `Enums/`: `SaleType` (Unitary=1, Recurrent=2), `PaymentType` (Pix=1, Deposit=2, CreditCard=3, DebitCard=4) + helpers `FromString` (default Pix/Unitary).
- `Events/`: `IDomainEvent`, `DomainEvent` (base), `{Context}Created`/`{Context}Updated` para Categories, Companies, Customers, Inventories, Products, Sales, Taxes; `NotificationError(message, details)`.
- `Exceptions/`: `NotFoundException`, `ValidationException` (com `Errors` dict).
- `Models/`: `BaseResult<T>`, `BaseResultList<T>` (data + PagedResult), `PagedResult`, `Keycloak/Credentials`, `Wso2` (desativado).
- `Repositories/`: `IRepository<T>` (Add, GetById, GetAllQueryable, GetAll, Find, Update, Remove **hard delete**, Disable/Active **soft delete**, Search paginado, Count, Exists) + 8 interfaces.
- `Validations/`: FluentValidation por entidade (mensagens e limites exatos preservados — ver 04-DOMAIN-MODEL).
- `ValueObjects/`: `CompanyAddress`, `CustomerAddress` (records: Street, Number, Complement, Neighborhood, District, City, Country, ZipCode, Latitude, Longitude).

### e-Estoque-API.Infrastructure

- `Persistence/EstoqueDbContext` (8 DbSets; DeleteBehavior.ClientSetNull; schema public; Npgsql legacy timestamp).
- `Persistence/Mappings/` — 8 IEntityTypeConfiguration (inclui Owned `CompanyAddress`/`CustomerAddress` com colunas `*_County` para Country — typo do schema).
- `Persistence/Migrations/20250327000516_001.cs` — migration única com o schema completo (tabelas: `Categories`, `Companies`, `Customers`, `Taxs`, `Products`, `Sales`, `SaleProducts`, `inventories` [minúsculas]).
- `Persistence/Repositories/` — 8 repositórios; overrides de includes: Product (Category, Company.CompanyAddress), Category (Products, Taxs; GetAll filtra DeletedAt==null), Sale (Customer, SaleProducts, SaleProducts.Product), Tax (Category), Inventory (Product.Category/Company), SaleProduct (Product, Sale).
- `MessageBus/` — `IMessageBusClient.Publish(message, routingKey, exchange)`, `RabbitMqClient` (exchange topic durable declarada no publish; JSON camelCase; RabbitMQ.Client 6.8.1), `ProducerConnection` (singleton criado no startup).
- `Configuration/` — DbContextConfig (+healthcheck EF), DependencyInjectionConfig (repositórios scoped), MessageBusConfig.

### Testes .NET

- `UnitTest/` — xUnit + Moq + Bogus: handlers de Auth, Categories, Companies, Customers, Inventories, Products, Sales, Taxes (create/update/delete/get/search).
- `IntegrationTest/` — WebApplicationFactory (`TestWebApplicationFactory`), autenticação de teste, `MockMessageBusClient`, seed de dados (Bogus), testes de Categories, Inventories, Products, Auth.

### Infraestrutura do repositório

- `docker-compose.yml` — redis 7.2 (provisionado, **não usado pelo código**), postgres 17, pgadmin, rabbitmq 3.13-management, keycloak 24.0.2 (`--import-realm`), nginx LB + 3 réplicas da API, prometheus, grafana 11, jaeger, loki 3, tempo 2.5, mimir (SonarQube comentado).
- `Dockerfile` — multi-stage aspnet:8.0.
- `keycloak-config/realms/my-realm.json` — realm `e-estoque`; client confidencial `e-estoque-client` (directAccessGrants habilitado); realm roles: `admin`, `user`, `Create`, `Read`, `Update`, `Delete`; usuários: `admin` (admin, Create, Read, Update, Delete) e `usuario` (user, Read).
- `prometheus/`, `grafana/`, `nginx/` — provisionamento.
- `load-tests/` — k6/azure pipelines (não funcional para parity).

## Repositório secundário — `mzet97/e-Estoque` (clone local: `D:\TI\git\e-Estoque`)

- Frontend Next.js em `e-estoque-next/` (NextAuth + Keycloak; `externalToken` JWT enviado como Bearer via interceptor axios).
- `src/config/axiosInstance.ts` — baseURL `NEXT_PUBLIC_API_URL`, Bearer token, redirect 401.
- `src/types/ApiResponse.ts` — envelope esperado: `{ data: T; success: boolean; message: string; pagedResult?: { currentPage, pageCount, pageSize, rowCount, firstRowOnPage, lastRowOnPage } }`.
- Serviços por contexto (`src/app/dashboard/{category,company,customer,inventory,product,sale,...}/api/*.ts`) chamando `/Products`, `/Products/{id}` etc. e lendo `response.data.data`.
- `src/utils/odata.ts` — builder de query OData (contains, equals, startsWith, endswith, gt, ge, lt, le, ne, sort, top/skip) usado para as grades de dados.

## Conclusões de discovery

1. Não há Redis no código .NET (só no compose). Não há consumers RabbitMQ nesta API (só producer).
2. Não há multi-tenancy: `IdCompany` vem do payload; nenhuma filtragem por usuário autenticado.
3. `TotalPrice`/`TotalTax` da venda vêm do cliente; venda não baixa estoque; estoque é CRUD independente.
4. Delete: Product/Category/Company/Customer/Inventory/Tax → hard delete (`RemoveAsync`); Sale → soft delete (`DisableAsync`).
5. OData/Gridify adicionados recentemente (git log) — grades do frontend.
