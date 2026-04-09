# Subtasks Tecnicas: Migracion de Coordenadas

## Objetivo
Migrar todo el sistema de coordenadas del proyecto para que use `Double` de extremo a extremo, permitiendo valores negativos y eliminando la restriccion historica de `0-100`.

## Regla base
- Las coordenadas de negocio dejan de ser una grilla limitada.
- El backend debe aceptar y persistir `Double`.
- El frontend debe permitir ingresar decimales y negativos.
- La visualizacion del mapa se considera logica de presentacion, no una restriccion del dominio.

## Orden de ejecucion propuesto
1. `delivery-service`
2. `order-service`
3. `frontend`
4. pruebas y documentacion

---

## Fase 1: delivery-service ✅ COMPLETADA Y PROBADA

### Subtasks tecnicas
1. Cambiar el modelo de dominio de coordenadas.
   - Actualizar `Coordenada` de `int` a `Double`.
   - Eliminar validaciones que bloquean negativos o valores mayores a 100.
   - Revisar `distanciaA` para asegurar compatibilidad con `Double`.

2. Migrar entidades y DTOs de repartidor.
   - Cambiar `RepartidorEntity` a `Double` en `x` e `y`.
   - Cambiar `RepartidorResponseDTO` y `RepartidorListResponseDto` a `Double`.
   - Cambiar `AsignacionRequestDTO` para recibir `restauranteX` y `restauranteY` como `Double`.

3. Revisar logica de asignacion.
   - Validar que `AsignacionApplicationService` y `AsignacionService` no dependan de enteros.
   - Verificar ordenamiento, calculo de distancia y comparaciones.

4. Actualizar datos de prueba.
   - Migrar `data.sql` con coordenadas reales o decimales.
   - Ajustar seeds para incluir valores negativos si aplica.

5. Actualizar pruebas.
   - Cambiar `CoordenadaTest` para eliminar supuestos de rango `0-100`.
   - Revisar pruebas de persistencia y de controlador.

### Criterio de salida ✅
- El servicio compila. ✅
- Los tests pasan (44 tests, 0 fallos). ✅
- El endpoint de asignacion acepta coordenadas decimales y negativas. ✅

---

## Fase 2: order-service ✅ COMPLETADA Y PROBADA

### Subtasks tecnicas
1. Migrar modelos de dominio y persistencia.
   - Cambiar `Pedido` a `Double` en coordenadas del cliente.
   - Cambiar `PedidoEntity` y `RestauranteEntity` a `Double`.

2. Migrar DTOs.
   - Cambiar `OrderRequestDto`, `OrderResponseDto` y `RestauranteResponseDto`.
   - Revisar validaciones y mapeos entre capas.

3. Actualizar cliente interno hacia delivery.
   - Cambiar `DeliveryClient` para enviar coordenadas `Double`.
   - Revisar adaptadores y mappers que construyen el request de asignacion.

4. Actualizar esquema y datos.
   - Cambiar `restaurantes.sql` de `INTEGER` a tipo decimal compatible.
   - Ajustar `data.sql` con valores nuevos.

5. Revisar flujo de creacion de pedidos.
   - Confirmar que `POST /orders` sigue funcionando con `Double`.
   - Verificar que `cancel` y `deliver` no dependan de coordenadas enteras.

### Criterio de salida ✅
- El contrato entre order-service y delivery-service queda alineado. ✅
- No quedan tipos `Integer` en coordenadas del flujo de pedido. ✅
- El servicio compila y sus tests pasan (25 tests, 0 fallos). ✅
- Endpoints validados con pruebas Karate. ✅

---

## Fase 3: frontend ⏳ PENDIENTE 

### Subtasks tecnicas
1. Ajustar modelos TypeScript.
   - Revisar `restaurante.model.ts`, `deliver.model.ts`, `order-request.model.ts` y `order-response.model.ts`.
   - Confirmar que no existan conversiones a enteros.

2. Eliminar restriccion de rango en formularios.
   - Quitar `Validators.min(0)` y `Validators.max(100)` del formulario.
   - Eliminar `min` y `max` del HTML.
   - Actualizar mensajes de error y placeholders.

3. Revisar el mapa.
   - Separar coordenada de negocio de la proyeccion visual.
   - Evitar que la conversion a Google Maps dependa de una grilla fija.
   - Verificar soporte para coordenadas negativas.

4. Revisar servicios que consumen coordenadas.
   - Confirmar que `OrderService`, `DeliverService`, `CartService` y `ActiveOrdersService` sigan funcionando con el nuevo contrato.
   - Validar que no haya formateos que asuman enteros.

### Criterio de salida
- El formulario acepta decimales y negativos.
- El mapa no falla con valores fuera de la vieja grilla.
- El frontend construye requests compatibles con el backend nuevo.

---

## Fase 4: pruebas y documentacion

### Subtasks tecnicas
1. Actualizar pruebas unitarias y de integracion.
   - Cambiar tests que esperan limites `0-100`.
   - Agregar casos con coordenadas negativas y decimales.

2. Actualizar documentacion.
   - Revisar READMEs de backend y frontend.
   - Revisar `CONTEXT.md`, prompts HU y cualquier ejemplo JSON con enteros.

3. Ejecutar verificacion completa.
   - Backend: compilacion y tests por microservicio.
   - Frontend: build y pruebas basicas de formulario/mapa.
   - Validacion manual de endpoints con coordenadas reales.

### Criterio de salida
- No quedan referencias funcionales a la restriccion `0-100`.
- La documentacion refleja el nuevo contrato.
- Las pruebas cubren coordenadas reales, negativas y decimales.

---

## Dependencias criticas
- El cambio debe hacerse de forma coordinada entre backend y frontend.
- No conviene dejar un microservicio actualizado y el otro no, porque rompe el contrato entre servicios.
- La logica de visualizacion del mapa debe mantenerse independiente del dominio.

## Riesgos
- Conversiones automaticas de JSON pueden truncar o redondear valores si algun mapper sigue esperando enteros.
- El mapa puede requerir ajustes de escala si se siguen usando coordenadas reales sin normalizacion.
- Los datos semilla viejos pueden ocultar fallos si no se actualizan las pruebas.
