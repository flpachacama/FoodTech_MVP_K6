# PROMPTS-HU12 — Marcar pedido como entregado

## Análisis de la HU

**Estado:** ✅ COMPLETADA

**Objetivo:** El repartidor, desde la vista `/repartidor`, puede presionar el botón "Marcar como Entregado" sobre su pedido activo. Eso llama al endpoint ya existente en el backend, el pedido pasa a `ENTREGADO` y el repartidor vuelve a `ACTIVO`. La UI se actualiza en consecuencia.

---

## Estado del backend

El backend está **100% implementado desde HU9**. No se requiere ningún cambio.

| Endpoint | Servicio | Puerto | Estado |
|----------|----------|--------|--------|
| `PUT /orders/{id}/deliver` | order-service | 8081 | ✅ YA EXISTE |

**Respuesta del endpoint (200 OK):**
```json
{
  "id": 3,
  "estado": "ENTREGADO",
  "mensaje": "Pedido 3 marcado como entregado. Repartidor 1 liberado."
}
```

El servicio internamente:
1. Cambia el estado del pedido a `ENTREGADO`
2. Llama a `delivery-service` para colocar al repartidor en `ACTIVO`

---

## Inventario de archivos a tocar

| Archivo | Acción | Estado |
|---------|--------|--------|
| `app/models/deliver-order-response.model.ts` | Crear — interfaz `DeliverOrderResponse` | ✅ COMPLETADO |
| `app/models/index.ts` | Modificar — re-exportar nuevo modelo | ✅ COMPLETADO |
| `app/services/repartidor-order.service.ts` | Modificar — agregar método `deliver()` | ✅ COMPLETADO |
| `app/components/repartidor-page/repartidor-page.component.ts` | Modificar — método `marcarEntregado()` + signal `isDelivering` | ✅ COMPLETADO |
| `app/components/repartidor-page/repartidor-page.component.html` | Modificar — botón "Marcar como Entregado" | ✅ COMPLETADO |
| `app/components/repartidor-page/repartidor-page.component.css` | Modificar — estilo del botón de entrega | ✅ COMPLETADO |

---

## Plan de ejecución

### Paso 1 — Agregar modelo `DeliverOrderResponse`

Crear o agregar en la carpeta `models/` la interfaz que mapea la respuesta del endpoint:

```typescript
// app/models/deliver-order-response.model.ts
export interface DeliverOrderResponse {
  id: number;
  estado: string;
  mensaje: string;
}
```

> Si el proyecto ya tiene un barrel `models/index.ts`, exportar desde ahí también.

---

### Paso 2 — Agregar método en `RepartidorOrderService`

Agregar a `repartidor-order.service.ts` el método:

```typescript
deliver(pedidoId: number): Observable<DeliverOrderResponse> {
  return this.http.put<DeliverOrderResponse>(
    `http://localhost:8081/orders/${pedidoId}/deliver`,
    {}
  );
}
```

---

### Paso 3 — Lógica en `RepartidorPageComponent (.ts)`

1. Agregar signal `isDelivering = signal(false)` para bloquear el botón durante la llamada.
2. Agregar método `marcarEntregado()`:
   - Guard: si no hay `pedidoActivo()`, no hacer nada.
   - `isDelivering.set(true)`
   - Llama `repartidorOrderService.deliver(pedidoActivo()!.id)`
   - En `next`:
     - `pedidoActivo.set(null)`
     - Actualiza el signal `repartidor` cambiando solo el campo `estado` a `'ACTIVO'`
     - `isDelivering.set(false)`
   - En `error`:
     - `isDelivering.set(false)`
     - (Opcional: mostrar mensaje de error)

---

### Paso 4 — Botón en el template

Dentro del bloque `@if (!isLoading() && pedidoActivo())`, **después** de la info del pedido y **antes** del botón "Volver al mapa":

```html
<button
  class="btn-entregar"
  (click)="marcarEntregado()"
  [disabled]="isDelivering()">
  {{ isDelivering() ? 'Procesando...' : '✅ Marcar como Entregado' }}
</button>
```

---

### Paso 5 — Estilo del botón

En `repartidor-page.component.css`:

```css
.btn-entregar {
  width: 100%;
  padding: 0.875rem;
  background-color: #16a34a;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 1rem;
  transition: background-color 0.2s;
}

.btn-entregar:hover:not(:disabled) {
  background-color: #15803d;
}

.btn-entregar:disabled {
  background-color: #86efac;
  cursor: not-allowed;
}
```

---

## Flujo completo de la vista tras presionar el botón

```
Repartidor presiona "Marcar como Entregado"
  │
  ├─ isDelivering = true  →  botón se deshabilita / muestra "Procesando..."
  │
  └─ PUT /orders/{pedidoId}/deliver  ──▶  order-service
                                            ├─ pedido → ENTREGADO
                                            └─ repartidor → ACTIVO (via delivery-service)
       │
       ├─ [200 OK]
       │    ├─ pedidoActivo.set(null)          → desaparece card del pedido
       │    ├─ repartidor().estado = 'ACTIVO'  → badge cambia a verde ACTIVO
       │    └─ isDelivering = false
       │
       └─ [Error]
            └─ isDelivering = false  (mostrar toast/mensaje opcional)
```

---

## Pruebas manuales

> **Prerequisito:** Docker corriendo, repartidor con estado `EN_ENTREGA` y pedido activo.

### 1. Preparar escenario — crear pedido

```bash
curl -s -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "restauranteId": 1,
    "restauranteX": -74.0627,
    "restauranteY": 4.6482,
    "clima": "SOLEADO",
    "clienteNombre": "Carlos Mendez",
    "clienteTelefono": "3009876543",
    "clienteCoordenadasX": -74.0503,
    "clienteCoordenadasY": 4.6643,
    "productos": [
      { "id": 1, "nombre": "Hamburguesa Clásica", "precio": 18000 }
    ]
  }' | jq '{id, repartidorId, estado}'
```

Anotar el `id` del pedido y el `repartidorId` asignado.

---

### 2. Verificar que el repartidor está EN_ENTREGA

```bash
# Reemplaza {repartidorId} con el valor del paso anterior
curl -s http://localhost:8080/delivers/{repartidorId} | jq '{id, nombre, estado}'
# Esperado: "estado": "EN_ENTREGA"
```

---

### 3. Marcar como entregado (vía curl)

```bash
# Reemplaza {pedidoId} con el id del pedido creado
curl -s -X PUT http://localhost:8081/orders/{pedidoId}/deliver | jq .
```

**Respuesta esperada (200 OK):**
```json
{
  "id": 1,
  "estado": "ENTREGADO",
  "mensaje": "Pedido 1 marcado como entregado. Repartidor X liberado."
}
```

---

### 4. Verificar que el repartidor volvió a ACTIVO

```bash
curl -s http://localhost:8080/delivers/{repartidorId} | jq '{id, nombre, estado}'
# Esperado: "estado": "ACTIVO"
```

---

### 5. Verificar que el endpoint del repartidor retorna 404

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/orders/repartidor/{repartidorId}
# Esperado: 404 (ya no tiene pedido activo)
```

---

## Prompt de implementación

```
Tengo una aplicación Angular 19 standalone.

### Contexto existente

- Ya existe RepartidorPageComponent en frontend/src/app/components/repartidor-page/
  con signals: isLoading, repartidor, pedidoActivo
- Ya existe RepartidorOrderService en frontend/src/app/services/repartidor-order.service.ts
  con método getOrderByRepartidorId(id): Observable<OrderResponse>
- Ya existe el endpoint PUT http://localhost:8081/orders/{id}/deliver que devuelve:
  { id: number, estado: string, mensaje: string }

### Lo que necesito

1. Agregar en models/ la interfaz DeliverOrderResponse:
   { id: number; estado: string; mensaje: string }

2. Agregar en RepartidorOrderService el método:
   deliver(pedidoId: number): Observable<DeliverOrderResponse>
   que llame PUT http://localhost:8081/orders/{pedidoId}/deliver

3. En RepartidorPageComponent (.ts):
   - Agregar signal isDelivering = signal(false)
   - Agregar método marcarEntregado():
     * Guard: si !pedidoActivo() retornar
     * isDelivering.set(true)
     * Llama repartidorOrderService.deliver(pedidoActivo()!.id)
     * next: pedidoActivo.set(null), actualizar repartidor estado a 'ACTIVO', isDelivering.set(false)
     * error: isDelivering.set(false)

4. En repartidor-page.component.html:
   - Dentro del bloque @if pedidoActivo(), agregar DEBAJO de la info del pedido:
     <button class="btn-entregar" (click)="marcarEntregado()" [disabled]="isDelivering()">
       {{ isDelivering() ? 'Procesando...' : '✅ Marcar como Entregado' }}
     </button>

5. En repartidor-page.component.css:
   - Estilo .btn-entregar: fondo verde (#16a34a), ancho completo, padding 0.875rem,
     border-radius 8px, blanco, deshabilitado con color claro

### Reglas
- Standalone, usar signals para todo el nuevo estado
- No modificar la lógica de carga existente (ngOnInit / cargarPedidoActivo)
- No agregar NgModules
```

---

## Criterio de aceptación visual

| Escenario | Resultado esperado en UI |
|-----------|--------------------------|
| Repartidor con pedido `EN_ENTREGA` | Botón verde "✅ Marcar como Entregado" visible |
| Clic en el botón | Botón cambia a "Procesando..." y se deshabilita |
| Éxito (200) | Card del pedido desaparece, badge del repartidor cambia a `ACTIVO` |
| Error de red | Botón vuelve a habilitarse, no hay cambio de estado |
| Repartidor sin pedido | Botón NO aparece (condición `@if pedidoActivo()`) |
