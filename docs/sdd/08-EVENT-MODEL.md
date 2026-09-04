# 08 — EVENT MODEL

## Fatos publicados (Domain Events, payload = registro do evento em camelCase)

| Evento | Exchange (topic, durable) | Routing key (dash-case) |
|---|---|---|
| ProductCreated / ProductUpdated | `product-service` | `product-created` / `product-updated` |
| CategoryCreated / CategoryUpdated | `category-service` | `category-created` / `category-updated` |
| CompanyCreated / CompanyUpdated | `company-service` | `company-created` / `company-updated` |
| CustomerCreated / CustomerUpdated | `customer-service` | `customer-created` / `customer-updated` |
| InventoryCreated / InventoryUpdated | `inventory-service` | `inventory-created` / `inventory-updated` |
| SaleCreated / SaleUpdated | `sale-service` | `sale-created` / `sale-updated` |
| TaxCreated / TaxUpdated | `tax-service` | `tax-created` / `tax-updated` |
| NotificationError (erros de negócio) | `notification-service` | `notification-error` |

Convenção de routing key = nome da classe em dash-case (`ToDashCase`).

## Payloads (camelCase, sem nulls no broker)

```json
// ProductCreated
{"id":"…","name":"…","description":"…","shortDescription":"…","price":10.5,"weight":1.0,"height":2.0,"length":3.0,"idCategory":"…","idCompany":"…"}

// SaleCreated (products = objetos Product completos)
{"id":"…","quantity":2,"totalPrice":21.0,"totalTax":2.1,"saleType":1,"paymentType":1,
 "deliveryDate":"2026-09-04T12:00:00","saleDate":"2026-09-04T12:00:00","paymentDate":"2026-09-04T12:00:00",
 "idCustomer":"…","products":[{"id":"…","name":"…", …}]}

// NotificationError
{"message":"Category not found","details":"Category not found"}
```

## Fluxo Java (Transactional Outbox — ADR-012)

```
Aggregate (lista domain events)
  → handler persiste agregado + OutboxEvent (mesma transação)
  → @TransactionalEventListener(AFTER_COMMIT) / dispatcher assíncrono
  → converte DomainEvent → IntegrationEvent (JSON camelCase idêntico)
  → Spring AMQP publish (exchange topic declarada durable)
  → retry com backoff; falhas permanentes → dead-letter de outbox + log
```

- Publicação **nunca antes do commit**.
- Wire-format idêntico ao .NET (mesmo JSON, mesmas exchanges/routing keys) — consumers externos não quebram.
- Diferença semântica: .NET = at-most-once sem transação; Java = at-least-once pós-commit (consumers devem tolerar duplicatas; documentado).
- Idempotência de consumo: fora de escopo (não há consumers no sistema original); `eventId` incluído no envelope de outbox para auditoria.
