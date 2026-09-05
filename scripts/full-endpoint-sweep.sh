#!/usr/bin/env bash
# Sweep completo de endpoints do e-Estoque-Java contra a API rodando.
# Uso: bash scripts/full-endpoint-sweep.sh   (API=http://localhost:8081)
set -u
API="${API:-http://localhost:8081}"
BASE="$(cd "$(dirname "$0")/.." && pwd)"
DIR="$BASE/target/sweep"
mkdir -p "$DIR"
PASS=0; FAIL=0
RESULTS="$DIR/results.txt"
: > "$RESULTS"

check() { # check <nome> <esperado> <obtido>
  if [ "$2" = "$3" ]; then
    PASS=$((PASS+1)); echo "OK   $1 ($3)" >> "$RESULTS"
  else
    FAIL=$((FAIL+1)); echo "FAIL $1 (esperado $2, veio $3)" >> "$RESULTS"
  fi
}

jget() { # jget <arquivo> <expressao-python sobre d>
  python -c 'import json,sys;d=json.load(open(sys.argv[1]));print(eval(sys.argv[2]))' "$1" "$2" 2>/dev/null
}

post() { curl -s -X POST "$API$1" -H "Authorization: Bearer $ADMIN" -H "Content-Type: application/json" -d "$2" -o "${3:-$DIR/out.json}" -w "%{http_code}"; }
get  () { curl -s "$API$1" -H "Authorization: Bearer ${2:-$ADMIN}" -o "${3:-$DIR/out.json}" -w "%{http_code}"; }
put  () { curl -s -X PUT "$API$1" -H "Authorization: Bearer $ADMIN" -H "Content-Type: application/json" -d "$2" -o "${3:-$DIR/out.json}" -w "%{http_code}"; }
del  () { curl -s -X DELETE "$API$1" -H "Authorization: Bearer $ADMIN" -o /dev/null -w "%{http_code}"; }

# ---------- AUTH ----------
RUNTAG=$(date +%s)
check "AUTH login (admin)" 200 "$(curl -s -X POST $API/api/Auth/login -H 'Content-Type: application/json' -d '{"email":"admin","password":"dsv@123"}' -o "$DIR/admin.json" -w '%{http_code}')"
ADMIN=$(jget "$DIR/admin.json" "d['access_token']")
check "AUTH login (usuario)" 200 "$(curl -s -X POST $API/api/Auth/login -H 'Content-Type: application/json' -d '{"email":"usuario","password":"senha123"}' -o "$DIR/usuario.json" -w '%{http_code}')"
check "AUTH refresh_token" 200 "$(RT=$(jget "$DIR/admin.json" "d['refresh_token']"); curl -s -X POST $API/api/Auth/refresh_token -H 'Content-Type: application/json' -d "{\"token\":\"$RT\"}" -o "$DIR/refresh.json" -w '%{http_code}')"
check "AUTH register (novo usuario)" 200 "$(curl -s -X POST $API/api/Auth/register -H 'Content-Type: application/json' -d "{\"username\":\"sweep$RUNTAG\",\"password\":\"sweep@123\",\"confirmPassword\":\"sweep@123\",\"email\":\"sweep$RUNTAG@test.com\",\"firstName\":\"Sweep\",\"lastName\":\"Test\"}" -o "$DIR/register.json" -w '%{http_code}')"
check "AUTH register -> login do novo usuario" 200 "$(curl -s -X POST $API/api/Auth/login -H 'Content-Type: application/json' -d "{\"email\":\"sweep$RUNTAG\",\"password\":\"sweep@123\"}" -o /dev/null -w '%{http_code}')"

# ---------- FIXTURES base ----------
R=$(post /api/Categories '{"name":"Sweep Cat","description":"Categoria do sweep","shortDescription":"Sweep"}'); check "CATEGORIES post (201 + Location)" 201 "$R"
CAT=$(jget "$DIR/out.json" "d['data']['id']")
R=$(post /api/Companies '{"name":"Sweep Company","docId":"12.345.678/0001-90","email":"sweep@acme.com","description":"Empresa sweep","phoneNumber":"+55 11 99999-0000","companyAddress":{"street":"Av Paulista","number":"1000","complement":"Sala 10","neighborhood":"Bela Vista","district":"Centro","city":"Sao Paulo","country":"Brasil","zipCode":"01310-100","latitude":"-23.561","longitude":"-46.654"}}'); check "COMPANY post (200)" 200 "$R"
COMP=$(jget "$DIR/out.json" "d['data']['id']")
R=$(post /api/Customers '{"name":"Sweep Customer","docId":"123.456.789-00","email":"sweep@email.com","description":"Cliente sweep","phoneNumber":"+55 11 98888-7777","customerAddress":{"street":"Rua das Flores","number":"1000","complement":"Casa 2","neighborhood":"Jardim","district":"Centro","city":"Campinas","country":"Brasil","zipCode":"13010-000","latitude":"-22.907","longitude":"-47.061"}}'); check "CUSTOMER post (200)" 200 "$R"
CUST=$(jget "$DIR/out.json" "d['data']['id']")
R=$(post /api/Products "{\"name\":\"Sweep Product\",\"description\":\"Produto do sweep\",\"shortDescription\":\"Sweep\",\"price\":10.50,\"weight\":1.0,\"height\":1.5,\"length\":2.0,\"image\":\"https://img/sweep.png\",\"idCategory\":\"$CAT\",\"idCompany\":\"$COMP\"}"); check "PRODUCT post (200)" 200 "$R"
PROD=$(jget "$DIR/out.json" "d['data']['id']")
R=$(post /api/Inventories "{\"quantity\":7,\"dateOrder\":\"2026-09-05T10:00:00Z\",\"idProduct\":\"$PROD\"}"); check "INVENTORY post (200)" 200 "$R"
INV=$(jget "$DIR/out.json" "d['data']['id']")
R=$(post /api/Taxs "{\"name\":\"ICMS Sweep\",\"description\":\"Imposto sweep\",\"percentage\":18,\"idCategory\":\"$CAT\"}"); check "TAX post (200)" 200 "$R"
TAX=$(jget "$DIR/out.json" "d['data']['id']")
R=$(post /api/Sales "{\"quantity\":1,\"totalPrice\":10.50,\"totalTax\":1.05,\"saleType\":1,\"paymentType\":1,\"deliveryDate\":\"2026-09-10T12:00:00Z\",\"saleDate\":\"2026-09-05T10:00:00Z\",\"paymentDate\":\"2026-09-05T11:00:00Z\",\"idCustomer\":\"$CUST\",\"idsProducts\":[\"$PROD\"]}"); check "SALE post (200)" 200 "$R"
SALE=$(jget "$DIR/out.json" "d['data']['id']")
SALE2=$(post /api/Sales "{\"quantity\":2,\"totalPrice\":21.00,\"totalTax\":2.10,\"saleType\":2,\"paymentType\":3,\"deliveryDate\":\"2026-09-11T12:00:00Z\",\"saleDate\":\"2026-09-05T12:00:00Z\",\"paymentDate\":\"2026-09-05T13:00:00Z\",\"idCustomer\":\"$CUST\",\"idsProducts\":[\"$PROD\"]}" "$DIR/sale2.json"; jget "$DIR/sale2.json" "d['data']['id']")

# ---------- GET by id / list / search (todos os contextos) ----------
for c in Categories Companies Customers Inventories Taxs Products Sales; do
  check "GET /api/$c (lista)" 200 "$(get /api/$c)"
  ID=$(jget "$DIR/out.json" "d['data'][0]['id']")
  check "GET /api/$c/{id}" 200 "$(get /api/$c/$ID)"
done

# ---------- PUT (id da rota prevalece) ----------
check "CATEGORIES put" 200 "$(put /api/Categories/$CAT '{"name":"Sweep Cat Up","description":"Atualizada","shortDescription":"Upd"}')"
check "COMPANY put" 200 "$(put /api/Companies/$COMP '{"name":"Sweep Company Up","docId":"12.345.678/0001-90","email":"sweep@acme.com","description":"Atualizada","phoneNumber":"+55 11 99999-0000","companyAddress":{"street":"Av Paulista","number":"1000","complement":"Sala 10","neighborhood":"Bela Vista","district":"Centro","city":"Sao Paulo","country":"Brasil","zipCode":"01310-100","latitude":"-23.561","longitude":"-46.654"}}')"
check "CUSTOMER put" 200 "$(put /api/Customers/$CUST '{"name":"Sweep Customer Up","docId":"123.456.789-00","email":"sweep@email.com","description":"Atualizado","phoneNumber":"+55 11 98888-7777","customerAddress":{"street":"Rua das Flores","number":"1000","complement":"Casa 2","neighborhood":"Jardim","district":"Centro","city":"Campinas","country":"Brasil","zipCode":"13010-000","latitude":"-22.907","longitude":"-47.061"}}')"
check "PRODUCT put" 200 "$(put /api/Products/$PROD '{"name":"Sweep Product Up","description":"Atualizado","shortDescription":"Upd","price":11.00,"weight":1.0,"height":1.5,"length":2.0,"image":"https://img/sweep.png","idCategory":"'$CAT'","idCompany":"'$COMP'"}')"
check "INVENTORY put" 200 "$(put /api/Inventories/$INV '{"quantity":9,"dateOrder":"2026-09-06T10:00:00Z","idProduct":"'$PROD'"}')"
check "TAX put" 200 "$(put /api/Taxs/$TAX '{"name":"ICMS Sweep Up","description":"Atualizado","percentage":20,"idCategory":"'$CAT'"}')"
check "SALE put" 200 "$(put /api/Sales/$SALE '{"quantity":3,"totalPrice":31.50,"totalTax":3.15,"saleType":1,"paymentType":2,"deliveryDate":"2026-09-12T12:00:00Z","saleDate":"2026-09-05T10:00:00Z","paymentDate":"2026-09-05T11:00:00Z","idCustomer":"'$CUST'","idsProducts":["'$PROD'"]}')"

# ---------- gridify (todos) ----------
check "CATEGORIES gridify" 200 "$(get "/api/Categories/gridify?filter=name==Sweep%20Cat%20Up")"
check "PRODUCTS gridify" 200 "$(get "/api/Products/gridify?filter=name*=Sweep")"
check "COMPANIES gridify" 200 "$(get "/api/Companies/gridify?filter=name==Sweep%20Company%20Up")"
check "CUSTOMERS gridify" 200 "$(get "/api/Customers/gridify?filter=name*=Sweep")"
check "INVENTORIES gridify" 200 "$(get "/api/Inventories/gridify?filter=quantity==9")"
check "TAXS gridify" 200 "$(get "/api/Taxs/gridify?filter=percentage%3E10")"
check "SALES gridify" 200 "$(get "/api/Sales/gridify?filter=quantity%3E=1&orderBy=quantity%20desc")"

# ---------- search com filtro ----------
check "PRODUCTS search filtro" 200 "$(get "/api/Products?name=Sweep%20Product%20Up")"
check "SALES search saleType=Unitary" 200 "$(get "/api/Sales?saleType=Unitary")"

# ---------- OData (7 entidades x list/key/filter/count) ----------
for e in Categories Companies Customers Inventories Products Sales Taxs; do
  check "ODATA /odata/$e (lista)" 200 "$(get "/odata/$e")"
  ID=$(jget "$DIR/out.json" "d['value'][0]['id']")
  check "ODATA /odata/$e(id)" 200 "$(get "/odata/$e($ID)")"
done
check "ODATA Products \$filter contains" 200 "$(get "/odata/Products?\$filter=contains(name,'Sweep')")"
check "ODATA Sales \$count" 200 "$(get "/odata/Sales?\$count=true")"
N=$(jget "$DIR/out.json" "d['@odata.count']")
check "ODATA Sales \$count>0" 200 "$([ "${N:-0}" -ge 1 ] && echo 200 || echo 500)"
check "ODATA Categories \$orderby+top" 200 "$(get "/odata/Categories?\$orderby=name%20desc&\$top=2")"

# ---------- DELETE (hard x soft) ----------
check "INVENTORY delete (hard)" 200 "$(del /api/Inventories/$INV)"
check "INVENTORY get apos delete -> 404" 404 "$(get /api/Inventories/$INV)"
check "TAX delete (hard)" 200 "$(del /api/Taxs/$TAX)"
check "TAX get apos delete -> 404" 404 "$(get /api/Taxs/$TAX)"
check "SALE delete (soft)" 200 "$(del /api/Sales/$SALE)"
SOFT=$(wsl -e sh -c "docker exec eestoque-postgres psql -U postgres -d e-estoque -tAc \"SELECT \\\"IsDeleted\\\" FROM public.\\\"Sales\\\" WHERE \\\"Id\\\"='$SALE'\"" 2>/dev/null | tr -d '\0 \r\n')
check "SALE soft delete no banco (IsDeleted=t)" 200 "$([ "$SOFT" = "t" ] && echo 200 || echo 500)"

# ---------- infra ----------
check "GET /health" 200 "$(curl -s -o /dev/null -w '%{http_code}' $API/health)"
check "GET /actuator/health" 200 "$(curl -s -o /dev/null -w '%{http_code}' $API/actuator/health)"
check "GET /actuator/prometheus" 200 "$(curl -s -o /dev/null -w '%{http_code}' $API/actuator/prometheus)"
R=$(curl -s $API/v3/api-docs -o "$DIR/apidocs.json" -w '%{http_code}')
V=$(jget "$DIR/apidocs.json" "d['openapi']")
check "GET /v3/api-docs (openapi)" 200 "$([ -n "$V" ] && echo "$R" || echo 500)"
check "GET /swagger-ui/index.html" 200 "$(curl -s -o /dev/null -w '%{http_code}' $API/swagger-ui/index.html)"

echo "================ SWEEP ================"
cat "$RESULTS"
echo "======================================"
echo "PASS=$PASS FAIL=$FAIL"
[ "$FAIL" = "0" ]
