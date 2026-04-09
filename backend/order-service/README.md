# Order Service

Microservicio encargado de gestionar los **pedidos** y los **restaurantes** de la plataforma FoodTech. Cuando se crea un pedido, este servicio se comunica internamente con el `delivery-service` para asignar automáticamente un repartidor disponible.

- **Puerto:** `8081`
- **Base de datos:** PostgreSQL — `foodtech_orders` (tablas: `restaurantes`, `pedidos`)
- **Java:** 17 — Spring Boot 3.x

---

## Levantar con Docker (recomendado)

Desde la carpeta `backend/` del repositorio:

```bash
# Primera vez o luego de cambios en el código
docker compose up -d --build

# Solo reiniciar (sin rebuild)
docker compose restart order-service

# Ver logs en tiempo real
docker compose logs -f order-service

# Bajar todos los servicios
docker compose down

# Bajar y borrar base de datos (reset completo)
docker compose down -v
```

Estado de los contenedores:

```bash
docker compose ps
```

---

## Endpoints

Base URL: `http://localhost:8081`

### Restaurantes

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET`  | `/restaurants` | Lista todos los restaurantes con su menú |
| `GET`  | `/restaurants/{id}` | Obtiene un restaurante por ID |

**Ejemplo — listar restaurantes:**
```bash
curl http://localhost:8081/restaurants
```

**Ejemplo — obtener restaurante por ID:**
```bash
curl http://localhost:8081/restaurants/1
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "La Hamburguesería",
  "coordenadaX": 10,
  "coordenadaY": 20,
  "menu": [
    { "id": 1, "nombre": "Hamburguesa Clásica", "precio": 18000 },
    { "id": 2, "nombre": "Hamburguesa BBQ", "precio": 22000 }
  ]
}
```

---

### Pedidos

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/orders` | Crea un nuevo pedido y asigna repartidor |
| `PUT`  | `/orders/{id}/cancel` | Cancela un pedido existente |
| `PUT`  | `/orders/{id}/deliver` | Marca un pedido como entregado |
| `GET`  | `/orders/repartidor/{repartidorId}` | Pedido activo asignado a un repartidor |

**Ejemplo — crear pedido:**
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "restauranteId": 1,
    "restauranteX": -74.0627,
    "restauranteY": 4.6482,
    "clima": "SOLEADO",
    "clienteNombre": "Ana García",
    "clienteTelefono": "3001234567",
    "clienteCoordenadasX": -74.0637,
    "clienteCoordenadasY": 4.6482,
    "productos": [
      { "id": 1, "nombre": "Hamburguesa Clásica", "precio": 18000 }
    ]
  }'
```

**Respuesta 201 Created:**
```json
{
  "id": 1,
  "restauranteId": 1,
  "repartidorId": 3,
  "estado": "ASIGNADO",
  "tiempoEstimado": 18,
  "clienteNombre": "Ana García",
  "clienteTelefono": "3001234567",
  "clienteCoordenadasX": -74.0637,
  "clienteCoordenadasY": 4.6482,
  "productos": [...]
}
```

> `tiempoEstimado` incluye **dos tramos**: repartidor → restaurante (calculado por delivery-service) + restaurante → cliente (calculado por order-service con `TiempoDeliveryCalculator` a 20 km/h).

**Ejemplo — consultar pedido activo de un repartidor:**
```bash
curl http://localhost:8081/orders/repartidor/3
```

**Respuesta 200 OK (repartidor con pedido ASIGNADO):**
```json
{
  "id": 1,
  "restauranteId": 1,
  "repartidorId": 3,
  "estado": "ASIGNADO",
  "tiempoEstimado": 18,
  "clienteNombre": "Ana García",
  "clienteCoordenadasX": -74.0637,
  "clienteCoordenadasY": 4.6482,
  "clienteTelefono": null,
  "productos": [...]
}
```

**Respuesta 404 (repartidor sin pedido activo):**
```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "path": "/orders/repartidor/3"
}
```

**Ejemplo — cancelar pedido:**
```bash
curl -X PUT http://localhost:8081/orders/1/cancel
```

**Ejemplo — marcar como entregado:**
```bash
curl -X PUT http://localhost:8081/orders/1/deliver
```

---

## Lógica de negocio destacada

### Cálculo de tiempo estimado total

`TiempoDeliveryCalculator` (servicio de dominio) calcula el tramo **restaurante → cliente** usando la aproximación plana (flat-earth) a 20 km/h:

```
tiempoTotal = tiempoRepartidorRestaurante (delivery-service) + tiempoRestauranteCliente (order-service)
```

Las coordenadas son **longitud/latitud reales de Bogotá** (Double). Los 4 restaurantes de ejemplo están distribuidos en Chapinero, Usaquén, Zona Rosa y Teusaquillo.

---

## Consultar la base de datos directamente

```bash
# Listar tablas
docker exec -it foodtech_postgres psql -U foodtech_user -d foodtech_orders -c "\dt"

# Ver restaurantes
docker exec -it foodtech_postgres psql -U foodtech_user -d foodtech_orders -c "SELECT * FROM restaurantes;"

# Ver pedidos
docker exec -it foodtech_postgres psql -U foodtech_user -d foodtech_orders -c "SELECT * FROM pedidos;"
```

---

## Comunicación entre servicios

```
order-service (8081)
    └── POST /delivery  →  delivery-service (8080)
                            asigna repartidor disponible más cercano
```

La URL del delivery-service se configura en `src/main/resources/application.properties`:
```properties
delivery.service.url=http://localhost:8080
```
En Docker, se inyecta como variable de entorno `DELIVERY_SERVICE_URL=http://delivery-service:8080`.

---

## Datos iniciales

Los datos de ejemplo se cargan automáticamente al iniciar desde:
- `src/main/resources/data.sql` — restaurantes de prueba
- `src/main/resources/db/restaurantes.sql` — datos adicionales

Son idempotentes: no insertan si el registro ya existe (`ON CONFLICT DO NOTHING`).
