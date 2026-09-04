# 02 — FUNCTIONAL REQUIREMENTS

IDs estáveis. Fonte = caminho no repo .NET. Cada FR é implementado por vertical slice e rastreado em 12-TRACEABILITY.md.

Padrão comum de CRUD (aplicado a Category, Product, Company, Customer, Inventory, Tax, Sale):

- **Criar**: entidade construída pelo domain (factory), validada; referências checadas; persistida; evento `{E}Created` publicado; retorna o recurso.
- **Atualizar**: carrega por id (404 se ausente), checa referências, aplica mudanças, valida, persiste, evento `{E}Updated`.
- **Excluir**: carrega por id (404 se ausente); Sale → soft delete; demais → hard delete.
- **Obter por id**: 404 + NotificationError se ausente.
- **Search**: filtros exatos opcionais, ordenação whitelist (default Id), paginação `pageIndex`/`pageSize`.
- **Gridify**: `filter`, `orderBy`, `page`, `pageSize` com includes.

---

### FR-AUTH-001 — Registrar usuário

**Descrição** Criar usuário no Keycloak e efetuar login automático.
**Entrada** `{ username, password, confirmPassword, email, firstName, lastName }`.
**Regras** 1) Obtém token de sistema (client_credentials, FR-AUTH-004); 2) POST `admin/realms/{realm}/users` com usuário `enabled=true, emailVerified=true`, credential tipo password temporária=false, atributo `attribute_key=client`; 3) falha HTTP → ForbiddenAccessException; 4) sucesso → login (FR-AUTH-002) com `username` como `username`.
**Resultado** TokenViewModel (snake_case do Keycloak).
**Erros** 403 `Invalid username or password`/`Error creating user`.
**Fonte** Application/Auth/Commands/Handlers/{RegisterUserCommandHandler,SystemLoginCommandHandlers}.cs
**Aceite** usuário criado no realm e tokens retornados; falha do Admin API → 403.

### FR-AUTH-002 — Login

**Descrição** Trocar credenciais por tokens via Keycloak password grant.
**Entrada** `{ email, password }` (email usado como campo `username` do grant).
**Regras** POST `{auth-server}/realms/{realm}/protocol/openid-connect/token` com grant_type=password, client_id=e-estoque-client, client_secret. Resposta sem access_token → ErrorNotification + 403. Sucesso → LoginUserNotification (MediatR).
**Resultado** `200` JSON snake_case: `access_token, expires_in, refresh_token, refresh_expires_in, token_type, not-before-policy, session_state, scope`.
**Erros** 403 `Invalid username or password`.
**Fonte** AuthController; LoginUserCommandHandler.cs
**Aceite** resposta idêntica à do Keycloak; credenciais inválidas → 403.

### FR-AUTH-003 — Refresh token

**Descrição** Renovar tokens.
**Entrada** `{ token }` (refresh token).
**Regras** grant_type=refresh_token. Resposta nula → 403 `Invalid refresh token`.
**Resultado/Erros** idem FR-AUTH-002.
**Fonte** RefreshTokenCommandHandler.cs
**Aceite** novos tokens; refresh inválido → 403.

### FR-AUTH-004 — System login (client_credentials)

**Descrição** Token de serviço para a Admin API do Keycloak.
**Regras** grant_type=client_credentials com client confidencial. Token nulo → 403.
**Resultado** TokenViewModel. **Endpoint** não exposto; usado internamente por FR-AUTH-001.
**Fonte** SystemLoginCommandHandlers.cs

---

### FR-CAT-001..006 — Categories CRUD + consultas

- **001 Criar** `POST /api/Categories` (role Create). Entrada `{name, description, shortDescription}`. Regras de validade: name 3–80, shortDescription 3–500, description 3–5000 (obrigatórios). Evento `CategoryCreated` → exchange `category-service`, rk `category-created`. **Retorno 201 + Location** `GET /api/Categories/{id}` e corpo do GetById.
- **002 Atualizar** `PUT /api/Categories/{id}` (Create). Mesmas validações. Evento `CategoryUpdated` → `category-updated`.
- **003 Excluir** `DELETE /api/Categories/{id}` (Create). **Hard delete**. 404 `Delete Error` se ausente.
- **004 Obter por id** `GET /api/Categories/{id}`. Includes: Products, Taxs. 404 se ausente.
- **005 Search** `GET /api/Categories?name=&description=&shortDescription=&order=&pageIndex=&pageSize=`. Igualdade exata; order whitelist {Name, Description, ShortDescription, CreatedAt, UpdatedAt, DeletedAt, default Id}.
- **006 Gridify** `GET /api/Categories/gridify?filter=&orderBy=&page=&pageSize=`.

### FR-PROD-001..006 — Products CRUD + consultas

- **001 Criar** `POST /api/Products` (Create). Entrada `{name, description, shortDescription, price, weight, height, length, image, idCategory, idCompany}` (decimais > 0; name/shortDescription 3–250; description 3–500; image 3–5000; ids obrigatórios). Valida entidade → checa Category (404 `Category not found`) → checa Company (404 `Company not found`) → persiste. Evento `ProductCreated` → exchange `product-service`, rk `product-created`. **Retorno 200** com corpo do GetById (o id de resposta vem da entidade criada).
- **002 Atualizar** `PUT /api/Products/{id}` (Create). 404 `Find Error` se produto ausente; mesmas checagens; evento `ProductUpdated` → `product-updated`.
- **003 Excluir** `DELETE /api/Products/{id}` (Create). **Hard delete**; 404 `Delete Error`.
- **004 Obter por id** `GET /api/Products/{id}` com Category + Company + CompanyAddress. 404 `Not found`.
- **005 Search** filtros: name, description, shortDescription, price, weight, height, length, idCategory, idCompany, id, createdAt, updatedAt, deletedAt; order whitelist análoga.
- **006 Gridify** includes Category, Company.

### FR-COMPANY-001..006 — Companies CRUD + consultas

Entrada inclui value object `companyAddress {street, number, complement, neighborhood, district, city, country, zipCode, latitude, longitude}` (todos obrigatórios 3–80; no banco `country` → coluna `CompanyAddress_County`). Validações: name/docId 3–80; email/description 3–250; phoneNumber 3–80. Eventos → exchange `company-service`. Delete = **hard delete**. Search/GetById sem includes de navegação.

### FR-CUSTOMER-001..006 — Customers CRUD + consultas

Idêntico a Company com `customerAddress`. Eventos → exchange `customer-service`. Delete = **hard delete**.

### FR-INV-001..006 — Inventory CRUD + consultas

- Entrada `{quantity, dateOrder, idProduct}` (quantity > 0; dateOrder obrigatória; idProduct obrigatório).
- **001 Criar**: valida entidade → checa Product (404 `Product not found`) → persiste. Evento `InventoryCreated` → exchange `inventory-service`, rk `inventory-created`. **Não altera estoque de Product** (não existe tal conceito).
- **002 Atualizar**: 404 `Find Error`; checa Product; evento `InventoryUpdated`.
- **003 Excluir**: **hard delete**; 404 `Delete Error`.
- **004 GetById** com Product (Category, Company). 405/404: 404.
- **005/006 Search/Gridify**: campos quantity, dateOrder, idProduct, id, datas de auditoria.

### FR-TAX-001..006 — Taxes CRUD + consultas

Entrada `{name, description, percentage, idCategory}`; name 3–80, description 3–250, percentage 0–100. **001 Criar**: valida → persiste (a validação de Category existe no relacionamento FK, porém o handler .NET não checa category — **parity: não checar**). Eventos → exchange `tax-service`. **003 Delete = hard delete**. GetById/Search com Category.

### FR-SALE-001..006 — Sales CRUD + consultas

- **001 Criar** `POST /api/Sales` (Create). Entrada `{quantity, totalPrice, totalTax, saleType, paymentType, deliveryDate, saleDate, paymentDate, idCustomer, idsProducts[]}`.
  - saleType: `Unitary`=1|`Recurrent`=2; paymentType: `Pix`=1|`Deposit`=2|`CreditCard`=3|`DebitCard`=4.
  - Regras: customer deve existir (404 `Customer not found`); cada product deve existir (404 `Product not found`); cada `SaleProduct` recebe a MESMA `quantity` da venda; `totalPrice`/`totalTax` **não são recalculados** (vem do cliente); validação: quantity>0, totalPrice>0, totalTax>0, datas obrigatórias (deliveryDate/saleDate/paymentDate — note que a validação FluentValidation exige valores não-default mesmo para campos nullable), idCustomer obrigatório.
  - Persiste Sale + SaleProducts. Evento `SaleCreated` (payload inclui produtos) → exchange `sale-service`, rk `sale-created`.
  - **Não verifica/decrementa estoque** (parity com .NET).
- **002 Atualizar** `PUT /api/Sales/{id}`: **o id da rota sobrescreve command.Id**; 404 `Sale not found`/`Customer not found`/`Product not found`; revalida; evento `SaleUpdated` → `sale-updated`.
- **003 Excluir** `DELETE /api/Sales/{id}`: **soft delete** (`DisableAsync` — IsDeleted=true, DeletedAt=now). 404 `Sale not found`.
- **004 GetById** com Customer + SaleProducts + Products. 404 `Not found`.
- **005 Search**: filtros quantity, totalPrice, totalTax, saleType (string→int helper, default Pix/Unitary quando inválido), paymentType, deliveryDate, saleDate, paymentDate, idCustomer, idProduct (via SaleProducts), id, datas de auditoria; order whitelist.
- **006 Gridify**: includes Customer, SaleProducts→Product→Category/Company.

### FR-ODATA-001..007 — Consultas OData (somente leitura)

`/odata/{Categories|Companies|Customers|Inventories|Products|Sales|Taxs}` com `$filter, $orderby, $select, $top, $skip, $count, $expand` (máx top 1000, expansion depth 10) e `/$count`; item por key `/odata/{Entities}({key})` → 404 se ausente. Autenticado (JWT), sem role.

### FR-HTTP-001 — Contrato de envelope e erros

Envelope camelCase `{data, success, message, pagedResult?}`; erros `{"error": "..."}` (404/400/403/401/500); ProblemDetails **não** é usado no caminho ativo (.NET usa ExceptionMiddleware). Sem corpo quando BadRequest(null).

### FR-HTTP-002 — Health

`GET /health` anônimo → 200 (usado pelo LB/compose). Java: equivalente via Actuator (`/actuator/health`) + compat `/health`.
