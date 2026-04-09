# Performance Testing (k6) - FoodTech MVP

Suite de performance basada en los artefactos de negocio y QA:
- `PRD.md`
- `TEST_PLAN.md`
- `TEST_CASES.md`
- `USER_STORIES.md`
- `SUBTASKS.md`

## Estructura

```text
performance/
|- config/
|- data/
|- tests/
|- flows/
|- utils/
`- reports/
```

## Cobertura funcional trazable

La matriz completa esta en `performance/TRACEABILITY.md`.

Cobertura priorizada:
- HU10 / TC-031, TC-032: catalogo en mapa (`GET /restaurants`, `GET /delivers`)
- HU8 / TC-024 y HU5 / TC-013: confirmacion + asignacion (`POST /orders`)
- HU9 / TC-029 y HU6 / TC-019: cancelacion + liberacion (`PUT /orders/{id}/cancel`, `GET /delivers/{id}`)
- HU3 / TC-007 y HU5 / TC-016: clima adverso (`LLUVIA_FUERTE`)

## Escenarios

- `tests/smoke.test.js`: 
  - `smokeCatalogVisibility` (HU10 / TC-031, TC-032)
  - `smokeCheckoutAssignmentCancel` (HU8, HU5, HU9, HU6 / TC-024, TC-013, TC-029, TC-019)
- `tests/load.test.js`:
  - `loadCheckoutAssignment` (flujo core CRITICO)
  - `loadCheckoutPendingClimate` (clima adverso CRITICO)
  - `loadBrowseMap` (catalogo ALTO)
- `tests/stress.test.js`:
  - `stressCheckoutHappy`
  - `stressCheckoutClimateHard`
- `tests/spike.test.js`:
  - `spike_checkout_burst`

Distribucion de VUs optimizada por criticidad (TEST_PLAN):
- mayor VU en checkout/asignacion/cancelacion (HU5/HU8/HU9)
- menor VU en browse/mapa (HU10)

Think-time optimizado por escenario:
- smoke: 0.5s-0.7s
- load: 0.7s-1.1s
- stress: 0.5s-0.6s
- spike: 0.3s

## Variables de entorno

- `TEST_ENV=dev|qa|prod`
- `ORDER_BASE_URL`
- `DELIVERY_BASE_URL`
- `AUTH_ENABLED=true|false`
- `AUTH_URL`
- `AUTH_TOKEN`
- `HTTP_TIMEOUT` (default `30s`)
- `SLEEP_SECONDS` (default `0.8`)
- `ENABLE_CLEANUP=true|false`
- `REPORT_DIR` (default `performance/reports` para npm y `reports` con `run-local.cmd`)

## Ejecucion local (PowerShell)

```powershell
cd "C:\Users\fredd\Desktop\Freddy Leonel\FoodTech_MVP_K6\performance"
.\run-local.cmd smoke.test.js
.\run-local.cmd load.test.js
.\run-local.cmd stress.test.js
.\run-local.cmd spike.test.js
```

## Ejecucion con npm scripts

```powershell
cd "C:\Users\fredd\Desktop\Freddy Leonel\FoodTech_MVP_K6"
npm run perf:smoke
npm run perf:load
npm run perf:stress
npm run perf:spike
```

## Ejecucion con Docker

```powershell
cd "C:\Users\fredd\Desktop\Freddy Leonel\FoodTech_MVP_K6\performance"
.\run-docker.cmd smoke.test.js
```

```powershell
cd "C:\Users\fredd\Desktop\Freddy Leonel\FoodTech_MVP_K6\performance"
docker compose -f docker-compose.k6.yml up --build k6
```

## Reportes

Cada test exporta resumen JSON en `performance/reports/`:
- `smoke-summary.json`
- `load-summary.json`
- `stress-summary.json`
- `spike-summary.json`

Opcional observabilidad:

```powershell
cd "C:\Users\fredd\Desktop\Freddy Leonel\FoodTech_MVP_K6\performance"
docker compose -f docker-compose.k6.yml --profile observability up -d influxdb grafana
```

## Thresholds por criticidad

Definidos en `performance/config/config.js` con foco de negocio:
- latencia (`http_req_duration`)
- error rate (`http_req_failed`)
- checks funcionales (`checks`)

Incluye thresholds especificos para endpoint critico `orders_create`.

## Trazabilidad completa

Ver `performance/TRACEABILITY.md` para el mapping completo:
- USER_STORY -> TEST_CASE -> SCRIPT k6 -> scenario function
