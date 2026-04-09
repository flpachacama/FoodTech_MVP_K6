# FASE 1 - Bases del proyecto (Modelo y reglas base)

-----------------------------------------------------------------------------
## HU1 - Gestionar estado de repartidores

**Como** sistema de asignación\
**Quiero** manejar los estados de los repartidores (ACTIVO, INACTIVO, EN_ENTREGA) \
**Para** saber cuáles están disponibles para asignarlos a un domicilio y descartar los que no. 

### Criterios de aceptación

```gherkin
Feature: Estado de repartidores

Scenario: Repartidor activo está disponible
Given un repartidor en estado ACTIVO
When el sistema verifica su disponibilidad
Then el repartidor debe ser considerado para asignación

Scenario: Repartidor en esta EN_ENTREGA no está disponible
Given un repartidor en estado EN_ENTREGA
When el sistema  valida su disponibilidad
Then el repartidor no debe ser considerado para asignación

Scenario: Repartidor INACTIVO no aparece como candidato
Given el repartidor tiene estado INACTIVO
When el sistema busca candidatos para un nuevo pedido
Then el repartidor no debe aparecer en la lista de candidatos

```
-----------------------------------------------------------------------------
## HU2 - Filtrar repartidores por cercanía 

**Como** sistema de asignación \
**Quiero** validar la distancia de todos repartidores con respecto al restaurante \
**Para** obtener un lista ordenada de los repartidores 

### Criterios de aceptación 

```gherkin 
Feature: Filtrado por distancia

Scenario: Repartidores en el mapa
Given repartidores con coordenadas y estado ACTIVO
When el sistema calcula la distacia al restaurante de todos los repartidores
Then se obtiene un lista ordenada de más cercanos a más lejanos 

Scenario: No hay repartidores ACTIVOS
Given todos los repartidores con estado INACTIVO o EN_ENTREGA
When el sistema intenta calcula la distacia
Then se obtiene un lista vacia
```

-----------------------------------------------------------------------------
## HU3 - Aplicar restricciones por clima

**Como** sistema de asignación \
**Quiero** aplicar reglas según el clima \
**Para** excluir transportes no aptos para el pedido

### Criterios de aceptación

```gherkin 
Feature: Restricciones por clima

Scenario: Lluvia fuerte excluye bicicleta y moto
Given clima Lluvia_Fuerte
And repartidores con transporte bicicleta y moto
When el sistema evalúa candidatos
Then estos repartidores no deben ser considerados

Scenario: Cualquier caso de lluvia excluye bicicleta 
Given clima Lluvia (LLUVIA_FUERTE,LLUVIA_SUAVE)
And repartidores con transporte bicicleta 
When el sistema evalúa candidatos
Then estos repartidores no deben ser considerados
```
-----------------------------------------------------------------------------
# FASE 2 - Lógica del algoritmo

-----------------------------------------------------------------------------
## HU4 - Calcular prioridad de repartidores

**Como** sistema de asignación \
**Quiero** priorizar repartidores por distancia y velocidad del transporte, \
**Para** seleccionar el más eficiente

### Criterios de aceptación

```gherkin
Feature: Priorización de repartidores

Scenario: Moto más lejos supera a bici más cerca
Given un repartidor en BICICLETA a distancia 10
And un repartidor en MOTO a distancia 15
When el sistema calcula el tiempo estimado de cada uno
Then el repartidor en MOTO debe tener mayor prioridad
And su tiempo estimado debe ser menor que el de BICICLETA

Scenario: Mismo vehículo, gana el más cercano
Given dos repartidores en MOTO
And el primero está a distancia 10 y el segundo a distancia 20
When el sistema calcula el tiempo estimado
Then el repartidor a distancia 10 debe tener mayor prioridad

Scenario: Dos repartidores con el mismo tiempo estimado
Given dos repartidores con el mismo tiempo estimado de llegada
When el sistema prioriza
Then se selecciona cualquiera de los dos sin error
```
-----------------------------------------------------------------------------
## HU5 - Asignar pedido automáticamente

**Como** sistema de asignación \
**Quiero** asignar el pedido al repartidor con menor tiempo estimado de llegada \
**Para** minimizar el tiempo de entraga al usuario 

### Criterios de aceptación

```gherkin
Feature: Asignación de pedidos

Scenario: Asignación exitosa
Given un pedido tiene estado pendiente
And repartidores están disponibles
When el sistema ejecuta el algoritmo
Then debe asignar el pedido al mejor candidato

Scenario: Sin repartidores disponibles
Given no hay repartidores válidos
When el sistema intenta asignar
Then el pedido debe quedar pendiente
```
-----------------------------------------------------------------------------
## HU6 - Actualizar estado del repartidor

**Como** sistema de asignación \
**Quiero** actualizar el estado del repartidor según los eventos del pedido \
**Para** garantizar que solo repartidores disponibles sea asignados

### Criterios de aceptación

```gherkin
Feature: Cambio de estados del repartidor

Scenario: Repartidor asignado cambia estado En_ENTREGA
Given un repartidor tiene estado ACTIVO
When se le asigna un pedido por el sistema 
Then el estado del repartidor debe cambiar a EN_ENTREGA

Scenario: Repartidor vuelve a ACTIVO al completar entrega
Given el repartidor tiene estado EN_ENTREGA
When marca el pedido como entregado
Then el estado del repartidor debe cambiar a ACTIVO

Scenario: Repartidor vuelve a ACTIVO al ser cancelado el pedido
Given el repartidor tiene estado EN_ENTREGA
When el usuario consumidor cancela el pedido
Then el estado del repartidor debe cambiar a ACTIVO

```
-----------------------------------------------------------------------------
# FASE 3 - Flujo de pedidos (Core del negocio)

-----------------------------------------------------------------------------
## HU7 - Generar pedido

**Como** usuario consumidor \
**Quiero** seleccionar productos del menú y agregarlos al carrito \
**Para** armar mi pedido antes de confirmarlo

### Criterios de aceptación

```gherkin
Feature: Generación de pedido

Scenario: Usuario agrega varios productos al carrito
Given el usuario ha seleccionado un restaurante 
When agrega uno o más productos al carrito
Then el carrito debe mostrar la cantidad de productos con su precio total

Scenario: Usuario elimina un producto del carrito 
Given un usuario tiene un sólo producto determinado en el carrito
When elimina dicho producto
Then el carrito debe quedar vacío 

Scenario: Usuario intenta validar un carrito vacío
Given el usuario ha seleccionado un restaurante
And no ha agregado ningún producto
When intenta seguir el flujo del pedido
Then el sistema no debe permitir avanzar en el proceso
And debe indicarle al usuario que está vacío el carrito

```
-----------------------------------------------------------------------------
## HU8 - Ingresar datos y confirmar pedido

**Como** usuario consumidor \
**Quiero** ingresar mis datos personales y confirmar mi pedido\
**Para** que el sistema genere la orden y asigne un repartidor

### Criterios de aceptación
```gherkin
Feature: Confirmación de pedido

Scenario: Pedido confirmado y asignado exitosamente a un repartidor
Given el usuario tiene productos en el carrito
And ingresa su nombre,sus coordenada "x","y" y su número de teléfono
When confirma el pedido
Then el sistema registra la orden en estado ASIGNADO
And muestra el tiempo estimado de entrega al usuario

Scenario: Tiempo estimado visible hasta que el repartidor entrega
Given el usuario tiene una orden en estado ASIGNADO
When consulta su pedido activo
Then debe ver el tiempo estimado de entrega
And este desaparece cuando la orden cambia a estado ENTREGADO


Scenario: Pedido confirmado sin repartidores disponibles
Given el usuario confirma un pedido con datos completos
And no hay repartidores ACTIVOS en el sistema o que funcionen por criterio de clima
When el sistema intenta asignar
Then la orden queda en estado PENDIENTE
And no se muestra tiempo estimado al usuario
And se informa al usuario que no hay repartidores disponibles en el momento

Scenario: Usuario intenta confirmar sin llenar todos sus datos
Given el usuario tiene productos en el carrito
And deja el campo teléfono vacío
When intenta confirmar
Then el sistema no debe permitir la generación y confirmación del pedido
And debe indicar qué campos son obligatorios
```

-----------------------------------------------------------------------------
## HU9 - Cancelar pedido

**Como** usuario consumidor \
**Quiero** cancelar un pedido \
**Para** evitar que sea procesado

### Criterios de aceptación

```gherkin
Feature: Cancelación de pedido

Scenario: Cancelar pedido antes de asignación
Given que el usuario tiene un pedido activo
When el usuario cancela el pedido
Then el sistema debe marcar el pedido como cancelado

Scenario: Cancelar pedido con repartidor ya asignado
Given el usuario tiene un pedido en estado ASIGNADO
When el usuario cancela el pedido
Then el sistema marca el pedido como CANCELADO
And el repartidor asignado vuelve a estado ACTIVO
```
-----------------------------------------------------------------------------
# FASE 4 - Interfaz de usuario consumidor

-----------------------------------------------------------------------------
## HU10 - Visualizar y seleccionar restaurante

**Como** usuario consumidor \
**Quiero** ver los restaurantes y repartidores disponibles en el mapa y seleccionar un restaurante \
**Para** ver su menú y hacer un pedido

### Criterios de aceptación

```gherkin
Feature: Visualización y selección de un restaurante

Scenario: Usuario ve todos los restaurantes en el mapa
Given existen restaurantes en el sistema
When el usuario accede al mapa
Then debe ver todos los restaurantes con su nombre y ubicación
And debe ver todos los repartidores con su estado y vehículo

Scenario: Usuario selecciona un restaurante y ve su menú
Given el usuario visualiza los restaurantes en el mapa
When selecciona el restaurante "La Parrilla"
Then debe ver el menú de "La Parrilla" con sus productos y precios
And puede agregar productos al carrito

Scenario: No hay restaurantes registrados
Given no existen restaurantes en el sistema
When el usuario accede al mapa
Then debe ver un mensaje indicando que no hay restaurantes disponibles

Scenario: API retorna lista de restaurantes
Given el servicio order-service está activo
When se hace GET /restaurants
Then retorna lista de restaurantes con id, nombre, coordenadas y menú

Scenario: API retorna lista de repartidores
Given el servicio delivery-service está activo
When se hace GET /delivery/repartidores
Then retorna lista de repartidores con id, nombre, estado, vehículo y coordenadas
```
-----------------------------------------------------------------------------
# FASE 5 - Interfaz del repartidor

-----------------------------------------------------------------------------
## HU11 - Visualizar pedido asignado

**Como** repartidor \
**Quiero** ver el pedido asignado \
**Para** poder realizar la entrega

### Criterios de aceptación

```gherkin
Feature: Vista repartidor

Scenario: Mostrar pedido asignado con datos del usuario
Given un repartidor con pedido asignado con estado EN_ENTREGA
And tiene asignado el pedido de un usuario 
When accede a su interfaz
Then debe visualizar los datos del usuario con nombre, telefon y coordenadas
And el tiempo estimado de entrega

Scenario: Repartidor sin pedido asignado
Given un repartidor sin pedido asignado
When accede a su interfaz
Then debe ver un mensaje indicando que no tiene pedidos asignados
```
-----------------------------------------------------------------------------
## HU12 - Marcar pedido como entregado

**Como** repartidor \
**Quiero** marcar el pedido como entregado \
**Para** finalizar la entrega

### Criterios de aceptación

```gherkin
Feature: Finalización de entrega

Scenario: Marcar pedido como entregado
Given el repartidor tiene un pedido asignado con estado EN_ENTREGA
When el repartidor presiona "Entregado"
Then el pedido debe cambiar a estado ENTREGADO
And el repartidor debe volver a estado ACTIVO
And el tiempo debe desaparecer de la vista del usuario consumidor
```