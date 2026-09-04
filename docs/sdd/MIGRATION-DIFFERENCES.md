# MIGRATION DIFFERENCES

Diferenças intencionais entre a API .NET e a Java. Nenhuma breaking change de contrato externo.

| ID | Tipo | Diferença | Justificativa / Compat |
|---|---|---|---|
| MD-01 | BUG-FIX | `PUT /api/{Entity}/{id}` dos contextos Product, Company, Customer, Inventory, Tax ignoravam o `id` da rota e usavam `command.Id` do corpo (Sales já sobrescrevia) | Java sobrescreve `id` da rota no command para **todos** os contextos (comportamento seguro; para payloads normais é indistinguível). Teste dedicado. |
| MD-02 | BUG-FIX | Typo `noticiation-service` em parte dos handlers | Padronizado `notification-service`. |
| MD-03 | SECURITY-FIX | Keycloak secret commitado em `appsettings.Development.json` | Nenhum secret no repositório Java; `.env.example` + variáveis de ambiente. |
| MD-04 | FRAMEWORK-DIFFERENCE | Datas persistidas em UTC (`Instant`) em vez de `DateTime.Now` local | §14 do master prompt; coluna continua naive `timestamp`; leitura de bases .NET continua válida. |
| MD-05 | FRAMEWORK-DIFFERENCE | Eventos via Outbox pós-commit (at-least-once) em vez de publish pós-save sem transação | Mesma wire-format; consumidores devem ser idempotentes (fato já verdadeiro para retries de rede no .NET). |
| MD-06 | FRAMEWORK-DIFFERENCE | Health: `/actuator/health` + compat `/health`; Prometheus em `/actuator/prometheus` | Actuator substitui HealthChecks/MapPrometheusScrapingEndpoint; compose atualizado. |
| MD-07 | FRAMEWORK-DIFFERENCE | Swagger UI com springdoc (dev/test) em vez de Swashbuckle | Equivalente funcional. |
| MD-08 | INTENTIONAL-DESIGN | Command/Query handlers Java não usam MediatR; `CommandBus` interno mínimo | §15 — solução idiomática Spring. |
| MD-09 | INTENTIONAL-DESIGN | Nomes de classes Customer corrigidos (`CreateCustomerCommand` em vez de `CreateCompanyCommand` no pacote Customers) | Copy/paste do .NET; wire-format e rotas inalterados. |
| MD-10 | INTENTIONAL-DESIGN | Redis usado de fato (Spring Cache seletivo) | .NET provisionava Redis sem uso; ADR-009; nenhum dado crítico exposto a stale. |
| MD-11 | INTENTIONAL-DESIGN | Outbox adiciona `eventId`/`occurredAt`/`correlationId` no envelope interno de despacho (não no payload publicado) | Rastreabilidade; payload publicado permanece idêntico ao .NET. |
| MD-12 | FRAMEWORK-DIFFERENCE | OData: implementação como subset-adapters (web layer) em vez de biblioteca OData completa | ADR-011; cobre $filter/$orderby/$select/$top/$skip/$count/$expand básico usado pelo frontend; escopo explícito. |
| MD-13 | BUG-FIX | `Sale.DeliveryDate/PaymentDate` exigidos (não null) | Coerente com validação FluentValidation e NOT NULL do banco (o .NET permitia null na propriedade, mas validação falhava em runtime). |

## SECURITY GAP (documentado, não alterado)

| ID | Situação |
|---|---|
| SG-01 | Ausência de isolamento multi-tenant por Company no .NET (qualquer usuário autenticado lê/escreve qualquer Company id). Mantido por parity; isolamento completo é mudança de contrato (breaking) e exige decisão de produto. Hooks (AuthenticatedCompanyContext) preparados. Testes documentam o comportamento. |
