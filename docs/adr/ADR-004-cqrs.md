# ADR-004 — CQRS interno (sem MediatR)

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** .NET usa MediatR com pipeline behaviors (validation, unhandled exception).
**Decisão:** `Command<R>`/`Query<R>` sealed interfaces + handlers como beans Spring; `CommandBus`/`QueryBus` mínimos (despacho por tipo); controllers finos; transações nos handlers; validação de entrada via Bean Validation nos requests e invariantes no domínio.
**Consequências:** Sem dependência externa; fácil evoluir para mediators assíncronos; behaviors replicados via advice/interceptors.
