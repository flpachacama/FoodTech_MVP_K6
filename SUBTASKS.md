# Subtareas consolidadas – DEV & QA

## Visión general

    Se harán 2 microservicios 
    
    1. Microservicio para ordenes que gestionará pedidos, estados y flujos del cliente (order)
    2. Microservicio para repartidores que gestionará estados, ubicaciones y algoritmo de asignación(delivery)
    
    Se hará una interfaz gráfica que consuma ambos microservicios 

---

## Fase 1 - Modelo base

### HU1 - Gestionar estado de repartidores

**Subtareas DEV:**
* Crear entidad del Repartidor con atributos como id, nombre, estado, vehiculo y coordenadas (x,y)
* Crear ENUM para los estados del repartidor (ACTIVO, INACTIVO, EN_ENTREGA)
* Crear ENUM para los vehículos (BICICLETA, MOTO, AUTO)
* Implementar capa de acceso a datos para gestionar repartidores
* Insertar datos iniciales de repartidores en la base de datos

**Subtareas QA:**
* Diseñar casos de prueba por cada estado
* Validar que EN_ENTREGA e INACTIVO no sean seleccionados
* Probar cambios de estado en distintos escenarios
* Validar integridad de estados en flujo completo

---

### HU2 - Filtrar repartidores por cercanía

**Subtareas DEV:**
* Implementar función calcularDistancia(restauranteCoordenadas(x,y), repartidorCoordenadas(x,y)) con distancia euclidiana
* Implementar función obtenerCandidatosCercanos()
* Filtrar solo repartidores con estado ACTIVO antes de calcular
* Ordenar lista por distancia ascedente (el de menor distancia primerio)

**Subtareas QA:**
* Validar cálculo correcto de distancias
* Probar ordenamiento correcto
* Validar comportamiento sin repartidores activos
* Probar casos límite

---

### HU3 - Aplicar restricciones por clima

**Subtareas DEV:**
* Crear Enums para el clima (SOLEADO, LLUVIA_SUAVE, LLUVIA_FUERTE)
* Implemementar función aplicarFiltroClima(candidatos, clima) 
* Crear reglas LLUVIA_FUERTE excluye BICICLETA y MOTO, LLUVIA_SUAVE excluye solo BICICLETA

**Subtareas QA:**
* Probar cada tipo de clima
* Validar exclusión de bici y moto en lluvia fuerte
* Validar combinaciones clima + transporte
* Verificar comportamiento sin candidatos

---

## Fase 2 - Lógica del algoritmo

### HU4 - Calcular prioridad de repartidores

**Subtareas DEV:**
* Definir velocidades de los vehiculos (BICICLETA=15, MOTO=20, AUTO=30)
* Implementar función calcularTiempoEstimado(distancia, vehiculo)
* Ordenar candidatos por tiempo estimado de menor a mayor

**Subtareas QA:**
* Validar cálculo correcto del tiempo estimado
* Probar diferentes combinaciones de transporte
* Validar comportamiento en empates
* Verificar consistencia de resultados

---

### HU5 - Asignar pedido automáticamente

**Subtareas DEV:**
* Crear endpoint POST /delivery 
* Recibir pedidoId, restauranteId
* Ejecutar lógica de filtrado siguiendo el flujo 
    - Filtrar por estado ACTIVO
    - Filtrar por clima
    - Calcular tiempos
    - Ordenarpor tiempo estimado
    - Tomar al primero
* Si la lista está vacía retornar PENDIENTE
* Si hay candidato -> Asignar repartidor al pedido y actualizar estado

Nota: Para esta versión las asignaciones pendientes quedán en ese estado, en futuras versiones se añadirá un (CRON) para reintentos automáticos cada cierto tiempo. 

**Subtareas QA:**
* Validar asignación correcta
* Probar escenarios sin repartidores
* Validar que siempre se elige el mejor candidato
* Verificar consistencia del flujo

---

### HU6 - Actualizar estado del repartidor

**Subtareas DEV:**
* Implementar función cambiarEstado(repartidorId, nuevoEstado) en el servicio de repartidores
* Llamar automaticamente al asignar un pedido a un repartidor y cambié a EN_ENTREGA
* Llamar al dar detectar el evento de entregado y colocarl el repartido en ACTIVO
* Llmar al detectar evento de cancelación y colocar al repartidor en ACTIVO
* Exponer endpoint PUT/delivery/{id}/state 

**Subtareas QA:**
* Probar transición ACTIVO -> EN_ENTREGA
* Probar transición EN_ENTREGA -> ACTIVO
* Validar cambios en cancelación
* Detectar estados inconsistentes

---

## FASE 3 - Flujo de pedidos (Core del negocio)

### HU7 - Agregar productos al carrito

**Subtareas DEV:**
* Crear entidad Pedido con id, estado, restauranteId, productos, clienteId, clienteNombre, ClienteCoordenadas(x,y), tiempoEstimado
* Crear Enum EstadoPedido (PENDIENTE, ASIGNADO, ENTREGADO, CANCELADO)
* Crear entidad ProductoPedido con id, nombre, precio
* Crear capa de acceso a datos para gestionar pedidos 
* Crear tabla para pedidos 
* Crear tabla para almacenar Restaurantes 
* Insertar restaurantes con coodernadas, nombre y menus
* Implementar logica del carrito en el frontend 

**Subtareas QA:**
* Probar agregar múltiples productos
* Validar eliminación de productos
* Verificar cálculo de total
* Validar restricción de carrito vacío

---

### HU8 - Confirmar pedido

**Subtareas DEV:**
* Crear DTO para recibir el pedido con restauranteId, productos[], clienteNombre, clienteCoordenadas(x,y), clienteTelefono
* Crear DTO para la respuesta del pedido con restauranteId, productos[], clienteNombre, clienteCoordenadas(x,y), clienteTelefono, tiempoEstimado, estadoPedido
* Exponer endpoint POST /orders en el servicio de orders
* Al confirmar seguir el siguiente flujo
    - Persistir el pedido 
    - Llamar al servicio de delivery para asignar el pedido
    - Actualizar el estado 
    - Retornar tiempo estimado 
* Comunicación entre servicios via REST 

Nota: Para la comunicación entre servicios, si esta llega a fallar se debe contemplar entre dos opciones politica de reintentos usar un broker de mensajeria como RabbitMQ para manejar estos eventos. 

**Subtareas QA:**
* Validar campos obligatorios
* Probar confirmación exitosa
* Probar sin repartidores disponibles
* Validar visualización del tiempo estimado

---

### HU9 - Cancelar pedido

**Subtareas DEV:**
* Exponer endpoint PUT /orders/{id}/cancel
* Validar que el pedido no esté en ENTREGADO
* Cambiar estado a CANCELADO
* Si tenia repartidor asignado seguir el siguiente flujo
    - Llamar al endpoint PUT /delivery/{id}/state del servicio delivery para liberar al repartidor    (cambiar estado a ACTIVO)
    - Retornar confirmación de cancelación

**Subtareas QA:**
* Validar cancelación antes de asignación
* Verificar cambio de estado
* Validar liberación del repartidor
* Probar multiples cancelaciones

---

## FASE 4 - Interfaz de usuario consumidor

### HU10 - Visualizar y seleccionar restaurante

**Subtareas DEV (Backend - order-service):**
* Crear RestauranteResponseDto con id, nombre, coordenadasX, coordenadasY, menu (lista de productos)
* Crear RestauranteController con endpoints:
  - GET /restaurants → lista todos los restaurantes con sus menús
  - GET /restaurants/{id} → retorna un restaurante específico con su menú
* Crear RestauranteService para la lógica de consulta

**Subtareas DEV (Backend - delivery-service):**
* Crear RepartidorListResponseDto con id, nombre, estado, vehiculo, ubicacionX, ubicacionY
* Agregar endpoint GET /delivery/repartidores en AsignacionController
* Retornar lista de repartidores con sus coordenadas actuales

**Subtareas DEV (Frontend):**
* Crear componente MapaComponent con Canvas para visualizar el mapa
* Crear servicio RestauranteService para consultar GET /restaurants
* Crear servicio RepartidorService para consultar GET /delivery/repartidores
* Renderizar posiciones x,y en el mapa con símbolos específicos:
  - 🏪 Restaurantes (círculo rojo)
  - 🛵 Repartidores (triángulo según vehículo y color según estado)
* Al dar click en restaurante, abrir modal con datos y menú
**Subtareas QA:**
* Validar visualización en mapa
* Probar selección de restaurante
* Verificar carga de menú
* Validar caso sin restaurantes

---

### HU11 - Visualizar pedido asignado

**Subtareas DEV:**
* Crear componente RepartidorPageComponent
* Crear servicio para consultar el estado del pedido y repartidor asignado
* Mostrar datos del cliente y tiempo estimado
* Exponer el endpoint GET/orders/{id}/active-order en el servicios de orders

**Subtareas QA:**
* Validar datos mostrados
* Probar repartidor sin pedidos
* Verificar consistencia de información
* Validar visualización del tiempo

---

### HU12 - Marcar como entregado

**Subtareas DEV:**
* Agregar Botón "Entregar" en el  componente de RepartidosPageComponente
* Al dar click en el botón llamar a PUT/orders/{id}/delivered en order
* El servicio order cambia el pedido a ENTREGADO y notifica al servicio delivery para cambiar el estado del repartidor a ACTIVOy liberarlo. 

**Subtareas QA:**
* Validar cambio de estado
* Verificar retorno a Activo
* Validar desaparición del tiempo
* Probar flujo completo de entrega

---

## Estimación del esfuerzo – Story Points

| HU   | SP (DEV) | SP (QA) | Valor Definido |
|------|----------|---------|----------|
| HU1  | 3        | 3       | 3        |
| HU2  | 2        | 5       | 5      |
| HU3  | 2        | 3       | 3      |
| HU4  | 2        | 5       | 3      |
| HU5  | 5        | 5       | 5        |
| HU6  | 2        | 3       | 3      |
| HU7  | 5        | 5       | 5        |
| HU8  | 5        | 8       | 8      |
| HU9  | 3        | 3       | 3        |
| HU10 | 5        | 3       | 5        |
| HU11 | 2        | 3       | 3      |
| HU12 | 1        | 3       | 2        |
| **Total** | **37** | **49** | **48** |

Para ver la justificación de cada storypoint remitirse a los archivos [SUBTASK_DEV.md](SUBTASK_DEV.md) | [SUBTASK_QA.md](SUBTASK_QA.md) respectivamente.   
