# ADR-010 — Observabilidade

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** .NET: Serilog→Loki, OpenTelemetry→Tempo (OTLP gRPC), Prometheus endpoint, console exporters.
**Decisão:** Actuator (health/info/metrics/prometheus); Micrometer Tracing + OTLP (bridge OTEL); Logback com logging estruturado JSON e MDC traceId/spanId; Prometheus scrape em /actuator/prometheus; métricas de negócio (vendas criadas/removidas, eventos publicados, estoque baixo).
**Consequências:** Endpoints Actuator não expostos integralmente em prod; dashboards Grafana mantidos via provisionamento no compose.
