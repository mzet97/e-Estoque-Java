# 07 — SECURITY MODEL

## Identity Provider

Keycloak 24 realm **e-estoque** (docker `--import-realm` com `keycloak-config/realms/my-realm.json`).

- Client confidencial `e-estoque-client` — direct access grants habilitado (password grant usado por /api/Auth/login).
- Realm roles: `admin`, `user`, `Create`, `Read`, `Update`, `Delete`.
- Usuários seed: `admin` (admin, Create, Read, Update, Delete), `usuario` (user, Read).
- Authority source: claim `realm_access.roles` (KeycloakClaimsTransformation no .NET).

## Resource server (Java)

- Spring Security OAuth2 Resource Server + JWT (`issuer-uri` do realm).
- `KeycloakRealmRolesAuthoritiesConverter`: extrai `realm_access.roles` → `SimpleGrantedAuthority`.
- Audience validation: token deve conter `e-estoque-client` (equivalente ao AudienceValidator do Program.cs).
- Regras:
  - `POST/PUT/DELETE /api/**` → `hasAuthority("Create")`
  - `GET /api/**`, `/odata/**` → `authenticated`
  - `/health`, `/actuator/health|info` (dev/test), swagger (dev/test) → `permitAll`
  - `/actuator/**` demais → denial por padrão em prod
- CSRF off (API stateless), CORS espelhando CorsConfig (dev: `*`; prod: GET de `https://mzet97.dev`).

## Fluxo Auth (FR-AUTH)

`/api/Auth/login|refresh_token|register` fazem proxy HTTP ao Keycloak com `RestClient`:
- login: `grant_type=password&client_id=e-estoque-client&client_secret=…&username={email}&password=…`
- refresh: `grant_type=refresh_token…`
- register: 1º `grant_type=client_credentials` → POST `admin/realms/{realm}/users` (Bearer system token) → login.
Respostas pass-through **snake_case** (Jackson `@JsonProperty` / PropertyNamingStrategies.SNAKE_CASE no DTO de auth).

## Erros

| Exceção Java | HTTP | Corpo |
|---|---|---|
| `NotFoundException` | 404 | `{"error": msg}` |
| `ValidationException` (domínio) | 400 | `{"error": "msgs joined ,"} |
| `ForbiddenAccessException` | 403 | `{"error": msg}` |
| `AccessDeniedException` (Spring) | 403 | corpo padrão do framework (parity: .NET também usa 403 do framework) |
| token ausente/inválido/expirado | 401 | padrão do framework |
| qualquer outra | 500 | `{"error": msg}` |

## Multi-tenancy

Não existe isolamento no sistema original (`idCompany` informado pelo cliente; nenhuma verificação contra o usuário autenticado). Parity mantida; risco documentado em MIGRATION-DIFFERENCES (SECURITY-GAP-001). Testes documentam o comportamento atual.

## Logging seguro

Nunca logar: password, client_secret, access_token, refresh_token, Authorization header.
