# ADR-009 — Redis / Spring Cache

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** .NET provisiona Redis no compose mas não o usa. Master prompt pede Spring Cache + Redis sem arriscar stale em dados críticos.
**Decisão:** Spring Cache com Redis: cache `reference:{entity}:{id}` somente para leituras por id de Categories/Companies/Customers/Taxes (referências estáveis), TTL 15 min, `@CacheEvict` em update/delete. NADA de Product/Sale/Inventory (dados de negócio sensíveis). `CacheErrorHandler` que degrada para banco quando Redis indisponível.
**Consequências:** Redis é funcional (health/parity) sem risco de incorreção; desligável por config (`spring.cache.type=none` em prod conservador).
