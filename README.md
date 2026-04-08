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
