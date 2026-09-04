# ASSUMPTIONS

Registro de ambiguidades e a opção conservadora adotada (regra de precedência do master prompt §2).

| # | Ambiguidade | Decisão conservadora |
|---|---|---|
| A-01 | Redis não aparece no código .NET (só no compose/env var) | Provisionar Redis e usar Spring Cache de forma seletiva e segura (GetById de Categories/Companies/Customers/Taxes com evict em escrita; nada de dados críticos de venda/estoque). ADR-009. |
| A-02 | Exchanges `noticiation-service` (typo) e `notification-service` coexistem no .NET | Padronizar `notification-service` (BUG-FIX documentado). Consumers externos que escutavam o typo devem migrar. |
| A-03 | `IRepository.RemoveAsync` (hard) vs `DisableAsync` (soft) — handlers escolhem | Preservar exatamente: Sale→soft; todos os outros→hard. |
| A-04 | `Sale.DeliveryDate/PaymentDate` são nullable na entidade mas obrigatórios na validação e NOT NULL no banco | Tratar como obrigatórios (contrato do banco + validação). JSON serializa como não-null. |
| A-05 | `Include("SaleProduct")` no SaleRepository .NET usa nome de navegação que não existe (`SaleProducts` é o real) | Intent = carregar SaleProducts + Product; replicar o intent (fetch join correto). |
| A-06 | Ordem dos filtros no CreateProduct (valida entidade antes de checar referências) | Preservada (mensagens/ordem de erro idênticas). |
| A-07 | `Tax` create não checa existência de Category no handler .NET (só FK no banco) | Parity: não checar (FK 500/23503 caso inexistente). |
| A-08 | `.NET` publica eventos após save sem transação | Java usa Outbox pós-commit com mesmo wire-format (ADR-012). Semântica at-least-once. |
| A-09 | Datas de eventos/payloads .NET usam `DateTime.Now` local; coluna é naive | Java grava/lê UTC (ISO-8601) na mesma coluna naive; payloads de evento serializam Instant ISO-8601 UTC. Documentado como INTENTIONAL-DESIGN (UTC como fonte de verdade). |
| A-10 | `SearchSaleQuery.SaleType`/`PaymentType` recebem string; helper converte com defaults (Pix/Unitary) quando inválido | Replicado (StringToEnum com default). |
| A-11 | OData MaxTop=1000, MaxExpansionDepth=10, rotas `/odata/{Entities}` | Replicado no adapter (subset). |
| A-12 | Token do Keycloak: validação de audience `e-estoque-client` | Replicada via issuer-uri + audience validator. |
| A-13 | `Customers.Email` varchar(80) no banco (validação aceita 250) | Schema mantido 80 (parity); validação mantém 250 — overflow retorna erro de banco como no .NET. |
| A-14 | `Products.Name` é `text` no banco, mas validação 3–250 | Schema `text`; validação 3–250. |
| A-15 | Casing JSON: .NET System.Text.Json web defaults = camelCase, nulls incluídos | Jackson com `PropertyNamingStrategies.LOWER_CAMEL_CASE` (default) e nulls incluídos (NON_NULL apenas no publisher AMQP). |
| A-16 | DELETE retorna `200 {}` (Unit serializado) | Java: 200 com corpo `{}`. |
| A-17 | Frontend espera `response.data.data` | Envelope `{data, success, message, pagedResult?}` preservado. |
| A-18 | Load tests (k6) do repo .NET fora de escopo | Não migrados. |
