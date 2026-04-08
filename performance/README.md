# Performance Testing with k6

Suite de pruebas de rendimiento para FoodTech usando k6 con enfoque QA:
- scripts modulares reutilizables
- configuracion por entorno con `__ENV`
- validaciones funcionales y de performance
- reportes JSON para analisis comparativo

## Estructura

```text
performance/
├── config/
├── data/
├── tests/
├── utils/
└── reports/
```

## Requisitos

- k6 instalado localmente (opcional si usas Docker)
- backend levantado (`order-service` en `8081` y `delivery-service` en `8080`)

## Variables de entorno soportadas

- `TEST_ENV`: `dev`, `qa`, `prod` (default: `dev`)
- `ORDER_BASE_URL`: URL base de order-service
- `DELIVERY_BASE_URL`: URL base de delivery-service
- `AUTH_ENABLED`: `true/false` para activar login
- `AUTH_URL`: endpoint de autenticacion (si aplica)
- `AUTH_TOKEN`: token fijo para pruebas sin login
- `HTTP_TIMEOUT`: timeout por request (default: `30s`)
- `SLEEP_SECONDS`: pausa entre iteraciones (default: `1`)
- `ENABLE_CLEANUP`: cancela pedido al final del flujo (default: `true`)

## Ejecucion local (Windows cmd)

```bat
cd performance
run-local.cmd smoke.test.js
run-local.cmd load.test.js
run-local.cmd stress.test.js
```

## Ejecucion local directa con k6

```bat
k6 run --env TEST_ENV=dev performance\tests\smoke.test.js
k6 run --env TEST_ENV=dev performance\tests\load.test.js
k6 run --env TEST_ENV=dev performance\tests\stress.test.js
```

## Ejecucion con Docker

```bat
cd performance
run-docker.cmd smoke.test.js
run-docker.cmd load.test.js
run-docker.cmd stress.test.js
```

## Docker Compose (opcional)

```bat
cd performance
docker compose -f docker-compose.k6.yml up --build k6
```

Para habilitar observabilidad opcional (InfluxDB + Grafana):

```bat
cd performance
docker compose -f docker-compose.k6.yml --profile observability up -d influxdb grafana
```

Luego ejecuta k6 enviando metricas a InfluxDB:

```bat
cd performance
docker compose -f docker-compose.k6.yml run --rm k6 run --out influxdb=http://influxdb:8086/k6 tests/load.test.js
```

## Escenarios implementados

- `smoke.test.js`: validacion rapida de disponibilidad y flujo base
- `load.test.js`: carga esperada con rampa estable
- `stress.test.js`: carga incremental para encontrar degradacion

Cada flujo cubre:
1. Login opcional
2. Consulta de restaurantes
3. Consulta de restaurante por id
4. Creacion de pedido
5. Consulta de pedido por repartidor
6. Consulta de repartidores
7. Limpieza (cancelacion de pedido)

## Thresholds base

- `http_req_failed`: error rate maximo permitido
- `http_req_duration`: p95 y p99 por tipo de prueba
- `checks`: tasa de checks exitosos

## Reportes

Cada script exporta su resumen en `performance/reports/`:
- `smoke-summary.json`
- `load-summary.json`
- `stress-summary.json`

Usa estos archivos para comparar iteraciones entre cambios de codigo o infraestructura.
