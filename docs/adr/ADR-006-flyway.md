# ADR-006 — Flyway

**Status:** Aceito · **Data:** 2026-09-04 · **Emendado:** 2026-09-05

**Contexto:** Substituir migrations EF Core (uma única migration 001).
**Decisão:** `V1__baseline.sql` reproduz o schema .NET (tabelas case-sensitive entre aspas, PK_/FK_/IX_ idênticos); `V2__event_publication.sql` cria a tabela `"EVENT_PUBLICATION"` do registry do Modulith (emenda do ADR-012); migrations imutáveis após publicadas. `.gitattributes` fixa LF nas migrations (checksum do Flyway é sensível a CRLF/LF).
**Consequências:** Bancos criados pelo .NET ou pelo Java ficam intercambiáveis (a tabela do registry é aditiva). Identificadores do V2 são **citados** (`"EVENT_PUBLICATION"`, colunas camelCase) porque `globally_quoted_identifiers` torna a validação Hibernate case-sensitive para a entidade do Modulith (MD-26).
