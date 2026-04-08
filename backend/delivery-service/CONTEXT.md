Ejemplo:

Request POST /delivery

```json
{ "pedidoId": 1, "restauranteX": 99, "restauranteY": 45, "clima": "SOLEADO" }
```

Response 200 OK (ejemplo):

```json
{
  "pedidoId": 1,
  "estado": "ASIGNADO",
  "repartidorId": 2,
  "nombreRepartidor": "Ana Rodríguez"
}
```
Ejemplo:

Request PUT /delivery/11/state

```json
{ "evento": "ENTREGADO" }
```

Response 200 OK (ejemplo):

```json
{
  "id": 11,
  "nombre": "Carlos Mendoza",
  "estado": "ACTIVO",
  "vehiculo": "MOTO",
  "x": 25,
  "y": 40
}
```
**Delivery Service Context**

- **Description:**: Servicio microservicio responsable de la asignación inteligente de repartidores a pedidos según ubicación y condiciones climáticas.
- **Responsibility:**: Exponer un endpoint REST que recibe una solicitud de asignación y devuelve el repartidor asignado (o estado PENDIENTE) usando reglas de negocio encapsuladas en la capa de dominio.

**Stack & Runtime**
- **Language / Frameworks:**: Java 17, Spring Boot 3.x, Spring Data JPA, Jakarta Validation, Lombok
- **Persistence:**: PostgreSQL (JPA / Hibernate)
- **Build / CI:**: Maven
- **Code coverage:**: JaCoCo (configured in pom.xml)

**Endpoints**
- **POST /delivery:**: Recibe una solicitud de asignación y responde con el repartidor seleccionado o estado pendiente. Controller: [AsignacionController.java](backend/delivery-service/src/main/java/com/foodtech/infrastructure/web/controller/AsignacionController.java)

**Request / Response DTOs**
- **Request:**: [AsignacionRequestDTO.java](backend/delivery-service/src/main/java/com/foodtech/infrastructure/web/dto/AsignacionRequestDTO.java) — campos validados: `pedidoId`, `restauranteX`, `restauranteY`, `clima` (opcional).
- **Response:**: [AsignacionResponseDTO.java](backend/delivery-service/src/main/java/com/foodtech/infrastructure/web/dto/AsignacionResponseDTO.java) — `pedidoId`, `estado`, `repartidorId`, `nombreRepartidor`.

**Domain Model (principal)**
- `Repartidor` (domain model): [Repartidor.java](backend/delivery-service/src/main/java/com/foodtech/domain/model/Repartidor.java)
- `Coordenada` value object: [Coordenada.java](backend/delivery-service/src/main/java/com/foodtech/domain/model/Coordenada.java)
- `Clima`, `TipoVehiculo`, `EstadoRepartidor`: enums under domain model.

**Architecture: Hexagonal / Ports & Adapters**
- **Input port (use case):**: [AsignacionUseCase.java](backend/delivery-service/src/main/java/com/foodtech/domain/port/input/AsignacionUseCase.java)
- **Application Service (orchestrator):**: [AsignacionApplicationService.java](backend/delivery-service/src/main/java/com/foodtech/application/service/AsignacionApplicationService.java) — coordina repositorio y servicio de dominio.
- **Domain Service (business rules):**: [AsignacionService.java](backend/delivery-service/src/main/java/com/foodtech/domain/service/AsignacionService.java) — contiene la lógica de filtrado por clima y priorización por tiempo estimado.
- **Output adapter (persistence):**: [RepartidorPersistenceAdapter.java](backend/delivery-service/src/main/java/com/foodtech/infrastructure/persistence/adapter/RepartidorPersistenceAdapter.java) con entidad JPA [RepartidorEntity.java](backend/delivery-service/src/main/java/com/foodtech/infrastructure/persistence/entity/RepartidorEntity.java) y repository [JpaRepartidorRepository.java](backend/delivery-service/src/main/java/com/foodtech/infrastructure/persistence/repository/JpaRepartidorRepository.java).

**Important Config & Data**
- **application.properties:**: deferred datasource initialization and datasource URL (see [application.properties](backend/delivery-service/src/main/resources/application.properties)).
- **Seed data:**: `data.sql` used to initialize a small set of repartidores (migrated from import.sql for Spring Boot 3.x): [data.sql](backend/delivery-service/src/main/resources/data.sql)

**Tests & Coverage**
- **Unit tests:**: Tests cover domain service, value objects, persistence adapter, application service, controller DTOs and exception handler. Key tests live under `src/test/java`.
- **Last run results:**: All tests passed. Line coverage: **93.66%**. Branch coverage: **87.10%**. JaCoCo report: `target/site/jacoco/index.html`.

**High-level Flow**
- 1) `POST /delivery` receives AsignacionRequestDTO → controller converts to Coordenada + Clima.
- 2) Controller calls `AsignacionUseCase.obtenerRepartidoresPriorizados`.
- 3) Application service fetches activos via `RepartidorRepository` and delegates prioritization to `AsignacionService`.
- 4) `AsignacionService` filters por aptitud de vehículo según `Clima` y ordena por tiempo estimado (distancia / velocidad), devuelve lista priorizada.
- 5) Controller responde `ASIGNADO` con primer candidato o `PENDIENTE` si no hay candidatos.

**External Dependencies**
- PostgreSQL database (configured in Docker Compose at repository root). Ensure the `SPRING_DATASOURCE_URL` environment variable or application properties point to the running DB.

**Files I updated / created during analysis**
- Tests added: `src/test/java/com/foodtech/infrastructure/web/controller/AsignacionControllerTest.java`, `src/test/java/com/foodtech/infrastructure/web/dto/AsignacionRequestDTOTest.java`, `src/test/java/com/foodtech/infrastructure/web/dto/AsignacionResponseDTOTest.java`, `src/test/java/com/foodtech/infrastructure/web/exception/GlobalExceptionHandlerTest.java`.

**How to run locally**
- Run tests and generate JaCoCo report:

  mvn clean test

- Open coverage report: `backend/delivery-service/target/site/jacoco/index.html`.

**Notes & Next steps**
- Controller is light-weight and covered by unit tests; consider adding `@WebMvcTest` integration tests for true HTTP layer coverage if needed.
- Keep `spring.jpa.defer-datasource-initialization=true` when using `data.sql` with `ddl-auto=update`.

---
Generated on 2026-03-25 (automated scan).
# Backend – Documento de Contexto Actual
> Generado: 25 de marzo de 2026

---

## 1. Descripción general

Microservicio Spring Boot (Java 17) que implementa el núcleo del dominio de **asignación de repartidores** para la plataforma FoodTech. Corresponde al microservicio `delivery` descrito en el PRD: gestiona estados de repartidores, ubicaciones y el algoritmo de selección.

---

## 2. Stack tecnológico

| Componente        | Versión / Detalle                        |
|-------------------|------------------------------------------|
| Java              | 17                                       |
| Spring Boot       | 3.2.3                                    |
| Spring Web        | REST Controllers                         |
| Spring Data JPA   | Persistencia con Hibernate               |
| Bean Validation   | Jakarta Validation                       |
| Base de datos     | PostgreSQL (driver runtime)              |
| Lombok            | Reducción de boilerplate (excluido del JAR final) |
| Testing           | JUnit 5 + Mockito (spring-boot-starter-test) |
| Build             | Maven (spring-boot-maven-plugin)         |

---

## 3. Arquitectura

El proyecto aplica **Arquitectura Hexagonal (Ports & Adapters)**:

```
com.foodtech
├── FoodTechApplication.java          ← Entry point (@SpringBootApplication)
│
├── domain/                           ← Núcleo de negocio (sin dependencias externas)
│   ├── model/                        ← Entidades y value objects del dominio
│   │   ├── Clima.java                ← Enum de condiciones climáticas
│   │   ├── Coordenada.java           ← Record (x, y) con validación y cálculo de distancia
│   │   ├── EstadoRepartidor.java     ← Enum de estado operativo
│   │   ├── Repartidor.java           ← Entidad de dominio principal
│   │   └── TipoVehiculo.java         ← Enum con velocidad en km/h
│   ├── port/
│   │   └── output/
│   │       └── RepartidorRepository.java  ← Puerto de salida (interfaz)
│   └── service/
│       └── AsignacionService.java    ← Lógica de asignación
│
└── infrastructure/                   ← Adaptadores externos
    └── persistence/
        ├── adapter/
        │   └── RepartidorPersistenceAdapter.java  ← Implementa RepartidorRepository
        ├── entity/
        │   └── RepartidorEntity.java              ← Entidad JPA (@Entity)
        └── repository/
            └── JpaRepartidorRepository.java       ← Extiende JpaRepository
```

---

## 4. Modelo de dominio

### 4.1 `Coordenada` (record)
- Cuadrícula simulada `x, y ∈ [0, 100]`
- Validación en el constructor compacto: lanza `IllegalArgumentException` si están fuera de rango o son negativos
- Método `distanciaA(Coordenada otra)` → distancia Euclidiana con protección de null

### 4.2 `Clima` (enum)
```
SOLEADO | LLUVIA_SUAVE | LLUVIA_FUERTE
```

### 4.3 `EstadoRepartidor` (enum)
```
ACTIVO | INACTIVO | EN_ENTREGA
```

### 4.4 `TipoVehiculo` (enum)
| Vehículo   | Velocidad (km/h) |
|------------|-----------------|
| BICICLETA  | 15              |
| MOTO       | 20              |
| AUTO       | 30              |

### 4.5 `Repartidor` (dominio)
- Campos: `id`, `nombre`, `EstadoRepartidor`, `TipoVehiculo`, `Coordenada`
- Anotaciones Lombok: `@Getter`, `@Builder`, `@AllArgsConstructor`

---

## 5. Servicio de dominio: `AsignacionService`

### Método principal
```java
List<Repartidor> obtenerRepartidoresPriorizados(Coordenada restauranteUbicacion, Clima clima)
```

### Algoritmo (3 etapas)
1. **Obtener activos** → `findByEstado(ACTIVO)` — retorna lista vacía si no hay ninguno
2. **Filtrar por clima** (`esVehiculoApto`):
   - `LLUVIA_FUERTE` → solo `AUTO`
   - `LLUVIA_SUAVE` → `MOTO` o `AUTO`
   - `SOLEADO` / `null` → todos
3. **Ordenar por tiempo estimado** = `distancia / velocidadKmH` (ascendente)

---

## 6. Capa de infraestructura

### `RepartidorEntity` (JPA)
- Tabla: `repartidores`
- Campos: `id` (identity), `nombre`, `estado` (EnumType.STRING), `vehiculo` (EnumType.STRING), `x`, `y`
- Anotaciones Lombok: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

### `JpaRepartidorRepository`
- Extiende `JpaRepository<RepartidorEntity, Long>`
- Query derivada: `findByEstado(EstadoRepartidor estado)`

### `RepartidorPersistenceAdapter`
- Implementa `RepartidorRepository` (puerto de salida)
- Inyectado como `@Component` con `@RequiredArgsConstructor`
- Mapeo bidireccional dominio ↔ entidad JPA:
  - `toDomain()`: construye `Repartidor` con `new Coordenada(entity.getX(), entity.getY())`
  - `toEntity()`: extrae `x` e `y` por separado de `Coordenada`

---

## 7. Configuración

### `application.properties`
```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/foodtech_db
spring.datasource.username=foodtech_user
spring.datasource.password=foodtech_pass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.sql.init.mode=always
```

### `import.sql` (datos iniciales)
| Nombre            | Estado     | Vehículo   | X  | Y  |
|-------------------|------------|------------|----|----|
| Carlos Mendoza    | ACTIVO     | MOTO       | 25 | 40 |
| Ana Rodríguez     | ACTIVO     | BICICLETA  | 60 | 15 |
| Luis Fernández    | EN_ENTREGA | AUTO       | 80 | 75 |
| María González    | EN_ENTREGA | MOTO       | 10 | 90 |
| Pedro Sánchez     | INACTIVO   | AUTO       | 45 | 55 |

---

## 8. Cobertura de tests

### `CoordenadaTest` (8 tests)
- Creación válida en límites (0,0) y (100,100)
- Excepción con x o y negativos
- Excepción al superar límite (101)
- Distancia cero al mismo punto
- Triángulo 3-4-5 → distancia = 5.0
- Simetría de distancia
- Diagonal unitaria → √2

### `AsignacionServiceTest` (8 tests)
- Sin activos → lista vacía
- Ordenamiento correcto por distancia
- Empate de distancia manejado
- Punto exacto (distancia 0)
- Moto lejana priorizada sobre bici cercana (por tiempo estimado)
- Prioridad por cercanía con el mismo vehículo
- Empate de tiempo estimado (Auto 30 km/h a 30 vs Moto 20 km/h a 20)
- Combinación de filtro clima + prioridad de tiempo

### `FiltroClimaTest` (4 tests)
- `LLUVIA_FUERTE` → solo AUTO
- `LLUVIA_SUAVE` → excluye BICICLETA
- `SOLEADO` → todos aptos
- Sin candidatos aptos → lista vacía no null

### `RepartidorPersistenceAdapterTest` (7 tests)
- `findById` convierte entidad a dominio con `Coordenada`
- `save` mapea correctamente x/y separados
- `findById` con ID inexistente → Optional vacío
- Coordenadas en límites (0,0) y (100,100)
- `findByEstado` con lista vacía → lista no null
- `save` con nombre null → persiste null
- `save` con ubicación null → lanza `NullPointerException`
- Integridad de datos: enums y campos mapeados exactamente

---

## 9. Lo que está implementado vs. lo que falta

### Implementado
- [x] Modelo de dominio completo (Repartidor, Coordenada, enums)
- [x] Puerto de salida `RepartidorRepository`
- [x] Servicio de asignación con filtro de clima y priorización por tiempo
- [x] Adaptador de persistencia JPA con mapeo bidireccional
- [x] Datos iniciales en `import.sql`
- [x] Suite de tests unitarios (dominio e infraestructura)

### Pendiente / No implementado aún
- [ ] **Controllers REST** — No existe ningún `@RestController`. La API no está expuesta
- [ ] **Puerto de entrada** — No hay `AsignacionUseCase` ni interfaz de entrada definida
- [ ] **Gestión de pedidos** — El microservicio `order` (pedidos, estados, flujos de cliente) no existe
- [ ] **Cambio de estado del repartidor** al asignar (`EN_ENTREGA`)
- [ ] **Estimación de tiempo** expuesta al cliente
- [ ] **Manejo de errores global** (`@ControllerAdvice` / `@ExceptionHandler`)
- [ ] **Tests de integración** (Spring context, base de datos en memoria H2)
- [ ] **Frontend** — La carpeta `frontend/` existe pero está vacía (solo README)
- [ ] **Seguridad** — No hay autenticación ni autorización (fuera de alcance según PRD)

---

## 10. Relación con el PRD

| Objetivo PRD                                          | Estado backend        |
|-------------------------------------------------------|-----------------------|
| Asignación automática por menor tiempo estimado       | Servicio implementado, sin endpoint |
| Considerar distancia, vehículo y clima                | Implementado en `AsignacionService` |
| Estado ACTIVO/INACTIVO/EN_ENTREGA                     | Modelo completo, sin lógica de cambio de estado en asignación |
| Cuadrícula simulada (matriz X,Y)                      | Implementado con `Coordenada` |
| Interfaz de usuario                                   | Pendiente             |
