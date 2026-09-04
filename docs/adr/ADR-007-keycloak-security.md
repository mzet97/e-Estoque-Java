# ADR-007 — Keycloak + Spring Security OAuth2 Resource Server

**Status:** Aceito · **Data:** 2026-09-04

**Contexto:** .NET usa Keycloak.AuthServices + JwtBearer + IClaimsTransformation (realm_access.roles) + AudienceValidator (e-estoque-client) + [Authorize(Roles="Create")].
**Decisão:** spring-boot-starter-oauth2-resource-server; issuer-uri do realm; `JwtAuthenticationConverter` custom extraindo `realm_access.roles`; aud claim validado; `POST/PUT/DELETE /api/**` exigem authority `Create`; endpoints Auth fazem proxy REST ao Keycloak (password/refresh/client_credentials + Admin API).
**Consequências:** Sem parser JWT manual; comportamento de autorização idêntico; roles adicionais (Read/Update/Delete/admin/user) já presentes no realm não alteram regras existentes.
