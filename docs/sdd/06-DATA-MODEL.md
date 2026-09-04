# 06 — DATA MODEL

Fonte: `Infrastructure/Persistence/Migrations/20250327000516_001.cs` + mappings EF. Schema `public`. Todas as PKs `uuid`; timestamps `timestamp without time zone`; FKs sem cascade (ClientSetNull). Detalhes quirks preservados para parity.

## Tabelas

### "Categories"
| Coluna | Tipo | Null |
|---|---|---|
| Id | uuid PK | no |
| Name | varchar(80) | no |
| Description | varchar(5000) | no |
| ShortDescription | varchar(500) | no |
| CreatedAt | timestamp | no |
| UpdatedAt / DeletedAt | timestamp | yes |
| IsDeleted | boolean | no |

### "Companies"
Id PK; Name varchar(80); DocId varchar(80); Email varchar(250); Description varchar(250); PhoneNumber varchar(80);
CompanyAddress_Street/_Number/_Complement/_Neighborhood/_District/_City/_County (country!)/_ZipCode/_Latitude/_Longitude varchar(80) not null; CreatedAt/UpdatedAt/DeletedAt/IsDeleted.

### "Customers"
Idêntica a Companies com prefixo `CustomerAddress_*` (mesmo `_County`); **Email varchar(80)** (diferente de Companies).

### "Taxs"
Id PK; Name varchar(80); Description varchar(250); Percentage decimal; IdCategory uuid FK→Categories (IX_Taxs_IdCategory); auditoria.

### "Products"
Id PK; Name **text**; Description varchar(500); ShortDescription varchar(250); Price/Weight/Height/Length decimal; Image varchar(5000); IdCategory FK→Categories (IX); IdCompany FK→Companies (IX); auditoria.

### "inventories" (minúsculas!)
Id PK; Quantity integer; **DateOrder varchar(80)** (DateTime serializado em texto — quirk); IdProduct FK→Products (IX_inventories_IdProduct); auditoria.

### "Sales"
Id PK; Quantity integer; TotalPrice/TotalTax decimal; SaleType/PaymentType integer (enum); DeliveryDate/SaleDate/PaymentDate **timestamp NOT NULL** (apesar das propriedades serem nullable); IdCustomer FK→Customers (IX_Sales_IdCustomer); auditoria.

### "SaleProducts"
Id PK; Quantity integer; IdProduct FK→Products (IX); IdSale FK→Sales (IX); auditoria.

## Java ↔ banco

- Hibernate ddl-auto `validate`; Flyway `V1__baseline.sql` recria exatamente este schema (nomes com aspas para case-sensitive).
- Entidades JPA mapeiam tabela por nome exato; colunas de endereço `*_County` mapeadas explicitamente.
- `Inventory.dateOrder`: coluna varchar; em Java o campo é `Instant`/`LocalDateTime` persistido via AttributeConverter que grava o mesmo formato de texto do .NET (representação textual padrão) — parity documentada.
- Leitura de bases existentes .NET: compatível (mesmos tipos/nomes).

Matriz detalhada: `DATABASE-PARITY.md`.
