# PROMPTS-HU9 — Cancelar pedido

## Historia de Usuario

**Como** usuario consumidor  
**Quiero** cancelar un pedido  
**Para** evitar que sea procesado

---

## Plan de ejecución

### Paso 1 — Agregar método releaseRepartidor al puerto DeliveryClient
- Archivos: `DeliveryClient.java`
- Dependencias previas: Ninguna
- Por qué este orden: El puerto es el contrato que define cómo nos comunicamos con delivery-service; debe existir antes de usarlo

### Paso 2 — Implementar releaseRepartidor en DeliveryClientAdapter
- Archivos: `DeliveryClientAdapter.java`
- Dependencias previas: Paso 1
- Por qué este orden: La implementación del adaptador necesita el contrato del puerto

### Paso 3 — Crear excepción PedidoNotFoundException
- Archivos: `PedidoNotFoundException.java`
- Dependencias previas: Ninguna
- Por qué este orden: Se necesita para manejar el caso de pedido no encontrado

### Paso 4 — Crear excepción PedidoCancelException
- Archivos: `PedidoCancelException.java`
- Dependencias previas: Ninguna
- Por qué este orden: Se necesita para manejar errores de cancelación (pedido ya entregado)

### Paso 5 — Agregar método cancelOrder al servicio de aplicación
- Archivos: `OrderApplicationService.java`
- Dependencias previas: Pasos 1, 2, 3, 4
- Por qué este orden: El servicio usa el puerto y las excepciones

### Paso 6 — Actualizar OrderUseCase con el nuevo método
- Archivos: `OrderUseCase.java`
- Dependencias previas: Paso 5
- Por qué este orden: El puerto de entrada debe reflejar la nueva capacidad

### Paso 7 — Agregar endpoint PUT /orders/{id}/cancel en el Controller
- Archivos: `OrderController.java`
- Dependencias previas: Pasos 5, 6
- Por qué este orden: El controller expone el endpoint y delega al servicio

### Paso 8 — Manejar excepciones en GlobalExceptionHandler
- Archivos: `OrderExceptionHandler.java`
- Dependencias previas: Pasos 3, 4
- Por qué este orden: Necesita conocer las excepciones para mapearlas a respuestas HTTP

---

## Prompt Paso 1 — Agregar método al puerto DeliveryClient

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
El archivo `DeliveryClient.java` define el puerto de salida para comunicarse con delivery-service.
Ya existe el método `assign()` y los records `DeliveryAssignmentRequest` y `DeliveryAssignmentResponse`.

### Lo que necesito
Agregar al interface `DeliveryClient` un nuevo método:
- `void releaseRepartidor(Long repartidorId)` — libera un repartidor cambiando su estado a ACTIVO

No crear records adicionales para esto, el método es simple.

### Reglas
- No modificar el método assign() existente
- No crear ningún archivo nuevo
- Solo agregar la firma del método
```

---

## Prompt Paso 2 — Implementar releaseRepartidor en DeliveryClientAdapter

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
Archivo: `DeliveryClientAdapter.java` en `infrastructure.web.client`
- Implementa `DeliveryClient`
- Usa `RestTemplate` inyectado
- La URL base del delivery-service está en `@Value("${delivery.service.url}")`
- Ya existe el método `assign()` que hace POST a `/delivery`

El delivery-service ya tiene endpoint PUT /delivery/{id}/state que recibe:
```json
{ "evento": "ACEPTA_PEDIDO" | "ENTREGA_COMPLETADA" }
```
Para liberar repartidor se usa evento "ENTREGA_COMPLETADA"

### Lo que necesito
Implementar el método `releaseRepartidor(Long repartidorId)`:
1. Hacer PUT a `{delivery.service.url}/delivery/{repartidorId}/state`
2. Enviar body: `{"evento": "ENTREGA_COMPLETADA"}`
3. Log de la operación
4. Capturar errores HTTP y loguearlos (no lanzar excepción si falla)

### Reglas
- No modificar el método assign() existente
- Usar el mismo RestTemplate ya inyectado
- Seguir el mismo patrón de manejo de errores que assign()
```

---

## Prompt Paso 3 — Crear excepción PedidoNotFoundException

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
Ya existe `RestauranteNotFoundException` en `domain.exception`

### Lo que necesito
Crear `PedidoNotFoundException.java` en `domain.exception`:
- Extiende `RuntimeException`
- Constructor que recibe `Long pedidoId`
- Mensaje: "Pedido no encontrado con id: {id}"

### Reglas
- Un solo archivo
- Seguir el mismo patrón que RestauranteNotFoundException
```

---

## Prompt Paso 4 — Crear excepción PedidoCancelException

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
Ya existe `PedidoNotFoundException` en `domain.exception`

### Lo que necesito
Crear `PedidoCancelException.java` en `domain.exception`:
- Extiende `RuntimeException`
- Constructor que recibe `Long pedidoId` y `String motivo`
- Mensaje: "No se puede cancelar el pedido {id}: {motivo}"

### Reglas
- Un solo archivo
- Se usará cuando el pedido ya está ENTREGADO o ya CANCELADO
```

---

## Prompt Paso 5 — Agregar método cancelOrder al servicio

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
Archivo: `OrderApplicationService.java` en `application.service`
Ya tiene inyectados:
- `PedidoRepository pedidoRepository` — con métodos `save()`, `findById()`, `findAll()`
- `DeliveryClient deliveryClient` — con métodos `assign()` y `releaseRepartidor()`

El modelo `Pedido` tiene:
- `id`, `estado` (EstadoPedido enum), `repartidorId`, etc.
- EstadoPedido: PENDIENTE, ASIGNADO, ENTREGADO, CANCELADO

### Lo que necesito
Agregar método público `cancelOrder(Long pedidoId)` que:

1. Busca el pedido por id, si no existe lanza `PedidoNotFoundException`
2. Valida que el estado NO sea ENTREGADO ni CANCELADO, si lo es lanza `PedidoCancelException`
3. Si el pedido tiene `repartidorId != null`, llama a `deliveryClient.releaseRepartidor(repartidorId)`
4. Cambia el estado del pedido a CANCELADO (repartidorId = null, tiempoEstimado = null)
5. Guarda el pedido actualizado
6. Retorna un DTO simple con: `id`, `estado`, `mensaje: "Pedido cancelado exitosamente"`
7. Loguea cada paso importante

### Reglas
- No modificar createOrder() ni otros métodos
- Usar Pedido.builder() para crear la copia actualizada
- Importar las excepciones creadas en pasos anteriores
```

---

## Prompt Paso 6 — Actualizar OrderUseCase

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
Archivo: `OrderUseCase.java` en `domain.port.input`
Ya tiene método: `OrderResponseDto createOrder(OrderRequestDto request)`

### Lo que necesito
Agregar la firma del nuevo método:
- `CancelOrderResponseDto cancelOrder(Long pedidoId)`

El DTO `CancelOrderResponseDto` debe tener: `id`, `estado`, `mensaje`
Crear este record/clase en `infrastructure.web.dto`

### Reglas
- No modificar createOrder
- Mantener el interface limpio
```

---

## Prompt Paso 7 — Agregar endpoint en OrderController

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
Archivo: `OrderController.java` en `infrastructure.web.controller`
- Ya tiene `@RestController` con `@RequestMapping("/orders")`
- Ya tiene inyectado `OrderApplicationService` (o OrderUseCase)
- Ya tiene endpoint `POST /orders` para crear pedidos

### Lo que necesito
Agregar endpoint `PUT /orders/{id}/cancel`:
1. Recibe `@PathVariable Long id`
2. Llama a `orderApplicationService.cancelOrder(id)`
3. Retorna `ResponseEntity<CancelOrderResponseDto>` con status 200 OK

### Reglas
- No modificar el endpoint POST existente
- Usar @PutMapping("/{id}/cancel")
- Seguir el mismo estilo de código del controller
```

---

## Prompt Paso 8 — Manejar excepciones en GlobalExceptionHandler

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
Archivo: `OrderExceptionHandler.java` en `infrastructure.web.exception`
Ya maneja:
- `IllegalArgumentException` → 400
- `RestauranteNotFoundException` → 404
- `IllegalStateException` → 502

### Lo que necesito
Agregar handlers para:
1. `PedidoNotFoundException` → 404 NOT_FOUND
   - Response: `{"status": 404, "error": "Pedido no encontrado", "detail": "{mensaje}"}`

2. `PedidoCancelException` → 400 BAD_REQUEST  
   - Response: `{"status": 400, "error": "No se puede cancelar el pedido", "detail": "{mensaje}"}`

### Reglas
- Seguir el mismo patrón de los handlers existentes
- Usar el método buildResponse() si existe
- Importar las excepciones creadas
```

---

## Resumen de archivos a crear/modificar

| Paso | Archivo | Acción |
|------|---------|--------|
| 1 | `domain/port/output/DeliveryClient.java` | Modificar |
| 2 | `infrastructure/web/client/DeliveryClientAdapter.java` | Modificar |
| 3 | `domain/exception/PedidoNotFoundException.java` | **Crear** |
| 4 | `domain/exception/PedidoCancelException.java` | **Crear** |
| 5 | `application/service/OrderApplicationService.java` | Modificar |
| 6 | `domain/port/input/OrderUseCase.java` + `infrastructure/web/dto/CancelOrderResponseDto.java` | Modificar + **Crear** |
| 7 | `infrastructure/web/controller/OrderController.java` | Modificar |
| 8 | `infrastructure/web/exception/OrderExceptionHandler.java` | Modificar |

---

## Pruebas sugeridas (curl)

```bash
# Cancelar pedido PENDIENTE (sin repartidor)
curl -X PUT http://localhost:8081/orders/1/cancel

# Cancelar pedido ASIGNADO (con repartidor) - debe liberar repartidor
curl -X PUT http://localhost:8081/orders/2/cancel

# Cancelar pedido inexistente - debe retornar 404
curl -X PUT http://localhost:8081/orders/9999/cancel

# Cancelar pedido ya ENTREGADO - debe retornar 400
curl -X PUT http://localhost:8081/orders/3/cancel
```
