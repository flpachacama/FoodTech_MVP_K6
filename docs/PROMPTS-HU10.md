# PROMPTS-HU10 — Visualizar y seleccionar restaurante

## Análisis de la HU

**Estado:** ✅ **BACKEND COMPLETADO**

Endpoints implementados para consultar restaurantes y repartidores:

| Recurso | Endpoint | ¿Existe? | Servicio | Puerto |
|---------|----------|----------|----------|--------|
| Restaurantes | `GET /restaurants` | ✅ SÍ | order-service | 8081 |
| Restaurante por ID | `GET /restaurants/{id}` | ✅ SÍ | order-service | 8081 |
| Repartidores | `GET /delivers` | ✅ SÍ | delivery-service | 8080 |
| Repartidor por ID | `GET /delivers/{id}` | ✅ SÍ | delivery-service | 8080 |

---

## Propuesta de modificación al SUBTASKS.md

```markdown
### HU10 - Visualizar y seleccionar restaurante

**Subtareas DEV (Backend - order-service):** ✅ COMPLETADO
* ✅ Crear RestauranteResponseDto con id, nombre, coordenadasX, coordenadasY, menu (lista de productos)
* ✅ Crear RestauranteController con endpoints:
  - GET /restaurants → lista todos los restaurantes con sus menús
  - GET /restaurants/{id} → retorna un restaurante específico con su menú
* ✅ Crear RestauranteService para la lógica de consulta

**Subtareas DEV (Backend - delivery-service):** ✅ COMPLETADO
* ✅ Crear RepartidorListResponseDto con id, nombre, estado, vehiculo, ubicacionX, ubicacionY
* ✅ Crear DeliversController con endpoints:
  - GET /delivers → lista todos los repartidores con sus ubicaciones
  - GET /delivers/{id} → retorna un repartidor específico
* ✅ Agregar métodos getAllRepartidores() y getRepartidorById() en AsignacionApplicationService

**Subtareas DEV (Frontend):** ⏳ PENDIENTE
* Crear componente MapaComponent con Canvas para visualizar el mapa
* Crear servicio RestauranteService para consultar GET /restaurants
* Crear servicio RepartidorService para consultar GET /delivers
* Renderizar posiciones x,y en el mapa con símbolos específicos:
  - 🏪 Restaurantes (círculo rojo)
  - 🛵 Repartidores (triángulo según vehículo y color según estado)
* Al dar click en restaurante, abrir modal con datos y menú
```

---

## Propuesta de modificación a USER_STORIES.md (HU10)

```markdown
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
When se hace GET /delivers
Then retorna lista de repartidores con id, nombre, estado, vehículo y coordenadas

Scenario: API retorna repartidor específico
Given el servicio delivery-service está activo
When se hace GET /delivers/1
Then retorna el repartidor con id 1 con su estado, vehículo y coordenadas
```
```

---

## Plan de ejecución (Backend primero)

### Paso 1 — Crear endpoint GET /restaurants en order-service ✅ COMPLETADO
- Archivos: `RestauranteController.java`, `RestauranteResponseDto.java`, `RestauranteService.java`, `ProductoMenuDto.java`
- Dependencias previas: Ya existe `RestauranteJpaRepository` y `RestauranteEntity`
- Estado: ✅ Implementado y probado

### Paso 2 — Crear endpoints GET /delivers en delivery-service ✅ COMPLETADO
- Archivos: `DeliversController.java`, `RepartidorListResponseDto.java`, modificado `AsignacionApplicationService.java`
- Dependencias previas: Ya existe `RepartidorRepository` y modelo `Repartidor`
- Estado: ✅ Implementado y probado

### Paso 3 — Frontend ⏳ PENDIENTE
- Crear componentes y servicios Angular 19 para consumir los endpoints

---

## Prompt Paso 3.1 — Crear servicios HTTP (Angular 19) ✅ COMPLETADO

**Proyecto creado:** `/home/omar/proyectos/semana7/FoodTech_MVP/frontend/foodtech-app`

```
Tengo una aplicación Angular 19 standalone.

### Contexto
- Backend en 2 microservicios:
  - order-service: http://localhost:8081
  - delivery-service: http://localhost:8080

### Lo que necesito

1. Crear `environment.ts` y `environment.prod.ts` con las URLs base:
   ```typescript
   export const environment = {
     production: false,
     orderServiceUrl: 'http://localhost:8081',
     deliveryServiceUrl: 'http://localhost:8080'
   };
   ```

2. Crear `restaurante.service.ts` en `src/app/services/`:
   - Método `getAll(): Observable<Restaurante[]>` → GET /restaurants
   - Método `getById(id: number): Observable<Restaurante>` → GET /restaurants/{id}

3. Crear `deliver.service.ts` en `src/app/services/`:
   - Método `getAll(): Observable<Deliver[]>` → GET /delivers
   - Método `getById(id: number): Observable<Deliver>` → GET /delivers/{id}

4. Crear interfaces en `src/app/models/`:
   - `restaurante.model.ts`: id, nombre, coordenadaX, coordenadaY, menu (ProductoMenu[])
   - `producto-menu.model.ts`: id, nombre, precio
   - `deliver.model.ts`: id, nombre, estado, vehiculo, ubicacionX, ubicacionY

### Reglas
- Usar HttpClient con inject()
- Usar signals donde aplique
- Standalone components
- Manejar errores con catchError
```

---

## Prompt Paso 3.2 — Crear componente MapaComponent con Canvas ✅ COMPLETADO

```
Tengo una aplicación Angular 19 standalone.

### Contexto existente
- Ya existen servicios `restaurante.service.ts` y `deliver.service.ts`
- Interfaz Restaurante: { id, nombre, coordenadaX, coordenadaY, menu }
- Interfaz Deliver: { id, nombre, estado, vehiculo, ubicacionX, ubicacionY }
- Las coordenadas van de 0 a 100 (grid 100x100)

### Lo que necesito

1. Crear `mapa.component.ts` en `src/app/components/mapa/`:
   - Componente standalone con Canvas HTML5
   - Canvas de 800x800 pixels (escalar coordenadas 0-100 → 0-800)
   - En ngOnInit cargar restaurantes y repartidores de los servicios
   - Usar signals para almacenar los datos

2. Renderizar en el canvas:
   - **Restaurantes:** Círculo rojo (radio 15px) con nombre debajo
   - **Repartidores:** Triángulo con color según estado:
     - ACTIVO: verde
     - EN_ENTREGA: amarillo
     - INACTIVO: gris
   - Icono del vehículo dentro del triángulo (emoji o letra):
     - BICICLETA: 🚲 o "B"
     - MOTO: 🏍️ o "M"
     - AUTO: 🚗 o "A"

3. Interactividad:
   - Al hacer click en un restaurante, emitir evento `(restauranteSelected)` con el objeto Restaurante
   - Mostrar tooltip al hacer hover sobre elementos

### Reglas
- Componente standalone
- Usar signals y computed para estado reactivo
- Canvas 2D context para dibujar
- Método privado para convertir coordenadas: coordToPixel(coord: number): number
```

---

## Prompt Paso 3.3 — Crear modal de menú del restaurante ✅ COMPLETADO

```
Tengo una aplicación Angular 19 standalone.

### Contexto existente
- Ya existe MapaComponent que emite evento (restauranteSelected)
- Interfaz Restaurante: { id, nombre, coordenadaX, coordenadaY, menu: ProductoMenu[] }
- Interfaz ProductoMenu: { id, nombre, precio }

### Lo que necesito

1. Crear `menu-modal.component.ts` en `src/app/components/menu-modal/`:
   - Recibe @Input() restaurante: Restaurante | null
   - Recibe @Input() visible: boolean
   - Emite @Output() close cuando se cierra
   - Emite @Output() agregarProducto con el producto seleccionado

2. Template del modal:
   - Overlay oscuro de fondo
   - Card centrada con:
     - Header: nombre del restaurante + botón X para cerrar
     - Lista de productos con nombre y precio formateado (COP)
     - Botón "Agregar" en cada producto

3. Estilos:
   - Modal centrado con animación de entrada
   - Scroll si hay muchos productos
   - Botón Agregar con hover effect

### Reglas
- Componente standalone
- Usar signals si es necesario
- Formatear precios con pipe currency o manualmente: $18.000
- Cerrar modal al hacer click en overlay o en X
```

---

## Prompt Paso 3.4 — Integrar componentes en página principal ✅ COMPLETADO

```
Tengo una aplicación Angular 19 standalone.

### Contexto existente
- Ya existe MapaComponent con evento (restauranteSelected)
- Ya existe MenuModalComponent con inputs restaurante y visible
- Ya existen servicios RestauranteService y DeliverService

### Lo que necesito

1. Crear o modificar `app.component.ts`:
   - Importar MapaComponent y MenuModalComponent
   - Signal para restaurante seleccionado: selectedRestaurante = signal<Restaurante | null>(null)
   - Signal para visibilidad del modal: modalVisible = signal(false)
   - Método onRestauranteSelected(r: Restaurante) que actualiza ambos signals
   - Método onCloseModal() que oculta el modal

2. Template:
   ```html
   <div class="container">
     <h1>FoodTech - Mapa de Restaurantes</h1>
     <app-mapa (restauranteSelected)="onRestauranteSelected($event)"></app-mapa>
     <app-menu-modal 
       [restaurante]="selectedRestaurante()" 
       [visible]="modalVisible()"
       (close)="onCloseModal()">
     </app-menu-modal>
   </div>
   ```

3. Estilos básicos en styles.css:
   - Reset básico
   - Fuente sistema
   - Container centrado

### Reglas
- Standalone application
- Usar signals para estado
- Responsive básico
```

---

## Resumen de archivos frontend a crear

| Paso | Archivo | Descripción | Estado |
|------|---------|-------------|--------|
| 3.1 | **Proyecto Angular 19** | `frontend/foodtech-app/` | ✅ Creado |
| 3.1 | `src/environments/environment.ts` | URLs de los servicios | ✅ Creado |
| 3.1 | `src/app/models/restaurante.model.ts` | Interface Restaurante | ✅ Creado |
| 3.1 | `src/app/models/producto-menu.model.ts` | Interface ProductoMenu | ✅ Creado |
| 3.1 | `src/app/models/deliver.model.ts` | Interface Deliver | ✅ Creado |
| 3.1 | `src/app/services/restaurante.service.ts` | Servicio HTTP restaurantes | ✅ Creado |
| 3.1 | `src/app/services/deliver.service.ts` | Servicio HTTP delivers | ✅ Creado |
| 3.1 | `src/app/app.config.ts` | Configuración HttpClient | ✅ Modificado |
| 3.2 | `src/app/components/mapa/mapa.component.ts` | Componente Canvas del mapa | ✅ Creado |
| 3.2 | `src/app/components/mapa/mapa.component.html` | Template del mapa | ✅ Creado |
| 3.2 | `src/app/components/mapa/mapa.component.css` | Estilos del mapa | ✅ Creado |
| 3.3 | `src/app/components/menu-modal/menu-modal.component.ts` | Modal del menú | ✅ Creado |
| 3.3 | `src/app/components/menu-modal/menu-modal.component.html` | Template del modal | ✅ Creado |
| 3.3 | `src/app/components/menu-modal/menu-modal.component.css` | Estilos del modal | ✅ Creado |
| 3.4 | `src/app/app.component.ts` | Integración de componentes | ✅ Modificado |
| 3.4 | `src/app/app.component.html` | Template principal | ✅ Modificado |
| 3.4 | `src/styles.css` | Estilos globales | ✅ Modificado |

---

## Prompt Paso 1 — Crear endpoint GET /restaurants ✅ COMPLETADO

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
- Ya existe `RestauranteEntity.java` en `infrastructure.persistence` con campos: id, nombre, coordenadaX, coordenadaY, menu (JSON de productos)
- Ya existe `RestauranteJpaRepository.java` que extiende JpaRepository<RestauranteEntity, Long>
- La tabla restaurantes tiene datos insertados con menús en formato JSON

### Lo que necesito

1. Crear `RestauranteResponseDto.java` en `infrastructure.web.dto`:
   - id (Long)
   - nombre (String)
   - coordenadaX (Integer)
   - coordenadaY (Integer)
   - menu (List<ProductoMenuDto>)
   
2. Crear `ProductoMenuDto.java` en `infrastructure.web.dto`:
   - id (Long)
   - nombre (String)
   - precio (Double)

3. Crear `RestauranteService.java` en `application.service`:
   - Inyectar RestauranteJpaRepository
   - Método `List<RestauranteResponseDto> getAllRestaurantes()`
   - Método `RestauranteResponseDto getRestauranteById(Long id)`
   - Si no existe el restaurante, lanzar RestauranteNotFoundException

4. Crear `RestauranteController.java` en `infrastructure.web.controller`:
   - @RestController con @RequestMapping("/restaurants")
   - GET / → retorna lista de restaurantes
   - GET /{id} → retorna restaurante específico

### Reglas
- Usar Lombok (@Data, @Builder, etc.)
- El menú está guardado como JSON en la columna `menu`, parsearlo a List<ProductoMenuDto>
- Seguir el mismo estilo del OrderController existente
```

---

## Prompt Paso 2 — Crear endpoint GET /delivers ✅ COMPLETADO

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech`

### Contexto existente
- Ya existe modelo `Repartidor.java` en `domain.model` con: id, nombre, estado (EstadoRepartidor), vehiculo (Vehiculo), ubicacion (Coordenada)
- Ya existe `RepartidorRepository` (puerto) y `RepartidorPersistenceAdapter` (adaptador)
- Ya existe `AsignacionController.java` con endpoints POST /delivery y PUT /delivery/{id}/state

### Lo que necesito

1. Crear `RepartidorListResponseDto.java` en `infrastructure.web.dto`:
   - id (Long)
   - nombre (String)
   - estado (String) — "ACTIVO", "INACTIVO", "EN_ENTREGA"
   - vehiculo (String) — "BICICLETA", "MOTO", "AUTO"
   - ubicacionX (Integer)
   - ubicacionY (Integer)

2. Crear `DeliversController.java` en `infrastructure.web.controller`:
   - @RestController con @RequestMapping("/delivers")
   - GET / → retorna lista de todos los repartidores
   - GET /{id} → retorna repartidor específico

3. Agregar métodos en `AsignacionApplicationService.java`:
   - `List<RepartidorListResponseDto> getAllRepartidores()`
   - `Optional<RepartidorListResponseDto> getRepartidorById(Long id)`

### Reglas
- Usar el RepartidorRepository existente (findAll() y findById())
- Mapear Coordenada a ubicacionX/ubicacionY
- Mapear enums a String para el DTO
- Seguir el mismo estilo del controller existente
```

---

## Resumen de archivos creados/modificados

### order-service ✅

| Archivo | Acción | Estado |
|---------|--------|--------|
| `infrastructure/web/dto/ProductoMenuDto.java` | **Crear** | ✅ Creado |
| `infrastructure/web/dto/RestauranteResponseDto.java` | **Crear** | ✅ Creado |
| `application/service/RestauranteService.java` | **Crear** | ✅ Creado |
| `infrastructure/web/controller/RestauranteController.java` | **Crear** | ✅ Creado |

### delivery-service ✅

| Archivo | Acción | Estado |
|---------|--------|--------|
| `infrastructure/web/dto/RepartidorListResponseDto.java` | **Crear** | ✅ Creado |
| `infrastructure/web/controller/DeliversController.java` | **Crear** | ✅ Creado |
| `application/service/AsignacionApplicationService.java` | Modificar | ✅ Modificado |
| `domain/port/output/RepartidorRepository.java` | Modificar (agregado findAll) | ✅ Modificado |
| `infrastructure/persistence/adapter/RepartidorPersistenceAdapter.java` | Modificar (implementado findAll) | ✅ Modificado |

---

## Pruebas realizadas (curl)

```bash
# Listar todos los restaurantes
curl -s http://localhost:8081/restaurants | jq

# Obtener restaurante específico
curl -s http://localhost:8081/restaurants/1 | jq

# Listar todos los repartidores
curl -s http://localhost:8080/delivers | jq

# Obtener repartidor específico
curl -s http://localhost:8080/delivers/1 | jq
```

### Ejemplos de respuestas:

**GET /restaurants/1:**
```json
{
  "id": 1,
  "nombre": "La Hamburguesería",
  "coordenadaX": 50,
  "coordenadaY": 50,
  "menu": [
    {"id": 1, "nombre": "Hamburguesa Clásica", "precio": 18000.0},
    {"id": 2, "nombre": "Hamburguesa BBQ", "precio": 22000.0},
    {"id": 3, "nombre": "Papas fritas", "precio": 8000.0}
  ]
}
```

**GET /delivers/1:**
```json
{
  "id": 1,
  "nombre": "Carlos Mendoza",
  "estado": "ACTIVO",
  "vehiculo": "MOTO",
  "ubicacionX": 25,
  "ubicacionY": 40
}
```

---

## Estado del desarrollo

**Backend:** ✅ **COMPLETADO Y PROBADO**

El trabajo backend de HU10 está terminado. Los endpoints están funcionando y listos para ser consumidos por el frontend.

**Frontend:** ✅ **COMPLETADO**

| Paso | Descripción | Estado |
|------|-------------|--------|
| 3.1 | Servicios HTTP y modelos | ✅ Completado |
| 3.2 | MapaComponent con Canvas | ✅ Completado |
| 3.3 | MenuModalComponent | ✅ Completado |
| 3.4 | Integración en AppComponent | ✅ Completado |

**Endpoints disponibles:**
- ✅ `GET /restaurants` → Lista todos los restaurantes con sus menús (puerto 8081)
- ✅ `GET /restaurants/{id}` → Obtiene restaurante específico (puerto 8081)
- ✅ `GET /delivers` → Lista todos los repartidores con ubicaciones (puerto 8080)
- ✅ `GET /delivers/{id}` → Obtiene repartidor específico (puerto 8080)

**Dependencias:**
1. ✅ HU1-HU6 (delivery-service funcionando)
2. ✅ HU7-HU9 (order-service funcionando)
3. ✅ **HU10 Backend** ← COMPLETADO
4. 🔄 **HU10 Frontend** ← PROMPTS LISTOS, ejecutar pasos 3.1 → 3.4
