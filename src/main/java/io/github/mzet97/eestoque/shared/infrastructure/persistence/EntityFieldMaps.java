package io.github.mzet97.eestoque.shared.infrastructure.persistence;

import java.util.Map;

/** Mapa de campos filtráveis/ordenáveis (whitelist) por entidade. */
public final class EntityFieldMaps {

    private EntityFieldMaps() {
    }

    public static Map<String, FilterSpecificationBuilder.FieldDef> category() {
        return Map.ofEntries(
                Map.entry("id", def("id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("name", def("name", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("description", def("description", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("shortdescription", def("shortDescription", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("createdat", def("createdAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("updatedat", def("updatedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("deletedat", def("deletedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("isdeleted", def("deleted", FilterSpecificationBuilder.FieldType.BOOLEAN)));
    }

    public static Map<String, FilterSpecificationBuilder.FieldDef> product() {
        return Map.ofEntries(
                Map.entry("id", def("id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("name", def("name", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("description", def("description", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("shortdescription", def("shortDescription", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("price", def("price", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("weight", def("weight", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("height", def("height", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("length", def("length", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("image", def("image", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("idcategory", def("category.id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("idcompany", def("company.id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("createdat", def("createdAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("updatedat", def("updatedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("deletedat", def("deletedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("isdeleted", def("deleted", FilterSpecificationBuilder.FieldType.BOOLEAN)));
    }

    public static Map<String, FilterSpecificationBuilder.FieldDef> company() {
        return Map.ofEntries(
                Map.entry("id", def("id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("name", def("name", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("docid", def("docId", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("email", def("email", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("description", def("description", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("phonenumber", def("phoneNumber", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("createdat", def("createdAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("updatedat", def("updatedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("deletedat", def("deletedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("isdeleted", def("deleted", FilterSpecificationBuilder.FieldType.BOOLEAN)));
    }

    public static Map<String, FilterSpecificationBuilder.FieldDef> customer() {
        return company();
    }

    public static Map<String, FilterSpecificationBuilder.FieldDef> inventory() {
        return Map.ofEntries(
                Map.entry("id", def("id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("quantity", def("quantity", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("dateorder", def("dateOrder", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("idproduct", def("product.id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("createdat", def("createdAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("updatedat", def("updatedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("deletedat", def("deletedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("isdeleted", def("deleted", FilterSpecificationBuilder.FieldType.BOOLEAN)));
    }

    public static Map<String, FilterSpecificationBuilder.FieldDef> tax() {
        return Map.ofEntries(
                Map.entry("id", def("id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("name", def("name", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("description", def("description", FilterSpecificationBuilder.FieldType.STRING)),
                Map.entry("percentage", def("percentage", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("idcategory", def("category.id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("createdat", def("createdAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("updatedat", def("updatedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("deletedat", def("deletedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("isdeleted", def("deleted", FilterSpecificationBuilder.FieldType.BOOLEAN)));
    }

    public static Map<String, FilterSpecificationBuilder.FieldDef> sale() {
        return Map.ofEntries(
                Map.entry("id", def("id", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("quantity", def("quantity", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("totalprice", def("totalPrice", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("totaltax", def("totalTax", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("saletype", def("saleType", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("paymenttype", def("paymentType", FilterSpecificationBuilder.FieldType.NUMBER)),
                Map.entry("deliverydate", def("deliveryDate", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("saledate", def("saleDate", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("paymentdate", def("paymentDate", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("idcustomer", def("idCustomer", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("idproduct", def("saleProducts.idProduct", FilterSpecificationBuilder.FieldType.UUID)),
                Map.entry("createdat", def("createdAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("updatedat", def("updatedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("deletedat", def("deletedAt", FilterSpecificationBuilder.FieldType.INSTANT)),
                Map.entry("isdeleted", def("deleted", FilterSpecificationBuilder.FieldType.BOOLEAN)));
    }

    private static FilterSpecificationBuilder.FieldDef def(String attribute, FilterSpecificationBuilder.FieldType type) {
        return new FilterSpecificationBuilder.FieldDef(attribute, type);
    }
}
