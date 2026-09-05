# ADR-004 — CQRS interno (sem MediatR)

**Status:** Aceito · **Data:** 2026-09-04 · **Emendado:** 2026-09-05

**Contexto:** .NET usa MediatR com pipeline behaviors (validation, unhandled exception).
**Decisão:** `Command<R>`/`Query<R>` marker interfaces + handlers como beans Spring; controllers finos; transações nos handlers; validação de entrada via Bean Validation nos requests e invariantes no domínio.
**Consequências:** Sem dependência externa; fácil evoluir para mediators assíncronos; behaviors replicados via advice/interceptors.

---

## Emenda (2026-09-05): injeção direta dos handlers, sem bus

**Contexto da emenda:** O `CommandBus`/`QueryBus` internos (despacho por `Map<Class, handler>`) eram maquinaria feita à mão sem análogo idiomático no Spring. Com `commandType()`/`queryType()` nos handlers, era código morto assim que os controllers passaram a conhecer os handlers concretos.

**Decisão emendada:** Os controllers injetam os **handlers concretos** diretamente (`CreateCategoryHandler`, `ProductHandlers.SearchProductsHandler`, ...) e chamam `handle(...)` — o padrão idiomático Spring. `CommandBus`, `QueryBus` e os métodos de registro foram **removidos**; as interfaces `CommandHandler`/`QueryHandler` reduzem-se a `handle()`. Regras ArchUnit (`ArchitectureTest`) fixam o desenho: web não bypassa application; application não depende de web/persistência; beans de application terminam em `Handler`.

**Consequências:** Menos indireção e menos código; compilador garante o vínculo command→handler; perde-se o ponto único de dispatch (nenhum behavior o usava — cross-cutting continua em `ApiExceptionHandler` e anotações declarativas).
