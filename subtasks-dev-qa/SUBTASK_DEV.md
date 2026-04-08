# Subtareas técnicas enfocadas en desarrollo 

## Visión general 

    Se harán 2 microservicios 
    
    1. Microservicio para ordenes que gestionará pedidos, estados y flujos del cliente (order)
    2. Microservicio para repartidores que gestionará estados, ubicaciones y algoritmo de asignación(delivery)
    
    Se hará una interfaz gráfica que consuma ambos microservicios 

## Tareas técnicas por HU y por fases

### Fase 1 - Modelo base
---
#### HU1 - Gestionar estado de repartidores
---
* Crear entidad del Repartidor con atributos como id, nombre, estado, vehiculo y coordenadas (x,y)
* Crear ENUM para los estados del repartidor (ACTIVO, INACTIVO, EN_ENTREGA)
* Crear ENUM para los vehículos (BICICLETA, MOTO, AUTO)
* Implementar capa de acceso a datos para gestionar repartidores
* Insertar datos iniciales de repartidores en la base de datos

#### HU2 - Filtrar repartidores por cercanía
---
* Implementar función calcularDistancia(restauranteCoordenadas(x,y), repartidorCoordenadas(x,y)) con distancia euclidiana
* Implementar función obtenerCandidatosCercanos()
* Filtrar solo repartidores con estado ACTIVO antes de calcular
* Ordenar lista por distancia ascedente (el de menor distancia primerio)

#### HU3 - Aplicar restricciones por clima
---
* Crear Enums para el clima (SOLEADO, LLUVIA_SUAVE, LLUVIA_FUERTE)
* Implemementar función aplicarFiltroClima(candidatos, clima) 
* Crear reglas LLUVIA_FUERTE excluye BICICLETA y MOTO, LLUVIA_SUAVE excluye solo BICICLETA
---
### Fase 2 - Lógica del algoritmo
---
#### HU4 - Calcular prioridad de repartidores
* Definir velocidades de los vehiculos (BICICLETA=15, MOTO=20, AUTO=30)
* Implementar función calcularTiempoEstimado(distancia, vehiculo)
* Ordenar candidatos por tiempo estimado de menor a mayor

#### HU5 - Asignar pedido automáticamente
---
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
#### HU6 - Actualizar estado del repartidor
---
* Implementar función cambiarEstado(repartidorId, nuevoEstado) en el servicio de repartidores
* Llamar automaticamente al asignar un pedido a un repartidor y cambié a EN_ENTREGA
* Llamar al dar detectar el evento de entregado y colocarl el repartido en ACTIVO
* Llmar al detectar evento de cancelación y colocar al repartidor en ACTIVO
* Exponer endpoint PUT/delivery/{id}/state 
---
### FASE 3 - Flujo de pedidos (Core del negocio)
---
#### HU7 - Agregar productos al carrito
---
* Crear entidad Pedido con id, estado, restauranteId, productos, clienteId, clienteNombre, ClienteCoordenadas(x,y), tiempoEstimado
* Crear Enum EstadoPedido (PENDIENTE, ASIGNADO, ENTREGADO, CANCELADO)
* Crear entidad ProductoPedido con id, nombre, precio
* Crear capa de acceso a datos para gestionar pedidos 
* Crear tabla para pedidos 
* Crear tabla para almacenar Restaurantes 
* Insertar restaurantes con coodernadas, nombre y menus
* Implementar logica del carrito en el frontend 

#### HU8 - Confirmar pedido
---
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
#### HU9 - Cancelar pedido
---
* Exponer endpoint PUT /orders/{id}/cancel
* Validar que el pedido no esté en ENTREGADO
* Cambiar estado a CANCELADO
* Si tenia repartidor asignado seguir el siguiente flujo
    - Llamar al endpoint PUT /delivery/{id}/state del servicio delivery para liberar al repartidor    (cambiar estado a ACTIVO)
    - Retornar confirmación de cancelación

---
### FASE 4 - Interfaz de usuario consumidor
---
#### HU10  Visualizar y seleccionar restaurante
---
* Crear componente MapaComponent con Canvas para visualizar el mapa
* Crear un servicio para consultar los restaurantes
* Renderizar posiciones x,y  en el mapa de repartidores y restaurantes con simbolos especificos
* Al dar click en restaurante, abrir un modal con datos del restaurante y mostrar menú. 

#### HU11 Visualizar pedido asignado
---
* Crear componente RepartidorPageComponent
* Crear servicio para consultar el estado del pedido y repartidor asignado
* Mostrar datos del cliente y tiempo estimado
* Exponer el endpoint GET/orders/{id}/active-order en el servicios de orders

### HU12 Marcar como entregado 
---
* Agregar Botón "Entregar" en el  componente de RepartidosPageComponente
* Al dar click en el botón llamar a PUT/orders/{id}/delivered en order
* El servicio order cambia el pedido a ENTREGADO y notifica al servicio delivery para cambiar el estado del repartidor a ACTIVOy liberarlo. 

## Estimación del esfuerzo de desarrollo – Story Points

| HU   | SP (DEV) | Justificación                                                                                                                                                                                                     |
|------|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HU1  | 3        | Se estima en este valor debido a que crear entidades y enums no tiene gran complejidad usando IA y haciendo bien los tests. Aumenta a 3 por la conexión a base de datos, ya que toca configurar puertos, crear tablas y hacer configuraciones adicionales que pueden demorar un poco más. |
| HU2  | 2        | El peso de esta HU se deriva de solo implementar funciones con lógica matemática y algoritmos de ordenamiento, los cuales con IA agilizan bastante el trabajo.                                                    |
| HU3  | 2        | En esta HU solo se implementan enums y reglas de relación entre vehículos y clima, es ágil de hacer con IA.                                                                                                      |
| HU4  | 2        | En este caso es un enum con valores y otro algoritmo de ordenamiento, por tal motivo el peso técnico no es tan alto.                                                                                              |
| HU5  | 5        | Esta HU tiene una mayor complejidad al tener que integrar varios procesos y métodos encadenados, y tiene varios caminos que tomar según cada paso.                                                                |
| HU6  | 2        | En esta HU solo se debe implementar un método y crear criterios de activación que lleven a un siguiente método. La complejidad está en exponer el endpoint y hacer pruebas que validen que esté bien ejecutado.    |
| HU7  | 5        | Esta HU también tiene una complejidad alta por la creación de entidades, enums, configuración de varias tablas e inserción de datos, así como tener presente que hay lógica que debería tenerse en cuenta en los issues de interfaces. |
| HU8  | 5        | Esta HU tiene un peso considerable porque toca crear DTOs y además exponer el endpoint, lo que llevará a pruebas externas con POST para saber que está funcionando bien. Además se debe dejar lista la comunicación con el otro servicio. |
| HU9  | 3        | La ventaja de esta HU es que en una HU previa ya se hizo trabajo que se puede reutilizar y solo es hacer lógica y validaciones.                                                                                  |
| HU10 | 5        | Esta HU puede tener una complejidad variable según cómo se implemente el mapa virtual que verá el usuario y que además sea interactivo.                                                                          |
| HU11 | 2        | Esta página es únicamente un dashboard con un botón que solo consumiendo bien el servicio no debería tener mayor complejidad.                                                                                     |
| HU12 | 1        | Se crea el botón y se envía el evento de orden entregada. Debe validar varios factores para lograrlo, pero en sí solo es conectar el botón con la acción de entregar pedido.                                     |
| **Total** | **37** |                                                                                                                                                                                                              |