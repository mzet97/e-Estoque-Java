# DATABASE PARITY

| Tabela .NET | Entidade Java | Compatível | Observação |
|---|---|---|---|
| public."Categories" | product CategoryEntity | ✅ | varchar(80)/(5000)/(500) exatos |
| public."Companies" | company CompanyEntity | ✅ | colunas CompanyAddress_* inclusive `_County` |
| public."Customers" | customer CustomerEntity | ✅ | Email varchar(80) preservado |
| public."Taxs" | tax TaxEntity | ✅ | FK/IX IdCategory |
| public."Products" | product ProductEntity | ✅ | Name text; 4 decimais |
| public."Sales" | sales SaleEntity | ✅ | enums int; datas timestamp NOT NULL |
| public."SaleProducts" | sales SaleProductEntity | ✅ | FKs duplas + IXs |
| public."inventories" | inventory InventoryEntity | ✅ | nome minúsculo; DateOrder varchar(80) via converter |
| (nova) outbox_events | outbox | ➕ nova | não conflita com schema .NET; ver ADR-012 |

Regras: Flyway `V1__baseline.sql` = schema .NET byte-a-byte (nomes case-sensitive entre aspas, índices e FKs com mesmos nomes .NET: PK_, FK_, IX_). `ddl-auto: validate`. Uma API Java deve operar sobre um banco criado pelas migrations .NET e vice-versa (exceto tabela outbox adicional criada por V2).
