# TECHNOLOGY BASELINE

> Data da especificação: 2026-09-04. Versões verificadas nas fontes oficiais na data da migração.

## Baseline obrigatória

| Tecnologia | Versão | Justificativa |
|---|---|---|
| Java | 25 (25.0.4 LTS instalado no ambiente) | Requisito do master prompt; JDK Microsoft OpenJDK 25.0.4.1 LTS |
| Spring Boot | **4.1.1** (GA 2026-08-21) | Última GA estável; 4.2 existe apenas como milestone (4.2.0-M1) — proibido |
| Spring Framework | 7.0.x (gerenciado pelo Boot 4.1.1) | Base do Boot 4.1 |
| Spring Modulith | **2.1.1** (GA, alinhado ao Boot 4.1.1) | Verificação de módulos e eventos |
| Maven | 3.9.x (wrapper 3.9.11) | Linha suportada pelo Boot 4 |
| springdoc-openapi | 3.1.0 | Linha v3.x = suporte a Spring Boot 4 |
| PostgreSQL driver | Gerenciado pelo Boot (JDBC) | — |
| Testcontainers | 2.x (BOM gerenciado) | Testes de integração |

## Regras

1. Nenhuma versão é definida manualmente se gerenciada pelo BOM do Spring Boot.
2. Proibido: Milestone, RC, Snapshot.
3. `<java.version>25</java.version>` e `<maven.compiler.release>25</maven.compiler.release>`.
4. Build reproduzível: `./mvnw clean verify`.
5. Features de preview do Java 25 não serão usadas.

## Referências de verificação

- spring.io/projects/spring-boot — 4.1.1 listada como GA estável (4.2.0-M1 é milestone).
- spring.io blog 2026-06-11: "Spring Modulith 2.1 GA, 2.0.7, and 1.4.12 released"; GitHub releases: 2.1.1 sobe para Boot 4.1.1.
- springdoc.org FAQ: "springdoc-openapi 3.x is compatible with spring-boot 4... last stable version as per today 3.1.0".
