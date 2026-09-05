# 10 — TEST PLAN

## Níveis

| Nível | Stack | Escopo | Execução |
|---|---|---|---|
| Unit | JUnit 5, Mockito, AssertJ, Instancio | domínio (invariantes, factories, eventos — dados aleatórios gerados), handlers com repositórios mockados, converters, mappers, gridify/odata parser | sempre |
| Web | MockMvc + `@WebMvcTest`/security config (handlers mockados com `@MockitoBean`) | contratos: rota, status, envelope, 401/403 | sempre |
| Integration | Testcontainers (PostgreSQL, RabbitMQ, Keycloak) + `@SpringBootTest` + MockMvc/TestRestTemplate | Controller→Application→JPA→PostgreSQL; registry→RabbitMQ; JWT real | CI/ambiente com Docker (`disabledWithoutDocker = true`) |
| Architecture | Spring Modulith `ApplicationModules.verify()` + ArchUnit (`ArchitectureTest`) | boundaries, dependências proibidas (domínio/application sem web-persistência; web não bypassa application; injeção por construtor) | sempre |
| Security | MockMvc + JWTs emitidos por Keycloak container | 401/403/role Create/tenant behavior | integration |
| Concurrency | integração | deletes concorrentes, idempotência/resubmission do registry | integration |

## Matriz por requisito (resumo — detalhe em 12-TRACEABILITY)

- Cada FR tem ≥1 unit test (handler/domínio) e ≥1 web/integration test por endpoint.
- Parity: testes de integração replicam casos dos testes xUnit .NET (Bogus seeds → fixtures determinísticas).

## Casos de segurança obrigatórios

1. sem token → 401; 2. token válido sem role Create em POST → 403; 3. token com Create → 200/201; 4. JWT inválido/expirado → 401; 5. `/odata/**` sem token → 401; 6. `/health` anônimo → 200.

## Casos de parity obrigatórios (por contexto)

- criar → 200 (201+Location para Category) e corpo do GetById;
- criar com referência inexistente → 404 com mensagem idêntica (`Category not found` etc.);
- criar com payload inválido → 400 `{"error":"<msgs joined>"}`;
- buscar inexistente → 404 `{"error":"Not found"}` (Product/Sale/…);
- delete Sale → IsDeleted=true e DeletedAt≠null; delete demais → linha removida;
- search: filtros aplicados, paginação (pageIndex/pageSize defaults 1/10), ordenação default Id;
- gridify: `filter=name==Foo`, `orderBy=name asc`, página;
- odata: `$filter`, `$orderby`, `$top/$skip`, `$count=true`.

## Cobertura

JaCoCo check: linha ≥80%, branch ≥70% (agregado). 100% dos caminhos em: validações de domínio, erros de handler, conversor de autoridades, publisher/registry de eventos.

## Decisão sobre ambientes sem Docker

`@Testcontainers(disabledWithoutDocker = true)` — execução condicional suportada oficialmente pelo Testcontainers; NÃO é `@Disabled` sem justificativa. CI executa com Docker e cobre 100% da suíte. Registrado como NFR-TEST-002.
