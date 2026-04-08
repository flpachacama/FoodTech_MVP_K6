# PROMPTS-HU11 — Visualizar pedido asignado (repartidor)

## Análisis de la HU

**Estado:** ✅ FASE A COMPLETADA — ✅ FASE B COMPLETADA — 🏁 HU11 FINALIZADA

**Objetivo:** El repartidor accede a la ruta `/repartidor` y ve el pedido que tiene actualmente asignado (datos del cliente + tiempo estimado). Si no tiene pedidos activos, ve un mensaje informativo. No hay autenticación real: se simula el login con el repartidor ID=1 (dato real en BD) y se usa un mock de usuario consumidor con 3 direcciones favoritas en Bogotá.

---

## Inventario de endpoints necesarios

| Recurso | Endpoint | ¿Existe? | Servicio | Puerto |
|---------|----------|----------|----------|--------|
| Datos del repartidor | `GET /delivers/{id}` | ✅ SÍ | delivery-service | 8080 |
| Pedido activo del repartidor | `GET /orders/repartidor/{repartidorId}` | ✅ CREADO | order-service | 8081 |
| Routing Angular | rutas `/` y `/repartidor` | ✅ COMPLETADO | frontend | — |

---

## Mock de usuario consumidor

El usuario no tiene autenticación. Sus datos viven en `frontend/src/app/data/mock-user.ts` como constante. Tiene 3 direcciones favoritas en Bogotá:

| # | Alias | Dirección | Longitud (X) | Latitud (Y) |
|---|-------|-----------|--------------|-------------|
| 1 | Casa | Cra 7 #45-20, Chapinero | -74.0637 | 4.6482 |
| 2 | Trabajo | Cl 72 #10-34, El Chicó | -74.0503 | 4.6643 |
| 3 | Novia | Av 19 #116-30, Usaquén | -74.0533 | 4.6942 |

La dirección **Casa** viene pre-seleccionada por defecto al abrir el formulario de pedido.

---

## Plan de ejecución

### Fase A — Backend: nuevo endpoint `GET /orders/repartidor/{id}` (order-service)

**Paso A1** → `PedidoJpaRepository.java`
- Agregar: `Optional<PedidoEntity> findFirstByRepartidorIdAndEstadoIn(Long repartidorId, List<String> estados)`
- Estados a buscar: `["ASIGNADO", "EN_ENTREGA"]`

**Paso A2** → `PedidoRepository.java` *(port)*
- Agregar: `Optional<Pedido> findPedidoActivoByRepartidorId(Long repartidorId)`

**Paso A3** → `PedidoDataAdapter.java`
- Implementar el método de A2 delegando en la query de A1 y mapeando `PedidoEntity → Pedido`

**Paso A4** → `OrderUseCase.java` *(port input)*
- Agregar: `OrderResponseDto getOrderByRepartidorId(Long repartidorId)`

**Paso A5** → `OrderApplicationService.java`
- Implementar: busca pedido activo; si no hay → lanza `PedidoNotFoundException`; si hay → mapea a `OrderResponseDto`

**Paso A6** → `OrderController.java`
- Agregar: `GET /orders/repartidor/{repartidorId}` → retorna `OrderResponseDto` (200) o 404

**Paso A7** → Tests
- `OrderApplicationServiceTest`: 2 nuevos casos (`pedidoActivo_retornaDto` y `sinPedidoActivo_lanza404`)
- `PedidoDataAdapterTest`: 1 nuevo caso para la nueva query

---

### Fase B — Frontend: routing + vista repartidor + mock usuario

**Paso B1** → Crear `frontend/src/app/data/mock-user.ts`
- Constante `MOCK_USER` con nombre, teléfono y lista `favoritos[]` con las 3 direcciones de la tabla de arriba

**Paso B2** → Habilitar routing Angular
- `app.component.ts` → template cambia a `<router-outlet />`
- `app.routes.ts`:
  - `{ path: '', component: MapaPageComponent }`
  - `{ path: 'repartidor', component: RepartidorPageComponent }`
  - `{ path: '**', redirectTo: '' }`
- `app.config.ts` → confirmar/añadir `provideRouter(routes)`

**Paso B3** → Crear `frontend/src/app/services/repartidor-order.service.ts`
- Método `getOrderByRepartidorId(id: number): Observable<OrderResponse>`
- Llama `GET /orders/repartidor/{id}` en order-service (port 8081)

**Paso B4** → Crear componente `repartidor-page`
- Ruta: `frontend/src/app/components/repartidor-page/`
- Al inicializar:
  1. Llama `GET /delivers/1` → obtiene datos del repartidor
  2. Si `estado === 'EN_ENTREGA'`: llama `getOrderByRepartidorId(1)` → muestra datos del cliente
  3. Si 404 o estado distinto: muestra *"No tienes pedidos asignados"*
- Muestra: nombre repartidor, vehículo, estado, clienteNombre, clienteTelefono, coordenadas cliente, tiempoEstimado
- Botón "← Volver al mapa" con `routerLink="/"`

**Paso B5** → Integrar mock-user en `order-form-modal`
- Importar `MOCK_USER`
- Mostrar selector de favoritos (Casa / Trabajo / Novia)
- Al seleccionar: pre-llenar `clienteNombre`, `clienteTelefono`, `clienteCoordenadasX/Y`

**Paso B6** → Agregar link en `mapa-page`
- Botón `routerLink="/repartidor"` visible en el mapa

---

---

## Pruebas manuales — endpoint `GET /orders/repartidor/{repartidorId}`

> **Prerequisito:** Docker corriendo, servicios levantados con `docker compose up -d`.
> Primero crea un pedido para tener un repartidor asignado, o consulta directamente.

### 1. Verificar que el repartidor 1 tiene un pedido activo

**Paso previo — crear pedido que asigne al repartidor 1:**
```bash
curl -s -X POST http://localhost:8081/orders \
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
  }' | jq .
```

**Respuesta esperada (pedido ASIGNADO):**
```json
{
  "id": 1,
  "restauranteId": 1,
  "repartidorId": 3,
  "estado": "ASIGNADO",
  "tiempoEstimado": 12,
  "clienteId":1,
  "clienteNombre": "Ana García",
  "clienteCoordenadasX": -74.0637,
  "clienteCoordenadasY": 4.6482,
  "clienteTelefono": "3001234567",
  "productos": [...]
}
```
> Nota el `repartidorId` que devuelve. Úsalo en el siguiente curl.

---

### 2. Consultar pedido activo del repartidor asignado

```bash
# Reemplaza {id} con el repartidorId del paso anterior
curl -s http://localhost:8081/orders/repartidor/{id} | jq .
```

**Respuesta esperada (200 OK):**
```json
{
  "id": 1,
  "restauranteId": 1,
  "repartidorId": 3,
  "estado": "ASIGNADO",
  "tiempoEstimado": 12,
  "clienteNombre": "Ana García",
  "clienteCoordenadasX": -74.0637,
  "clienteCoordenadasY": 4.6482,
  "clienteTelefono": null,
  "productos": [
    { "id": 1, "nombre": "Hamburguesa Clásica", "precio": 18000 }
  ]
}
```

---

### 3. Consultar repartidor sin pedido activo (404)

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/orders/repartidor/999
# Esperado: 404
```

**Respuesta esperada (404):**
```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "path": "/orders/repartidor/999"
}
```

---

### 4. Verificar listado de repartidores en delivery-service

```bash
# Ver cuáles están EN_ENTREGA después de crear el pedido
curl -s http://localhost:8080/delivers | jq '[.[] | select(.estado == "EN_ENTREGA")]'
```

---

## Prompt Paso A1-A6 — Crear endpoint GET /orders/repartidor/{id} ✅ COMPLETADO

```
Tengo un microservicio Spring Boot 3.2 (Java 17) con Arquitectura Hexagonal.
Paquete base: com.foodtech

### Contexto existente
- Ya existe PedidoJpaRepository que extiende JpaRepository<PedidoEntity, Long>
- Ya existe PedidoRepository (port) con save() y findById()
- Ya existe PedidoDataAdapter que implementa PedidoRepository
- Ya existe OrderUseCase (port input) con createOrder, cancelOrder, deliverOrder
- Ya existe OrderApplicationService que implementa OrderUseCase
- Ya existe OrderController con POST /orders, PUT /orders/{id}/cancel, PUT /orders/{id}/deliver
- Ya existe OrderResponseDto con: id, restauranteId, repartidorId, productos, clienteId,
  clienteNombre, clienteCoordenadasX, clienteCoordenadasY, clienteTelefono, tiempoEstimado, estado
- Ya existe PedidoNotFoundException que lanza 404

### Lo que necesito

1. En PedidoJpaRepository agregar:
   Optional<PedidoEntity> findFirstByRepartidorIdAndEstadoIn(Long repartidorId, List<String> estados);

2. En PedidoRepository (port) agregar:
   Optional<Pedido> findPedidoActivoByRepartidorId(Long repartidorId);

3. En PedidoDataAdapter implementar el método anterior:
   - Busca con estados ["ASIGNADO", "EN_ENTREGA"]
   - Mapea PedidoEntity → Pedido usando el mapper existente
   - Devuelve Optional.empty() si no hay resultado

4. En OrderUseCase agregar:
   OrderResponseDto getOrderByRepartidorId(Long repartidorId);

5. En OrderApplicationService implementar:
   - Llama findPedidoActivoByRepartidorId(repartidorId)
   - Si Optional vacío → throw new PedidoNotFoundException(repartidorId)
   - Si hay pedido → mapea a OrderResponseDto con toResponse(pedido)

6. En OrderController agregar:
   @GetMapping("/repartidor/{repartidorId}")
   public ResponseEntity<OrderResponseDto> getOrderByRepartidorId(@PathVariable Long repartidorId)
   - Retorna 200 con el DTO si existe
   - Si PedidoNotFoundException → 404 (ya manejado por GlobalExceptionHandler)

### Reglas
- Seguir el mismo estilo/convenciones del código existente
- No modificar lógica existente de createOrder/cancelOrder/deliverOrder
- Añadir 2 tests en OrderApplicationServiceTest y 1 en PedidoDataAdapterTest
```

---

## Prompt Paso B1 — Crear mock-user.ts ✅ COMPLETADO

```
Tengo una aplicación Angular 19 standalone.

### Lo que necesito

Crear el archivo frontend/src/app/data/mock-user.ts con una constante MOCK_USER
que representa al usuario consumidor simulado (no hay autenticación real).

La constante debe tener esta estructura:
{
  nombre: string,
  telefono: string,
  favoritos: Array<{
    alias: string,        // "Casa", "Trabajo", "Novia"
    direccion: string,    // dirección legible
    coordenadaX: number,  // longitud Bogotá
    coordenadaY: number   // latitud Bogotá
  }>
}

Usar estas ubicaciones reales de Bogotá:
1. Casa     → Cra 7 #45-20, Chapinero     | x: -74.0637  y: 4.6482
2. Trabajo  → Cl 72 #10-34, El Chicó      | x: -74.0503  y: 4.6643
3. Novia    → Av 19 #116-30, Usaquén      | x: -74.0533  y: 4.6942

El primer favorito (Casa) debe ser el pre-seleccionado por defecto.

### Reglas
- Solo exportar la constante, sin lógica adicional
- Tipado estricto: exportar también la interfaz MockUser y Favorito
```

---

## Prompt Paso B2 — Habilitar routing Angular ✅ COMPLETADO

```
Tengo una aplicación Angular 19 standalone.

### Contexto existente
- app.component.ts hace template: '<app-mapa-page />' sin router-outlet
- app.routes.ts tiene un array vacío: Routes = []
- app.config.ts tiene provideHttpClient() ya configurado
- Ya existe MapaPageComponent en components/mapa-page/

### Lo que necesito

1. Modificar app.routes.ts:
   - Ruta '' → MapaPageComponent (vista usuario consumidor)
   - Ruta 'repartidor' → RepartidorPageComponent (lazy o directo, usar lazy con loadComponent)
   - Ruta '**' → redirectTo: ''

2. Modificar app.component.ts:
   - Cambiar template a '<router-outlet />'
   - Importar RouterOutlet en imports[]

3. Verificar/modificar app.config.ts:
   - Asegurar que provideRouter(routes) esté en providers[]

### Reglas
- Mantener todo standalone (sin NgModules)
- El RepartidorPageComponent aún no existe; usar loadComponent con la ruta que tendrá
- No romper la funcionalidad existente del MapaPageComponent
```

---

## Prompt Paso B3-B4 — Crear servicio y componente repartidor-page ✅ COMPLETADO

```
Tengo una aplicación Angular 19 standalone con routing configurado.

### Contexto existente
- Models: OrderResponse (id, restauranteId, repartidorId, productos, clienteId, clienteNombre,
  clienteCoordenadasX, clienteCoordenadasY, clienteTelefono, tiempoEstimado, estado)
- Models: Deliver (id, nombre, estado, vehiculo, ubicacionX, ubicacionY)
- Services existentes: deliver.service.ts con getById(id): Observable<Deliver>
- Nuevo endpoint disponible: GET http://localhost:8081/orders/repartidor/{id}
- Repartidor simulado: siempre ID = 1

### Lo que necesito

1. Crear frontend/src/app/services/repartidor-order.service.ts:
   - Método getOrderByRepartidorId(id: number): Observable<OrderResponse>
   - Llama GET http://localhost:8081/orders/repartidor/{id}
   - Usar inject(HttpClient)

2. Crear frontend/src/app/components/repartidor-page/ (4 archivos):
   - Al inicializar con signal repartidorId = 1:
     a) Llama deliver.service.getById(1) → almacena en signal repartidor
     b) Si repartidor.estado === 'EN_ENTREGA':
        llama repartidorOrderService.getOrderByRepartidorId(1) → almacena en signal pedidoActivo
     c) Si error 404 o estado distinto: pedidoActivo = null
   - Template muestra:
     * Card repartidor: nombre, vehículo (emoji), estado (badge coloreado)
     * Si tiene pedido: Card pedido con clienteNombre, clienteTelefono,
       coordenadas (X: ..., Y: ...) y tiempoEstimado en minutos
     * Si no tiene pedido: mensaje "No tienes pedidos asignados en este momento"
     * Botón "← Volver al mapa" con routerLink="/"
   - Estilos básicos: cards con sombra, estado con badge verde/amarillo/gris

### Reglas
- Standalone component
- Usar signals para todo el estado
- Manejar estado de carga (isLoading signal)
- Importar RouterLink para el botón de volver
```

---

## Prompt Paso B5 — Integrar mock-user en order-form-modal ✅ COMPLETADO

```
Tengo una aplicación Angular 19 standalone.

### Contexto existente
- Ya existe order-form-modal.component.ts con un formulario para confirmar pedido
- El formulario recoge: clienteNombre, clienteTelefono, clienteCoordenadasX, clienteCoordenadasY
- Ya existe frontend/src/app/data/mock-user.ts con MOCK_USER y 3 favoritos

### Lo que necesito

Modificar order-form-modal para agregar un selector de lugar favorito:
1. Al abrir el modal, mostrar un <select> o botones con los alias de los favoritos
   ("Casa", "Trabajo", "Novia") antes de los campos manuales
2. Al seleccionar un favorito, pre-rellenar automáticamente:
   - clienteNombre ← MOCK_USER.nombre
   - clienteTelefono ← MOCK_USER.telefono
   - clienteCoordenadasX ← favorito.coordenadaX
   - clienteCoordenadasY ← favorito.coordenadaY
3. El primer favorito (Casa) debe estar pre-seleccionado cuando se abre el modal
4. El usuario puede seguir editando los campos manualmente después de pre-rellenar

### Reglas
- Importar MOCK_USER desde ../data/mock-user (o la ruta relativa correcta)
- No eliminar la posibilidad de ingreso manual
- Mantener las validaciones existentes del formulario
```

---

## Resumen de archivos a crear/modificar

### order-service (Backend)

| Archivo | Acción | Estado |
|---------|--------|--------|
| `PedidoJpaRepository.java` | Modificar — nueva query | ✅ COMPLETADO |
| `PedidoRepository.java` *(port)* | Modificar — nuevo método | ✅ COMPLETADO |
| `PedidoDataAdapter.java` | Modificar — implementar método | ✅ COMPLETADO |
| `OrderUseCase.java` *(port)* | Modificar — nuevo método | ✅ COMPLETADO |
| `OrderApplicationService.java` | Modificar — implementar | ✅ COMPLETADO |
| `OrderController.java` | Modificar — nuevo GET | ✅ COMPLETADO |
| `OrderApplicationServiceTest.java` | Modificar — 2 nuevos tests | ✅ COMPLETADO |
| `PedidoDataAdapterTest.java` | Modificar — 2 nuevos tests | ✅ COMPLETADO |

### Frontend (Angular 19)

| Archivo | Acción | Estado |
|---------|--------|--------|
| `app/data/mock-user.ts` | Crear | ✅ COMPLETADO |
| `app/app.routes.ts` | Modificar — añadir rutas | ✅ COMPLETADO |
| `app/app.component.ts` | Modificar — router-outlet | ✅ COMPLETADO |
| `app/app.config.ts` | Verificar/añadir provideRouter | ✅ COMPLETADO |
| `app/services/repartidor-order.service.ts` | Crear | ✅ COMPLETADO |
| `app/components/repartidor-page/*.ts/html/css` | Crear | ✅ COMPLETADO |
| `app/components/order-form-modal/*.ts/.html` | Modificar — selector favoritos | ✅ COMPLETADO |
| `app/components/mapa-page/*.html` | Modificar — link a /repartidor | ✅ COMPLETADO |

---

## Riesgos y mitigaciones

| Riesgo | Impacto | Mitigación |
|--------|---------|-----------|
| Repartidor ID=1 no está EN_ENTREGA al probar | La vista muestra "sin pedidos" aunque el endpoint funcione | Crear un pedido primero o resetear estado vía `docker exec` + SQL |
| `app.config.ts` ya tiene `provideRouter` | Duplicar rompe el bootstrap | Leer el archivo antes de modificar |
| `order-form-modal` usa reactive form muy cerrado | Pre-rellenar no funciona con `setValue` si hay validadores | Usar `patchValue()` que es tolerante a campos faltantes |
| Coordenadas bogotanas en el Canvas (longitudes negativas) | El mapa Canvas escala 0-100 y recibirá -74.x | El mapa ya tiene datos reales; verificar que el escalado del Canvas las maneje |
