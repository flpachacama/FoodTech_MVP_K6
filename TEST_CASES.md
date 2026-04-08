# TEST CASES - FoodTech MVP

Documento de casos de prueba funcionales para HU1-HU10 del MVP FoodTech.

## HU1 - Gestionar estado de repartidores

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU1 | TC-001 | Dado un repartidor en estado ACTIVO<br>Cuando el sistema valida disponibilidad para asignación<br>Entonces el repartidor debe quedar habilitado como candidato | Repartidor registrado en el sistema | Estado repartidor: ACTIVO | 1) Crear repartidor ACTIVO<br>2) Ejecutar validación de disponibilidad<br>3) Consultar lista de candidatos | El repartidor aparece en la lista de candidatos elegibles | Aprobado | Implementado | Crítico |
| HU1 | TC-002 | Dado un repartidor en estado EN_ENTREGA<br>Cuando el sistema valida disponibilidad para un nuevo pedido<br>Entonces el repartidor no debe ser considerado | Repartidor con pedido en curso | Estado repartidor: EN_ENTREGA | 1) Configurar repartidor EN_ENTREGA<br>2) Ejecutar validación de disponibilidad<br>3) Verificar candidatos | El repartidor no aparece en la lista de candidatos | Aprobado | Implementado | Alto |
| HU1 | TC-003 | Dado un repartidor en estado INACTIVO<br>Cuando el sistema busca candidatos para asignación<br>Entonces el repartidor debe ser excluido | Repartidor registrado | Estado repartidor: INACTIVO | 1) Configurar repartidor INACTIVO<br>2) Lanzar búsqueda de candidatos<br>3) Revisar resultado | El repartidor no se incluye como candidato | Aprobado | Implementado | Alto |

## HU2 - Filtrar repartidores por cercania

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU2 | TC-004 | Dado repartidores ACTIVOS con coordenadas validas<br>Cuando el sistema calcula distancia al restaurante<br>Entonces debe devolver la lista ordenada de menor a mayor distancia | Restaurante y repartidores existentes | Restaurante (5,5); R1 (6,5), R2 (10,10), R3 (5,6) | 1) Cargar restaurante y repartidores ACTIVOS<br>2) Ejecutar filtro por cercania<br>3) Verificar orden de salida | Lista ordenada correctamente por distancia ascendente | Aprobado | Implementado | Crítico |
| HU2 | TC-005 | Dado que no hay repartidores en estado ACTIVO<br>Cuando el sistema calcula cercania<br>Entonces debe retornar una lista vacia | Todos los repartidores en INACTIVO o EN_ENTREGA | Estados: INACTIVO, EN_ENTREGA | 1) Configurar repartidores no disponibles<br>2) Ejecutar filtro de cercania<br>3) Revisar lista | Se retorna lista vacia sin errores | Aprobado | Implementado | Alto |
| HU2 | TC-006 | Dado un repartidor ACTIVO sin coordenadas completas<br>Cuando el sistema calcula distancia<br>Entonces el repartidor invalido debe excluirse del ordenamiento | Restaurante disponible | Restaurante (5,5); R1 ACTIVO con X nulo | 1) Registrar repartidor con coordenada faltante<br>2) Ejecutar filtro por cercania<br>3) Revisar candidatos y logs funcionales | El repartidor con datos incompletos no participa y el proceso continua | Aprobado | Implementado | Medio |

## HU3 - Aplicar restricciones por clima

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU3 | TC-007 | Dado clima Lluvia_Fuerte y repartidores con bicicleta, moto y auto<br>Cuando el sistema evalua candidatos<br>Entonces debe excluir bicicleta y moto | Repartidores ACTIVOS disponibles | Clima: Lluvia_Fuerte; Transportes: bicicleta, moto, auto | 1) Configurar clima Lluvia_Fuerte<br>2) Ejecutar evaluacion de candidatos<br>3) Verificar transportes habilitados | Solo repartidores en auto quedan elegibles | Aprobado | Implementado | Crítico |
| HU3 | TC-008 | Dado clima Lluvia_suave y repartidores en bicicleta, moto y auto<br>Cuando el sistema aplica reglas de clima<br>Entonces debe excluir bicicleta y permitir moto/auto | Repartidores ACTIVOS con transportes mixtos | Clima: Lluvia_suave | 1) Configurar clima Lluvia_suave<br>2) Ejecutar filtro por clima<br>3) Validar candidatos resultantes | Bicicleta queda excluida; moto y auto continúan | Aprobado | Implementado | Alto |
| HU3 | TC-009 | Dado un valor de clima no soportado<br>Cuando el sistema intenta aplicar restricciones<br>Entonces debe rechazar la operacion con mensaje de validacion | Servicio de asignacion habilitado | Clima: TORMENTA_EXTREMA | 1) Enviar clima no valido<br>2) Ejecutar evaluacion de candidatos<br>3) Revisar respuesta del sistema | El sistema no asigna y retorna error de clima invalido | Aprobado | Implementado | Medio |

## HU4 - Calcular prioridad de repartidores

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU4 | TC-010 | Dado un repartidor en bicicleta a distancia 10 y otro en moto a distancia 15<br>Cuando el sistema calcula tiempo estimado<br>Entonces la moto debe tener mayor prioridad | Repartidores ACTIVOS y clima compatible | Bici dist=10; Moto dist=15 | 1) Configurar candidatos<br>2) Ejecutar calculo de prioridad<br>3) Comparar tiempos estimados | El repartidor en moto queda primero por menor tiempo estimado | Aprobado | Implementado | Crítico |
| HU4 | TC-011 | Dado dos repartidores en moto con distancias distintas<br>Cuando el sistema calcula prioridad<br>Entonces debe ganar el mas cercano | Repartidores ACTIVOS | Moto A dist=10; Moto B dist=20 | 1) Configurar dos motos<br>2) Ejecutar priorizacion<br>3) Revisar ranking | Moto a distancia 10 queda con mayor prioridad | Aprobado | Implementado | Alto |
| HU4 | TC-012 | Dado dos repartidores con igual tiempo estimado<br>Cuando el sistema prioriza candidatos<br>Entonces debe seleccionar uno sin fallar ni dejar pedido sin candidato | Repartidores ACTIVOS con ETA equivalente | Candidato A ETA=8; Candidato B ETA=8 | 1) Configurar empate de ETA<br>2) Ejecutar priorizacion<br>3) Verificar seleccion final | Se selecciona un candidato valido y el sistema no genera error | Aprobado | Implementado | Medio |

## HU5 - Asignar pedido automaticamente

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU5 | TC-013 | Dado un pedido en estado PENDIENTE y candidatos validos<br>Cuando el sistema ejecuta la asignacion automatica<br>Entonces debe asignar el pedido al mejor candidato | Pedido creado y repartidores elegibles | Pedido PENDIENTE; lista de candidatos con ETA | 1) Crear pedido PENDIENTE<br>2) Ejecutar algoritmo de asignacion<br>3) Consultar pedido actualizado | Pedido cambia a ASIGNADO con repartidor de menor ETA | Aprobado | Implementado | Crítico |
| HU5 | TC-014 | Dado un pedido en estado PENDIENTE sin candidatos validos<br>Cuando el sistema intenta asignar<br>Entonces el pedido debe permanecer PENDIENTE | Pedido existente | Sin repartidores disponibles | 1) Crear pedido PENDIENTE<br>2) Ejecutar asignacion<br>3) Verificar estado final | Pedido permanece PENDIENTE y se informa indisponibilidad | Aprobado | Implementado | Crítico |
| HU5 | TC-015 | Dado candidatos registrados pero todos en INACTIVO o EN_ENTREGA<br>Cuando se ejecuta la asignacion<br>Entonces no se debe asignar repartidor | Repartidores existentes no disponibles | Estados repartidores: INACTIVO/EN_ENTREGA | 1) Configurar repartidores no aptos<br>2) Ejecutar algoritmo<br>3) Consultar resultado | El pedido no se asigna y conserva estado PENDIENTE | Aprobado | Implementado | Alto |
| HU5 | TC-016 | Dado clima Lluvia_Fuerte y solo repartidores en bicicleta/moto<br>Cuando el sistema asigna automaticamente<br>Entonces no debe asignar pedido por restriccion climatica | Pedido PENDIENTE y clima configurado | Clima: Lluvia_Fuerte; transportes: bicicleta, moto | 1) Configurar pedido y clima<br>2) Ejecutar asignacion<br>3) Verificar estado del pedido | Pedido queda PENDIENTE por falta de candidatos aptos | Aprobado | Implementado | Alto |

## HU6 - Actualizar estado del repartidor

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU6 | TC-017 | Dado un repartidor ACTIVO asignado a un pedido<br>Cuando se confirma la asignacion<br>Entonces el estado del repartidor debe pasar a EN_ENTREGA | Repartidor ACTIVO y pedido asignable | Estado inicial: ACTIVO | 1) Asignar pedido al repartidor<br>2) Confirmar evento de asignacion<br>3) Consultar estado repartidor | El repartidor cambia de ACTIVO a EN_ENTREGA | Aprobado | Implementado | Crítico |
| HU6 | TC-018 | Dado un repartidor EN_ENTREGA con pedido activo<br>Cuando marca el pedido como entregado<br>Entonces el estado del repartidor debe volver a ACTIVO | Pedido en ruta | Estado inicial: EN_ENTREGA; pedido EN_ENTREGA | 1) Simular entrega finalizada<br>2) Ejecutar cambio de estado<br>3) Consultar estado repartidor | El repartidor vuelve a ACTIVO | Aprobado | Implementado | Crítico |
| HU6 | TC-019 | Dado un repartidor EN_ENTREGA con pedido ASIGNADO<br>Cuando el usuario cancela el pedido<br>Entonces el repartidor debe volver a ACTIVO | Pedido asignado a repartidor | Estado pedido: ASIGNADO; estado repartidor: EN_ENTREGA | 1) Cancelar pedido activo<br>2) Procesar evento de cancelacion<br>3) Validar estado final del repartidor | Repartidor queda ACTIVO y disponible | Aprobado | Implementado | Alto |

## HU7 - Generar pedido

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU7 | TC-020 | Dado un usuario con restaurante seleccionado<br>Cuando agrega uno o mas productos al carrito<br>Entonces el carrito muestra items y total correcto | Restaurante y menu disponibles | Productos: P1 x1, P2 x2 | 1) Seleccionar restaurante<br>2) Agregar productos al carrito<br>3) Validar subtotal y total | Carrito refleja cantidad y total calculado correctamente | Aprobado | Implementado | Crítico |
| HU7 | TC-021 | Dado un carrito con un unico producto<br>Cuando el usuario elimina ese producto<br>Entonces el carrito queda vacio | Carrito con 1 item | Item unico en carrito | 1) Cargar carrito con 1 producto<br>2) Eliminar producto<br>3) Verificar carrito | Carrito queda vacio y total en 0 | Aprobado | Implementado | Alto |
| HU7 | TC-022 | Dado un usuario con carrito vacio<br>Cuando intenta avanzar a confirmacion<br>Entonces el sistema debe bloquear el flujo y mostrar mensaje | Restaurante seleccionado | Carrito sin productos | 1) Asegurar carrito vacio<br>2) Intentar continuar flujo<br>3) Validar mensaje | No se permite avanzar y se informa que el carrito esta vacio | Aprobado | Implementado | Alto |
| HU7 | TC-023 | Dado un usuario en menu de productos<br>Cuando intenta agregar cantidad 0 o negativa<br>Entonces el sistema rechaza la accion | Menu cargado | Cantidad: 0 y -1 | 1) Seleccionar producto<br>2) Intentar agregar cantidad invalida<br>3) Revisar carrito | No se agrega el producto y se muestra validacion de cantidad | Aprobado | Implementado | Medio |

## HU8 - Confirmar pedido

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU8 | TC-024 | Dado un carrito con productos y datos personales completos<br>Cuando el usuario confirma el pedido<br>Entonces la orden se registra en ASIGNADO y muestra tiempo estimado | Carrito valido y repartidores disponibles | Nombre, telefono, coordenadas X,Y validas | 1) Completar datos obligatorios<br>2) Confirmar pedido<br>3) Verificar estado y ETA | Pedido queda ASIGNADO y ETA visible para el usuario | Aprobado | Implementado | Crítico |
| HU8 | TC-025 | Dado un pedido confirmado sin repartidores aptos<br>Cuando el sistema intenta asignar automaticamente<br>Entonces la orden queda PENDIENTE y sin ETA | Carrito valido sin candidatos | Datos completos; sin repartidores disponibles | 1) Confirmar pedido con datos completos<br>2) Ejecutar asignacion<br>3) Revisar estado y mensaje | Pedido queda PENDIENTE, sin ETA y con notificacion de indisponibilidad | Aprobado | Implementado | Crítico |
| HU8 | TC-026 | Dado un carrito con productos<br>Cuando el usuario confirma sin telefono<br>Entonces el sistema debe bloquear la confirmacion por campos obligatorios | Carrito valido | Nombre y coordenadas; telefono vacio | 1) Completar formulario incompleto<br>2) Confirmar pedido<br>3) Validar respuesta | No se genera pedido y se muestran campos requeridos | Aprobado | Implementado | Alto |
| HU8 | TC-027 | Dado un carrito con productos<br>Cuando el usuario ingresa coordenadas fuera del rango permitido<br>Entonces el sistema debe rechazar la confirmacion | Validaciones de coordenadas habilitadas | Coordenadas X,Y fuera de rango simulado | 1) Ingresar datos con coordenadas invalidas<br>2) Intentar confirmar<br>3) Revisar validacion | Pedido no se confirma y se muestra error de coordenadas invalidas | Aprobado | Implementado | Medio |

## HU9 - Cancelar pedido

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU9 | TC-028 | Dado un usuario con pedido activo antes de asignacion<br>Cuando cancela el pedido<br>Entonces el sistema debe cambiar el estado a CANCELADO | Pedido en estado PENDIENTE | Pedido activo sin repartidor | 1) Crear pedido PENDIENTE<br>2) Ejecutar cancelacion<br>3) Consultar estado | Pedido queda en estado CANCELADO | Aprobado | Implementado | Crítico |
| HU9 | TC-029 | Dado un pedido en estado ASIGNADO con repartidor EN_ENTREGA<br>Cuando el usuario cancela<br>Entonces el pedido pasa a CANCELADO y el repartidor vuelve a ACTIVO | Pedido asignado | Pedido ASIGNADO; repartidor EN_ENTREGA | 1) Configurar pedido asignado<br>2) Cancelar pedido<br>3) Verificar estados de pedido y repartidor | Pedido CANCELADO y repartidor ACTIVO | Aprobado | Implementado | Crítico |
| HU9 | TC-030 | Dado un pedido ya ENTREGADO<br>Cuando el usuario intenta cancelarlo<br>Entonces el sistema debe rechazar la operacion | Pedido en estado ENTREGADO | Estado pedido: ENTREGADO | 1) Seleccionar pedido entregado<br>2) Intentar cancelar<br>3) Revisar respuesta | Cancelacion denegada con mensaje de estado no cancelable | Aprobado | Implementado | Medio |

## HU10 - Visualizar y seleccionar restaurante

| Historia de Usuario | ID del Caso | Escenario | Precondiciones | Datos de entrada | Pasos de ejecución | Resultado esperado | Resultado obtenido | Estado | Prioridad |
|---|---|---|---|---|---|---|---|---|---|
| HU10 | TC-031 | Dado restaurantes registrados en el sistema<br>Cuando el usuario accede al mapa<br>Entonces debe visualizar nombre y ubicacion de todos los restaurantes | Restaurantes hardcodeados cargados | Lista de restaurantes con coordenadas | 1) Abrir vista de mapa<br>2) Cargar restaurantes<br>3) Validar elementos visibles | Se muestran todos los restaurantes con nombre y ubicacion | Aprobado | Implementado | Alto |
| HU10 | TC-032 | Dado el mapa con restaurantes disponibles<br>Cuando el usuario selecciona un restaurante<br>Entonces debe visualizar su menu con productos y precios | Restaurante con menu configurado | Restaurante seleccionado: La Parrilla | 1) Abrir mapa<br>2) Seleccionar restaurante<br>3) Revisar menu mostrado | Se muestra menu correcto del restaurante seleccionado | Aprobado | Implementado | Crítico |
| HU10 | TC-033 | Dado que no existen restaurantes registrados<br>Cuando el usuario accede al mapa<br>Entonces debe ver mensaje de no disponibilidad | Base de restaurantes vacia | Lista de restaurantes: vacia | 1) Configurar sin restaurantes<br>2) Abrir mapa<br>3) Verificar mensaje | El sistema informa que no hay restaurantes disponibles | Aprobado | Implementado | Medio |
