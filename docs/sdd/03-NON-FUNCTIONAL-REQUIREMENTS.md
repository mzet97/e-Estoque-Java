# 03 — NON-FUNCTIONAL REQUIREMENTS

### NFR-SEC-001 — Autenticação
Todas as rotas REST e OData exigem JWT do realm `e-estoque` (Keycloak), issuer `http://localhost:8080/realms/e-estoque`, audience `e-estoque-client`. `/health` e Swagger (perfis dev/test) anônimos. Implementação: Spring Security OAuth2 Resource Server (sem parser manual).

### NFR-SEC-002 — Autorização
Realm roles (`realm_access.roles`) mapeadas para authorities. POST/PUT/DELETE exigem authority `Create`. OData: apenas autenticado.

### NFR-SEC-003 — Secrets
Nenhum secret commitado. `.env.example` com placeholders; compose consome variáveis de ambiente. (O repo .NET commitou secret do Keycloak em appsettings.Development.json — não repetir.)

### NFR-SEC-004 — Multi-tenancy
O sistema original **não** isola dados por Company. Parity mantida; lacuna documentada em MIGRATION-DIFFERENCES (SECURITY-FIX candidato futuro, fora do escopo de parity).

### NFR-DATA-001 — Banco
PostgreSQL 17; schema idêntico ao da migration .NET (tabelas, colunas, tipos, índices, FKs, `*_County`); Flyway V1__baseline.sql equivalente; `ddl-auto: validate`.

### NFR-DATA-002 — Tempo
`Instant` em UTC para timestamps persistidos (correção documentada; schema permanece `timestamp without time zone`). Comparação de datas por igualdade preservada na semântica dos filtros.

### NFR-DATA-003 — Dinheiro
`BigDecimal` (nunca double); precisão do Postgres `decimal` (numeric sem escala definida) preservada; sem arredondamento no servidor (não há cálculo financeiro no servidor no sistema original).

### NFR-REL-001 — Eventos
Publicação pós-commit garantida (Transactional Outbox), mesma wire-format .NET (exchanges topic, routing key dash-case, JSON camelCase). Notificação de erros de negócio mantida.

### NFR-OBS-001 — Observabilidade
Actuator (health/info/metrics/prometheus), Micrometer Tracing + OTLP, logs estruturados com traceId/spanId, Loki-compatible. Prometheus scrape path equivalente.

### NFR-PERF-001 — JPA
Sem N+1 nos fluxos que o .NET resolve com includes (fetch joins/EntityGraph); paginação obrigatória com limite superior de `pageSize`.

### NFR-TEST-001 — Pirâmide
Unit (JUnit5+Mockito+AssertJ), web (MockMvc), integração (Testcontainers: PostgreSQL, RabbitMQ, Keycloak), arquitetura (Modulith verify), segurança (401/403/role/tenant-documented). JaCoCo: ≥80% linha, ≥70% branch em módulos de domínio/application.

### NFR-TEST-002 — Docker-less CI/local
Testcontainers com `@Testcontainers(disabledWithoutDocker = true)` — ambiente local sem Docker executa o restante da suíte; CI executa tudo. Justificativa formal registrada em 10-TEST-PLAN.

### NFR-ARCH-001 — Modularidade
Modular monolith Spring Modulith: product, inventory, sales, customer, company, tax, identity, shared. Domínio sem dependência de Spring Web/Jackson/AMQP. `ApplicationModules.verify()` no build.

### NFR-ARCH-002 — Compatibilidade de clientes
Cliente .NET existente deve funcionar com mudanças mínimas: mesmas rotas, casing camelCase, UUID, enums como int, envelope de resposta, mensagens de erro, status codes.
