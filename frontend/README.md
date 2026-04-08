# FoodTech Frontend — Angular 19

Aplicación Angular 19 para pedidos de comida con visualización de restaurantes en mapa interactivo, carrito de compras y gestión de pedidos activos.

---

## Requisitos

| Herramienta | Versión recomendada |
|-------------|---------------------|
| Node.js     | **≥ 18.19** (probado en v20 y v22) |
| npm         | ≥ 9 |
| Angular CLI | 19.x (`npm install -g @angular/cli`) |

> Angular 19 **no es compatible** con Node 16 ni anteriores.

---

## Instalación y ejecución

```bash
# Desde la raíz del repositorio
cd frontend

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm start
```

La aplicación estará disponible en **http://localhost:4200**

---

## Conexión con el backend

Asegúrate de que los contenedores Docker del backend estén corriendo:

```bash
cd backend
docker compose up -d --build
```

| Microservicio | URL | Endpoints usados |
|---------------|-----|------------------|
| order-service | http://localhost:8081 | `GET /restaurants`, `POST /orders`, `PUT /orders/{id}/cancel`, `PUT /orders/{id}/deliver`, `GET /orders/repartidor/{id}` |
| delivery-service | http://localhost:8080 | `GET /delivers`, `GET /delivers/{id}` |

Configuración en `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  orderServiceUrl: 'http://localhost:8081',
  deliveryServiceUrl: 'http://localhost:8080'
};
```

---

## Estructura del proyecto

```
frontend/
├── src/
│   ├── app/
│   │   ├── data/                      # Datos mock / constantes
│   │   │   └── mock-user.ts           # Usuario consumidor simulado (3 favoritos en Bogotá)
│   │   ├── models/                    # Interfaces TypeScript
│   │   │   ├── restaurante.model.ts
│   │   │   ├── producto-menu.model.ts
│   │   │   ├── deliver.model.ts
│   │   │   ├── cart-item.model.ts
│   │   │   ├── order-request.model.ts
│   │   │   ├── order-response.model.ts
│   │   │   └── deliver-order-response.model.ts
│   │   ├── services/                  # Servicios HTTP y estado
│   │   │   ├── restaurante.service.ts     # GET /restaurants
│   │   │   ├── deliver.service.ts         # GET /delivers, GET /delivers/{id}
│   │   │   ├── order.service.ts           # POST /orders
│   │   │   ├── repartidor-order.service.ts # GET /orders/repartidor/{id}, PUT /orders/{id}/deliver
│   │   │   ├── active-orders.service.ts   # Gestión pedidos activos
│   │   │   └── cart.service.ts            # Carrito de compras (signals)
│   │   └── components/
│   │       ├── mapa/                  # Selector de restaurantes en mapa
│   │       ├── mapa-page/             # Página principal (ruta /)
│   │       ├── repartidor-page/       # Vista repartidor (ruta /repartidor)
│   │       ├── menu-modal/            # Modal de menú del restaurante
│   │       ├── order-form-modal/      # Formulario de pedido (con selector favoritos)
│   │       ├── active-orders-panel/   # Panel de pedidos activos
│   │       └── restaurant-guide/      # Guía de selección
│   └── environments/
│       ├── environment.ts             # URLs desarrollo
│       └── environment.prod.ts        # URLs producción
└── public/assets/                     # Imágenes (restaurantes, repartidores)
```

---

## Componentes principales

### MapaComponent
Selector de restaurantes con integración Google Maps:
- Lista desplegable de restaurantes disponibles
- Visualización de ubicación en mapa

### MenuModalComponent
Modal de menú del restaurante:
- Lista de productos con precios formateados (`$18.000 COP`)
- Botón "Agregar" para añadir al carrito
- Cierre con botón X o click en overlay

### OrderFormModalComponent
Formulario para completar pedido:
- Selector de lugares favoritos del mock-user (Casa / Trabajo / Novia) con pre-relleno automático
- Datos del cliente editables (nombre, teléfono, coordenadas)
- Resumen del carrito
- Envío del pedido al backend

### RepartidorPageComponent
Vista de la ruta `/repartidor` (HU11 + HU12):
- Muestra datos del repartidor ID=1 (nombre, vehículo, estado)
- Si el repartidor está `EN_ENTREGA`: muestra datos del cliente y tiempo estimado
- Botón **"✅ Marcar como Entregado"**: llama `PUT /orders/{id}/deliver`, limpia el pedido activo y actualiza el badge del repartidor a `ACTIVO`
- Si no tiene pedido activo: mensaje informativo
- Botón "← Volver al mapa" con `routerLink="/"`

### ActiveOrdersPanelComponent
Panel lateral de pedidos activos:
- Lista de pedidos en curso
- Botón para cancelar pedidos
- Actualización reactiva con Angular Signals

### CartService
Servicio de estado del carrito (Angular Signals):
- Agregar/quitar productos
- Cálculo de total
- Limpieza al confirmar pedido

---

## Comandos útiles

```bash
# Compilar para producción
ng build

# Ejecutar tests unitarios
ng test

# Generar un componente nuevo
ng generate component components/nombre-componente
```

---

## Estado del desarrollo

| Paso | Descripción | Estado |
|------|-------------|--------|
| 3.1  | Environments, modelos e interfaces, servicios HTTP, HttpClient | ✅ |
| 3.2  | MapaComponent con Canvas, repartidores y restaurantes | ✅ |
| 3.3  | MenuModalComponent con lista de productos | ✅ |
| 3.4  | Integración completa en AppComponent con signals | ✅ |
| HU11 — Fase B | Routing Angular (`/` y `/repartidor`), `mock-user.ts`, `RepartidorPageComponent`, `RepartidorOrderService`, selector de favoritos en `OrderFormModalComponent`, link a `/repartidor` en `MapaPageComponent` | ✅ |
| HU12 | Botón "Marcar como Entregado" en `RepartidorPageComponent`, método `deliver()` en `RepartidorOrderService`, modelo `DeliverOrderResponse` | ✅ |
