# ADR-011 — OData/Gridify via adapters (Spring-first)

**Status:** Aceito · **Data:** 2026-09-04 · **Emendado:** 2026-09-05

**Contexto:** .NET expõe /odata/* (OData v4, MaxTop 1000) e /api/*/gridify (Gridify). Frontend usa builder OData (contains/eq/ne/gt/ge/lt/le/startswith/endswith + sort) e o frontend/grades consumem filtros. Substituir sem análise quebraria clientes.
**Decisão:** Não adotar Olingo nem portar Gridify: implementar na camada web (shared) um tradutor de query → JPA Specifications cobrindo o subconjunto EFETIVAMENTE usado:
- OData: $filter (eq, ne, gt, ge, lt, le, contains, startswith, endswith, and, or), $orderby, $select (projeção via Jackson), $top, $skip, $count, $expand de 1 nível para navegações mapeadas; maxTop 1000.
- Gridify: Filter (operadores =, !=, >, <, >=, <=, ^=, $=, *=, & e |), OrderBy ("field asc|desc, …"), Page, PageSize.
Exceção documentada à política Spring-first; domínio intocado; sem compat de metadados $metadata.
**Consequências:** Cobertura de operadores explícita e testada; operadores fora do subconjunto retornam 400 documentado.

**Emenda (2026-09-05):** os parsers (`GridifyRequestParser`, `ODataRequestParser`, `ODataFilterParser`) — puros, sem dependência de web — foram movidos para `shared.application.query`; a camada web mantém apenas o envelope de resposta OData (`ODataCollection`) e o filtro de rewrite de key-path. Regra fixada pelo ArchUnit: application não depende de infrastructure (MD-24).
