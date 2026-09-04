# ADR-008 — Virtual threads

**Status:** Aceito (com ressalvas) · **Data:** 2026-09-04

**Contexto:** Stack servlet/JPA bloqueante; Java 25.
**Decisão:** `spring.threads.virtual.enabled=true` em dev/test; carga padrão do produto é CRUD com poucas consultas por request; pools externos (Hikari) dimensionados; AMQP listener e outbox dispatcher em threads de plataforma dedicadas.
**Consequências:** Sem `synchronized` em hot paths do próprio código; monitorar pinning via JFR em benchmarks; reversível por configuração.
