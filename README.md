# FoodTech MVP

Plataforma de delivery de comida con asignación inteligente de repartidores basada en proximidad y condiciones climáticas.

---

## 🏗️ Arquitectura

```
┌─────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   Frontend      │────▶│  order-service   │────▶│ delivery-service │
│   Angular 19    │     │     :8081        │     │      :8080       │
│     :4200       │     └────────┬─────────┘     └────────┬─────────┘
└─────────────────┘              │                        │
                                 ▼                        ▼
                       ┌─────────────────────────────────────────┐
                       │        PostgreSQL :5432                 │
                       │  foodtech_orders  │  foodtech_db        │
                       └─────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Requisitos
- Docker y Docker Compose
- Node.js ≥ 18.19 (para frontend)

### 1. Levantar Backend

```bash
cd backend
docker compose up -d --build
```

Servicios disponibles:
| Servicio | URL | Descripción |
|----------|-----|-------------|
| order-service | http://localhost:8081 | Gestión de pedidos y restaurantes |
| delivery-service | http://localhost:8080 | Asignación de repartidores |
| PostgreSQL | localhost:5432 | Base de datos |

### 2. Levantar Frontend

```bash
cd frontend
npm install
npm start
```

Aplicación disponible en **http://localhost:4200**

| Ruta | Vista |
|------|-------|
| `/` | Mapa principal — pedidos de usuario consumidor |
| `/repartidor` | Vista repartidor — pedido activo asignado |

---

## 📁 Estructura del proyecto

```
FoodTech_MVP/
├── backend/
│   ├── docker-compose.yml
│   ├── delivery-service/     # Microservicio de repartidores
│   ├── order-service/        # Microservicio de pedidos
│   └── init-db/              # Scripts de inicialización DB
├── frontend/                 # Aplicación Angular 19
├── docs/                     # Documentación adicional
└── subtasks-dev-qa/          # Tareas de desarrollo y QA
```

---

## 📖 Documentación detallada

- [Backend - Delivery Service](backend/delivery-service/README.md)
- [Backend - Order Service](backend/order-service/README.md)
- [Frontend - Angular](frontend/README.md)
- [Casos de prueba Postman](docs/POSTMAN_TEST_CASES.md)
- [Performance Testing con k6](performance/README.md)

---

## ⚡ Performance Testing (k6)

Suite completa de pruebas de rendimiento con arquitectura SOLID:

**Escenarios**:
- 🚦 Smoke: Validación rápida (2 min)
- 📊 Load: Línea base bajo carga nominal (8 min)
- 💥 Stress: Límites del sistema (10 min)

**Características**:
- ✓ Arquitectura hexagonal y principios SOLID
- ✓ Validaciones de negocio (clima, repartidores, distancia)
- ✓ Thresholds basados en TEST_PLAN.md
- ✓ Reportes JSON e interpretación automática

**Ejecución**:
```powershell
# Scripts locales
npm run perf:smoke
npm run perf:load
npm run perf:stress
npm run perf:spike
```

---

## 📊 Monitoreo en tiempo real (Prometheus + Grafana)

Integración de k6 con Prometheus y Grafana para visualizar métricas en vivo:

**Servicios**:
- 🔍 Prometheus: http://localhost:9090 (base de datos de métricas)
- 📈 Grafana: http://localhost:3000 (visualización)

**Flujo rápido**:

```powershell
# 1. Levantar Prometheus + Grafana
npm run monitoring:start

# 2. Ejecutar k6 con Prometheus
npm run perf:smoke:prom
npm run perf:load:prom
npm run perf:stress:prom
npm run perf:spike:prom

# 3. Ver en Grafana (http://localhost:3000)
#    - Usuario: admin
#    - Contraseña: admin
#    - Importar dashboard: ID 2587 (k6 oficial)

# 4. Detener servicios
npm run monitoring:stop
```

**Documentación**: Ver [monitoring/QUICKSTART.md](monitoring/QUICKSTART.md) (guía rápida) o [monitoring/README.md](monitoring/README.md) (completa)
- ✓ Ejecución local y Docker

**Quick start**:
```bash
cd performance

# Con k6 instalado
npm run perf:smoke    # O: .\run-local.cmd smoke.test.js
npm run perf:load     # O: .\run-local.cmd load.test.js
npm run perf:stress   # O: .\run-local.cmd stress.test.js

# Con Docker
.\run-docker.cmd smoke.test.js
```

**Documentación**:
- [`performance/README.md`](performance/README.md) - Guía de ejecución y thresholds
- [`performance/ARCHITECTURE.md`](performance/ARCHITECTURE.md) - Diseño, SOLID, patrones
- [`performance/DEBUGGING.md`](performance/DEBUGGING.md) - Troubleshooting y checklist

---

## 📈 Performance Testing (k6)

Se agregó una suite de pruebas de rendimiento en `performance/` con escenarios:
- smoke
- load
- stress

Comandos rápidos:

```bash
# Requiere k6 instalado
npm run perf:smoke
npm run perf:load
npm run perf:stress
```

Para ejecución con Docker y configuración de entornos, revisa `performance/README.md`.
