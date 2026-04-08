# Plan de cobertura Jacoco — order-service

**Fecha de inicio:** 07 abril 2026  
**Cobertura inicial:** 45% instrucciones / 9% ramas  
**Objetivo:** ≥ 80% instrucciones / ≥ 70% ramas

---

## Estado actual

| Clase | Cobertura inicial | Estado |
|---|---|---|
| `OrderController` | 0% | ✅ Cubierto (Paso 1) |
| `OrderExceptionHandler` | 0% | ✅ Cubierto (Paso 1) |
| `DeliveryClientAdapter` | 0% | ✅ Cubierto (Paso 2) |
| `OrderApplicationService` | 90% inst / 68% branches | ✅ Cubierto (Paso 3) |
| `OrderRequestDto` | 23% | ✅ Cubierto (Paso 4) |
| `OrderResponseDto` | 9% | ✅ Cubierto (Paso 4) |
| `ProductoPedidoDto` | 15% | ✅ Cubierto (Paso 4) |
| `RestauranteEntity` | 50% | ✅ Cubierto (Paso 4) |

---

## ✅ Paso 1 — OrderControllerTest (COMPLETADO)

**Archivo:** `src/test/java/com/foodtech/infrastructure/web/controller/OrderControllerTest.java`  
**Técnica:** `@WebMvcTest` + `@MockBean` de `OrderUseCase`  
**Resultado:** 16/16 tests pasando

| Test | Tipo |
|---|---|
| `createOrder_conRequestValido_retorna201YCuerpoEsperado` | Happy path |
| `createOrder_cuandoRestauranteNoExiste_retorna404` | Sad path |
| `createOrder_cuandoDeliveryFalla_retorna502` | Sad path |
| `createOrder_cuandoDatosInvalidos_retorna400ConIllegalArgument` | Sad path |
| `createOrder_cuandoExcepcionGenerica_retorna500` | Sad path |
| `cancelOrder_conPedidoAsignado_retorna200` | Happy path |
| `cancelOrder_cuandoPedidoYaCancelado_retorna400` | Sad path |
| `cancelOrder_cuandoPedidoYaEntregado_retorna400` | Sad path |
| `cancelOrder_cuandoPedidoNoEncontrado_retorna404` | Sad path |
| `deliverOrder_conPedidoAsignado_retorna200` | Happy path |
| `deliverOrder_cuandoPedidoPendiente_retorna400` | Sad path |
| `deliverOrder_cuandoPedidoCancelado_retorna400` | Sad path |
| `deliverOrder_cuandoPedidoYaEntregado_retorna400` | Sad path |
| `deliverOrder_cuandoPedidoNoEncontrado_retorna404` | Sad path |
| `getOrderByRepartidorId_conPedidoActivo_retorna200` | Happy path |
| `getOrderByRepartidorId_cuandoSinPedidoActivo_retorna404` | Sad path |

---

## ✅ Paso 2 — DeliveryClientAdapterTest (COMPLETADO)

**Archivo:** `src/test/java/com/foodtech/infrastructure/web/client/DeliveryClientAdapterTest.java`  
**Técnica:** Mockito puro (`@ExtendWith(MockitoExtension.class)`) + `ReflectionTestUtils` para inyectar `@Value`  
**Resultado:** 6/6 tests pasando

| Test | Tipo |
|---|---|
| `assign_cuandoDeliveryRespondeOk_retornaResponse` | Happy path |
| `assign_cuandoDeliveryRetornaNull_lanzaRuntimeException` | Sad path |
| `assign_cuandoDeliveryLanzaRestClientResponseException_wrappea` | Sad path |
| `releaseRepartidor_cuandoOk_noLanzaExcepcion` | Happy path |
| `releaseRepartidor_cuandoRestClientResponseException_tragarExcepcionSinPropagar` | Sad path |
| `releaseRepartidor_cuandoExcepcionGenerica_tragarExcepcionSinPropagar` | Sad path |

---

## ✅ Paso 3 — Ampliar OrderApplicationServiceTest (COMPLETADO)

**Archivo:** `src/test/java/com/foodtech/application/service/OrderApplicationServiceTest.java`  
**Técnica:** Agregar casos al test existente (Mockito puro)  
**Resultado:** 22/22 tests pasando (14 nuevos casos agregados)

| Test a agregar | Rama que cubre | Tipo |
|---|---|---|
| `cancelOrder_conPedidoAsignadoYRepartidor_cancelaYLiberaRepartidor` | `repartidorId != null` | Happy path |
| `cancelOrder_conPedidoPendienteSinRepartidor_cancelaSinLlamarDelivery` | `repartidorId == null` | Happy path |
| `cancelOrder_cuandoPedidoNoEncontrado_lanzaNotFoundException` | `orElseThrow` cancelOrder | Sad path |
| `cancelOrder_cuandoPedidoEntregado_lanzaCancelException` | branch `ENTREGADO` | Sad path |
| `cancelOrder_cuandoPedidoCancelado_lanzaCancelException` | branch `CANCELADO` | Sad path |
| `deliverOrder_conPedidoAsignado_marcaEntregadoYLiberaRepartidor` | flujo normal deliver | Happy path |
| `deliverOrder_cuandoPedidoPendiente_lanzaDeliverException` | branch `PENDIENTE` | Sad path |
| `deliverOrder_cuandoPedidoEntregado_lanzaDeliverException` | branch ya `ENTREGADO` | Sad path |
| `deliverOrder_cuandoPedidoCancelado_lanzaDeliverException` | branch `CANCELADO` | Sad path |
| `deliverOrder_cuandoPedidoNoEncontrado_lanzaNotFoundException` | `orElseThrow` deliver | Sad path |
| `createOrder_cuandoClienteNombreBlank_lanzaIllegalArgument` | branch nombre blank | Sad path |
| `createOrder_cuandoTelefonoNull_lanzaIllegalArgument` | branch null teléfono | Sad path |
| `createOrder_cuandoCoordenadasNull_lanzaIllegalArgument` | branch null coords | Sad path |
| `createOrder_cuandoTiempoEstimadoNullEnDelivery_usaCero` | ternario `tiempoEstimado` | Edge case |

---

## ✅ Paso 4 — DTO Tests (COMPLETADO)

**Archivos nuevos a crear:**
- `src/test/java/com/foodtech/infrastructure/web/dto/OrderRequestDtoTest.java`
- `src/test/java/com/foodtech/infrastructure/web/dto/OrderResponseDtoTest.java`
- `src/test/java/com/foodtech/infrastructure/web/dto/ProductoPedidoDtoTest.java`

**Técnica:** Tests unitarios directos, sin Spring  
**Resultado:** 18/18 tests pasando (6 por cada DTO)

| Test por DTO | Qué cubre |
|---|---|
| Constructor `@AllArgsConstructor` con todos los campos | constructor completo |
| Builder con todos los campos y lectura con getters | builder + getters |
| Setters individuales de cada campo | setters |
| `equals()` dos objetos iguales → `true` | rama equals positiva |
| `equals()` objetos distintos → `false` | rama equals negativa |
| `toString()` no es nulo y contiene el id | toString Lombok |

---

## Proyección de cobertura final

| Métrica | Inicial | Tras Paso 1+2 | Tras Paso 3+4 (estimado) |
|---|---|---|---|
| Instrucciones | 45% | ~62% | ~82% |
| Ramas | 9% | ~25% | ~72% |
| Métodos | 40% | ~65% | ~85% |
