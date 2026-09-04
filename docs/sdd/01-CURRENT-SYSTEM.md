# 01 — CURRENT SYSTEM (e-Estoque-API .NET 8)

## Visão geral

API REST de gestão de estoque/vendas multi-contexto, ASP.NET Core 8, Clean Architecture (API → Application → Core/Domain ← Infrastructure), MediatR (CQRS), FluentValidation (entidades), EF Core 8 + Npgsql/PostgreSQL, Keycloak (OIDC, JWT bearer), RabbitMQ (producer, topic exchanges), OData v4 + Gridify (consultas), observabilidade com Serilog→Loki, OpenTelemetry→Tempo/Jaeger e Prometheus.

## Fluxo de request

```
HTTP → CORS → ExceptionMiddleware → Autenticação JWT (Keycloak; audience e-estoque-client)
     → Autorização (roles: escritas exigem realm role "Create")
     → Controller (fino) → MediatR → Command/Query Handler
     → Entidade (Create/Update/Validate com FluentValidation)
     → Repository (EF Core) → SaveChanges
     → IMessageBusClient.Publish(evento, routingKey=dash-case, exchange={context}-service | noticiation-service)
```

## Comportamentos relevantes comprovados por código/testes

1. **Envelope de resposta**: `{ success, message, data?, pagedResult? }` (camelCase), exceto Auth (JSON snake_case do Keycloak) e OData (formato OData).
2. **Status codes**: POST/PUT/DELETE/GET → 200; POST de Category → 201 com Location; validação de entidade → 400 `{"error": "<mensagens joined por vírgula>"}`; não encontrado → 404 `{"error": ...}`; sem token → 401 (framework); sem role → 403 (framework); exceção genérica → 500.
3. **Pesquisa** (`GET /api/{Entity}`): filtros de igualdade exata por campo (aplicados só quando != default/empty), `Order` whitelist (default: OrderBy Id), `PageIndex` (default 1), `PageSize` (default 10), resultado `BaseResultList` com `PagedResult` (CurrentPage, PageCount, PageSize, RowCount, FirstRowOnPage, LastRowOnPage). Sem token: 401.
4. **Gridify** (`GET /api/{Entity}/gridify`): `filter` (sintaxe Gridify), `orderBy`, `page` (1), `pageSize` (10) com includes completos.
5. **OData** (`/odata/{Entities}`): Select/Filter/OrderBy/Expand/Count, MaxTop 1000, MaxExpansionDepth 10; `$count`; GET por key `({key})`.
6. **Soft delete**: somente Sale (`DisableAsync`); demais entidades: hard delete.
7. **Eventos**: publicados após `SaveChangesAsync` (sem outbox, sem transação com o broker); exchanges topic duráveis criadas no publish; payload JSON camelCase do objeto de evento; routing key = nome da classe em dash-case (ex.: `product-created`).
8. **Notificações de erro**: `NotificationError` publicado nos exchanges `noticiation-service`/`notification-service` (ortografia inconsistente no .NET) antes de lançar ValidationException/NotFoundException.
9. **Auth**: proxy para Keycloak — login via password grant; refresh via refresh_token grant; register via Admin API (client_credentials) criando usuário com atributo `client` e depois login; resposta pass-through snake_case.
10. **Datas**: `DateTime.Now` (horário local do servidor) persistido em `timestamp without time zone`.
11. **Referências**: validação de existência de Category/Company (Product), Customer/Products (Sale), Product (Inventory), Category (Tax) — sempre APÓS criar/validar a entidade, ANTES de persistir.
12. **Autorização**: `[Authorize]` em todos os controllers REST (inclusive queries); `[Authorize(Roles="Create")]` em POST/PUT/DELETE; OData `[Authorize]` (sem role). Roles vêm de `realm_access.roles`.

## Limitações conhecidas (documentadas, não corrigidas silenciosamente)

- Sem multi-tenancy/isolamento por Company.
- Sem outbox/retry/idempotência no RabbitMQ; sem consumers.
- Sem Redis na aplicação (apenas provisionado).
- `Update*Command` de Product/Company/Customer/Inventory/Tax ignora o `id` da rota (exceto Sales, que sobrescreve `command.Id = id`).
- Venda não valida estoque nem decrementa inventário.
- `inventories.DateOrder` é `varchar(80)` no banco (DateTime serializado como texto).
