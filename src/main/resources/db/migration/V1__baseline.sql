-- V1__baseline.sql
-- Baseline equivalente à migration 20250327000516_001 do e-Estoque-API (.NET 8 / EF Core / Npgsql).
-- Nomes de tabelas/colunas/constraints case-sensitive e preservados entre aspas (DATABASE-PARITY.md).

CREATE TABLE IF NOT EXISTS public."Categories"
(
    "Id"               uuid         NOT NULL,
    "Name"             varchar(80)  NOT NULL,
    "Description"      varchar(5000) NOT NULL,
    "ShortDescription" varchar(500) NOT NULL,
    "CreatedAt"        timestamp    NOT NULL,
    "UpdatedAt"        timestamp    NULL,
    "DeletedAt"        timestamp    NULL,
    "IsDeleted"        boolean      NOT NULL,
    CONSTRAINT "PK_Categories" PRIMARY KEY ("Id")
);

CREATE TABLE IF NOT EXISTS public."Companies"
(
    "Id"                          uuid         NOT NULL,
    "Name"                        varchar(80)  NOT NULL,
    "DocId"                       varchar(80)  NOT NULL,
    "Email"                       varchar(250) NOT NULL,
    "Description"                 varchar(250) NOT NULL,
    "PhoneNumber"                 varchar(80)  NOT NULL,
    "CompanyAddress_Street"       varchar(80)  NOT NULL,
    "CompanyAddress_Number"       varchar(80)  NOT NULL,
    "CompanyAddress_Complement"   varchar(80)  NOT NULL,
    "CompanyAddress_Neighborhood" varchar(80)  NOT NULL,
    "CompanyAddress_District"     varchar(80)  NOT NULL,
    "CompanyAddress_City"         varchar(80)  NOT NULL,
    "CompanyAddress_County"       varchar(80)  NOT NULL,
    "CompanyAddress_ZipCode"      varchar(80)  NOT NULL,
    "CompanyAddress_Latitude"     varchar(80)  NOT NULL,
    "CompanyAddress_Longitude"    varchar(80)  NOT NULL,
    "CreatedAt"                   timestamp    NOT NULL,
    "UpdatedAt"                   timestamp    NULL,
    "DeletedAt"                   timestamp    NULL,
    "IsDeleted"                   boolean      NOT NULL,
    CONSTRAINT "PK_Companies" PRIMARY KEY ("Id")
);

CREATE TABLE IF NOT EXISTS public."Customers"
(
    "Id"                           uuid         NOT NULL,
    "Name"                         varchar(80)  NOT NULL,
    "DocId"                        varchar(80)  NOT NULL,
    "Email"                        varchar(80)  NOT NULL,
    "Description"                  varchar(250) NOT NULL,
    "PhoneNumber"                  varchar(80)  NOT NULL,
    "CustomerAddress_Street"       varchar(80)  NOT NULL,
    "CustomerAddress_Number"       varchar(80)  NOT NULL,
    "CustomerAddress_Complement"   varchar(80)  NOT NULL,
    "CustomerAddress_Neighborhood" varchar(80)  NOT NULL,
    "CustomerAddress_District"     varchar(80)  NOT NULL,
    "CustomerAddress_City"         varchar(80)  NOT NULL,
    "CustomerAddress_County"       varchar(80)  NOT NULL,
    "CustomerAddress_ZipCode"      varchar(80)  NOT NULL,
    "CustomerAddress_Latitude"     varchar(80)  NOT NULL,
    "CustomerAddress_Longitude"    varchar(80)  NOT NULL,
    "CreatedAt"                    timestamp    NOT NULL,
    "UpdatedAt"                    timestamp    NULL,
    "DeletedAt"                    timestamp    NULL,
    "IsDeleted"                    boolean      NOT NULL,
    CONSTRAINT "PK_Customers" PRIMARY KEY ("Id")
);

CREATE TABLE IF NOT EXISTS public."Products"
(
    "Id"               uuid          NOT NULL,
    "Name"             text          NOT NULL,
    "Description"      varchar(500)  NOT NULL,
    "ShortDescription" varchar(250)  NOT NULL,
    "Price"            decimal       NOT NULL,
    "Weight"           decimal       NOT NULL,
    "Height"           decimal       NOT NULL,
    "Length"           decimal       NOT NULL,
    "Image"            varchar(5000) NOT NULL,
    "IdCategory"       uuid          NOT NULL,
    "IdCompany"        uuid          NOT NULL,
    "CreatedAt"        timestamp     NOT NULL,
    "UpdatedAt"        timestamp     NULL,
    "DeletedAt"        timestamp     NULL,
    "IsDeleted"        boolean       NOT NULL,
    CONSTRAINT "PK_Products" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_Products_Categories_IdCategory" FOREIGN KEY ("IdCategory") REFERENCES public."Categories" ("Id"),
    CONSTRAINT "FK_Products_Companies_IdCompany" FOREIGN KEY ("IdCompany") REFERENCES public."Companies" ("Id")
);

CREATE INDEX IF NOT EXISTS "IX_Products_IdCategory" ON public."Products" ("IdCategory");
CREATE INDEX IF NOT EXISTS "IX_Products_IdCompany" ON public."Products" ("IdCompany");

CREATE TABLE IF NOT EXISTS public."Taxs"
(
    "Id"         uuid           NOT NULL,
    "Name"       varchar(80)    NOT NULL,
    "Description" varchar(250)  NOT NULL,
    "Percentage" decimal        NOT NULL,
    "IdCategory" uuid           NOT NULL,
    "CreatedAt"  timestamp      NOT NULL,
    "UpdatedAt"  timestamp      NULL,
    "DeletedAt"  timestamp      NULL,
    "IsDeleted"  boolean        NOT NULL,
    CONSTRAINT "PK_Taxs" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_Taxs_Categories_IdCategory" FOREIGN KEY ("IdCategory") REFERENCES public."Categories" ("Id")
);

CREATE INDEX IF NOT EXISTS "IX_Taxs_IdCategory" ON public."Taxs" ("IdCategory");

CREATE TABLE IF NOT EXISTS public.inventories
(
    "Id"        uuid        NOT NULL,
    "Quantity"  integer     NOT NULL,
    "DateOrder" varchar(80) NOT NULL,
    "IdProduct" uuid        NOT NULL,
    "CreatedAt" timestamp   NOT NULL,
    "UpdatedAt" timestamp   NULL,
    "DeletedAt" timestamp   NULL,
    "IsDeleted" boolean     NOT NULL,
    CONSTRAINT "PK_inventories" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_inventories_Products_IdProduct" FOREIGN KEY ("IdProduct") REFERENCES public."Products" ("Id")
);

CREATE INDEX IF NOT EXISTS "IX_inventories_IdProduct" ON public.inventories ("IdProduct");

CREATE TABLE IF NOT EXISTS public."Sales"
(
    "Id"           uuid     NOT NULL,
    "Quantity"     integer  NOT NULL,
    "TotalPrice"   decimal  NOT NULL,
    "TotalTax"     decimal  NOT NULL,
    "SaleType"     integer  NOT NULL,
    "PaymentType"  integer  NOT NULL,
    "DeliveryDate" timestamp NOT NULL,
    "SaleDate"     timestamp NOT NULL,
    "PaymentDate"  timestamp NOT NULL,
    "IdCustomer"   uuid     NOT NULL,
    "CreatedAt"    timestamp NOT NULL,
    "UpdatedAt"    timestamp NULL,
    "DeletedAt"    timestamp NULL,
    "IsDeleted"    boolean  NOT NULL,
    CONSTRAINT "PK_Sales" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_Sales_Customers_IdCustomer" FOREIGN KEY ("IdCustomer") REFERENCES public."Customers" ("Id")
);

CREATE INDEX IF NOT EXISTS "IX_Sales_IdCustomer" ON public."Sales" ("IdCustomer");

CREATE TABLE IF NOT EXISTS public."SaleProducts"
(
    "Id"        uuid    NOT NULL,
    "Quantity"  integer NOT NULL,
    "IdProduct" uuid    NOT NULL,
    "IdSale"    uuid    NOT NULL,
    "CreatedAt" timestamp NOT NULL,
    "UpdatedAt" timestamp NULL,
    "DeletedAt" timestamp NULL,
    "IsDeleted" boolean NOT NULL,
    CONSTRAINT "PK_SaleProducts" PRIMARY KEY ("Id"),
    CONSTRAINT "FK_SaleProducts_Products_IdProduct" FOREIGN KEY ("IdProduct") REFERENCES public."Products" ("Id"),
    CONSTRAINT "FK_SaleProducts_Sales_IdSale" FOREIGN KEY ("IdSale") REFERENCES public."Sales" ("Id")
);

CREATE INDEX IF NOT EXISTS "IX_SaleProducts_IdProduct" ON public."SaleProducts" ("IdProduct");
CREATE INDEX IF NOT EXISTS "IX_SaleProducts_IdSale" ON public."SaleProducts" ("IdSale");
