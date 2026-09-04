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

## ACHADOS DA VALIDAÇÃO AO VIVO (Docker WSL + Postgres/Rabbit/Redis/Keycloak reais — 2026-09-04)

| ID | Tipo | Achado e correção |
|---|---|---|
| MD-14 | BUG-FIX | Flyway 12 exige `flyway-database-postgresql` explícito (Boot 4 não traz o módulo); sem ele: "Unsupported Database: PostgreSQL 17" |
| MD-15 | BUG-FIX | Naming strategy default do Boot transformava @Column explícitos em snake_case → `PhysicalNamingStrategyStandardImpl` + `globally_quoted_identifiers` (schema PascalCase .NET exige aspas em runtime) |
| MD-16 | BUG-FIX | Nomes de entidade Hibernate duplicados entre módulos (snapshots read-only) → `@Entity(name=...)` únicos |
| MD-17 | BUG-FIX | `Instant` → coluna `timestamp without time zone`: `hibernate.type.preferred_instant_jdbc_type=TIMESTAMP` |
| MD-18 | BUG-FIX | Boot 4: `RestClient.Builder` sem auto-config no starter webmvc → construção direta no HttpKeycloakClient |
| MD-19 | BUG-FIX | Jackson: conversor web do Boot 4 (Jackson 3) posicionado primeiro via `extendMessageConverters` (Instant ISO-8601); cache Redis com serializer jsr310 + `transactionAware` (evict pós-commit evita recache stale no PUT) |
| MD-20 | INTENTIONAL-DESIGN | Constraints Bean Validation removidas dos commands: validação 100% no domínio, preservando o 400 com mensagens exatas do .NET (o @Size localizado gerava 500/pt-BR — quebra de contrato) |
| MD-21 | FRAMEWORK-DIFFERENCE | `/odata/{E}({key})`: PathPattern não permite variável no meio do segmento → filtro de rewrite `/odata/{E}({key})` → `/odata/{E}/{key}`; `$filter`/`$orderby` traduzidos por parser próprio (ADR-011) |
| MD-22 | FRAMEWORK-DIFFERENCE | `/actuator/prometheus` público (o scrape do .NET também era) |

Resultado da bateria ao vivo (todos via curl): 401/403/201+Location/200/400 mensagens .NET/404 paridade; login/refresh/register (Keycloak real, snake_case); Company→Product→Inventory→Sale com referências e view models aninhados; soft delete de Sale confirmado no banco (`IsDeleted=t`); hard delete dos demais; gridify (`==`, `*=`, orderBy) e OData (`contains`, `eq`, `$orderby`, `$top`, `$count`, key por parênteses); outbox 16/16 PUBLISHED; exchanges topic criadas no RabbitMQ; entrada de cache presente no Redis; prometheus com 431 linhas de métricas.
