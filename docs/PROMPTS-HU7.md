# HU7 - Generar pedido

## Plan de ejecución

### Paso 1 — Entidades de Dominio y Enum
- Archivos: Pedido.java, EstadoPedido.java, ProductoPedido.java (paquete dominio)
- Dependencias previas: Ninguna
- Por qué este orden: Se definen los modelos centrales del dominio para que las siguientes capas puedan usarlos.

### Paso 2 — Repositorio de Dominio para Pedido
- Archivos: PedidoRepository.java (paquete dominio)
- Dependencias previas: Entidades de dominio creadas
- Por qué este orden: El repositorio abstrae el acceso a datos y depende de las entidades.

### Paso 3 — Adaptador de Persistencia y Tablas
- Archivos: PedidoEntity.java, PedidoJpaRepository.java, PedidoDataAdapter.java (infraestructura), script SQL para tabla pedidos
- Dependencias previas: Entidades y repositorio de dominio
- Por qué este orden: El adaptador implementa el repositorio usando JPA y requiere las entidades y la interfaz.

### Paso 4 — Entidad y tabla Restaurante
- Archivos: RestauranteEntity.java, RestauranteJpaRepository.java, script SQL para tabla restaurantes
- Dependencias previas: Ninguna (puede ir en paralelo con Paso 1)
- Por qué este orden: Se necesita para asociar pedidos a restaurantes y poblar datos iniciales.

### Paso 5 — Insertar Restaurantes de ejemplo
- Archivos: data.sql (infraestructura o resources)
- Dependencias previas: Tabla restaurante creada
- Por qué este orden: Permite tener datos de prueba para desarrollo y pruebas.


## Prompt Paso 1 — Entidades de Dominio y Enum

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech`

### Contexto existente
No existen aún las entidades de dominio para pedidos ni productos.

### Lo que necesito
- Crear la clase `Pedido` en el paquete `com.foodtech.order.domain.model` con los campos: id (Long), estado (EstadoPedido), restauranteId (Long), productos (List<ProductoPedido>), clienteId (Long), clienteNombre (String), clienteCoordenadasX (Double), clienteCoordenadasY (Double), tiempoEstimado (Integer)
- Crear el enum `EstadoPedido` en el paquete `com.foodtech.order.domain.model` con los valores: PENDIENTE, ASIGNADO, ENTREGADO, CANCELADO
- Crear la clase `ProductoPedido` en el paquete `com.foodtech.order.domain.model` con los campos: id (Long), nombre (String), precio (BigDecimal)

### Reglas
- No crear ningún archivo fuera de los mencionados
- No modificar clases no mencionadas
- Usar Lombok donde aplique
- Seguir la nomenclatura del proyecto
```

## Prompt Paso 2 — Repositorio de Dominio para Pedido

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech`

### Contexto existente
- Existen las clases `Pedido`, `EstadoPedido`, `ProductoPedido` en `com.foodtech.order.domain.model`

### Lo que necesito
- Crear la interfaz `PedidoRepository` en el paquete `com.foodtech.order.domain` con los métodos necesarios para guardar, buscar por id y listar pedidos

### Reglas
- No crear ningún archivo fuera de los mencionados
- No modificar clases no mencionadas
- Usar Lombok donde aplique
- Seguir la nomenclatura del proyecto

### Ubicación del proyecto
- Este código pertenece al microservicio: `order-service`
- Path base en el workspace: `backend/order-service/src/main/java/`
- Paquete base: `com.foodtech.order`
- Todos los archivos deben crearse dentro de `backend/order-service/`
```

## Prompt Paso 3 — Adaptador de Persistencia y Tablas

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech`

### Contexto existente
- Existen las entidades de dominio y el repositorio de dominio para pedidos

### Lo que necesito
- Crear la clase `PedidoEntity` en el paquete `com.foodtech.order.infrastructure.persistence.entity` mapeando los campos de `Pedido` para JPA
- Crear la interfaz `PedidoJpaRepository` en el paquete `com.foodtech.order.infrastructure.persistence` extendiendo JpaRepository<PedidoEntity, Long>
- Crear la clase `PedidoDataAdapter` en el paquete `com.foodtech.order.infrastructure.persistence` que implemente `PedidoRepository` usando `PedidoJpaRepository`
- Crear un script SQL para la tabla `pedidos` con los campos necesarios

### Reglas
- No crear ningún archivo fuera de los mencionados
- No modificar clases no mencionadas
- Usar Lombok donde aplique
- Seguir la nomenclatura del proyecto

### Ubicación del proyecto
- Este código pertenece al microservicio: `order-service`
- Path base en el workspace: `backend/order-service/src/main/java/`
- Paquete base: `com.foodtech.order`
- Todos los archivos deben crearse dentro de `backend/order-service/`
```

## Prompt Paso 4 — Entidad y tabla Restaurante

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech`

### Contexto existente
No existe aún la entidad Restaurante ni su tabla.

### Lo que necesito
- Crear la clase `RestauranteEntity` en el paquete `com.foodtech.order.infrastructure.persistence.entity` con los campos: id (Long), nombre (String), coordenadaX (Double), coordenadaY (Double), menu (String o relación a productos)
- Crear la interfaz `RestauranteJpaRepository` en el paquete `com.foodtech.order.infrastructure.persistence` extendiendo JpaRepository<RestauranteEntity, Long>
- Crear un script SQL para la tabla `restaurantes` con los campos mencionados

### Reglas
- No crear ningún archivo fuera de los mencionados
- No modificar clases no mencionadas
- Usar Lombok donde aplique
- Seguir la nomenclatura del proyecto

### Ubicación del proyecto
- Este código pertenece al microservicio: `order-service`
- Path base en el workspace: `backend/order-service/src/main/java/`
- Paquete base: `com.foodtech.order`
- Todos los archivos deben crearse dentro de `backend/order-service/`
```

## Prompt Paso 5 — Insertar Restaurantes de ejemplo

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech`

### Contexto existente
- Existe la tabla `restaurantes`

### Lo que necesito
- Crear el archivo `data.sql` en la carpeta de recursos para insertar al menos 2 restaurantes con coordenadas, nombre y menú de ejemplo

### Reglas
- No crear ningún archivo fuera de los mencionados
- No modificar clases no mencionadas
- Seguir la nomenclatura del proyecto

### Ubicación del proyecto
- Este código pertenece al microservicio: `order-service`
- Path base en el workspace: `backend/order-service/src/main/java/`
- Paquete base: `com.foodtech.order`
- Todos los archivos deben crearse dentro de `backend/order-service/`
```
