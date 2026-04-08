# Casos de Prueba - FoodTech API

## 📋 Información General

**Base URLs:**
- Order Service: `http://localhost:8081`
- Delivery Service: `http://localhost:8080`

**Estado de datos iniciales:**
- Restaurantes: 3 (IDs: 1, 2, 3)
- Repartidores: 5 (IDs: 1-5)

**Valores de clima válidos:**
- `SOLEADO`: Todos los vehículos disponibles
- `LLUVIA_SUAVE`: Todos los vehículos disponibles
- `LLUVIA_FUERTE`: Solo AUTO y MOTO (excluye BICICLETA)

---

## 🍔 Order Service - Crear Pedido

### Endpoint
```
POST http://localhost:8081/orders
Content-Type: application/json
```

---

### ✅ Caso 1: Pedido Exitoso - La Hamburguesería (Sin lluvia)

**Request:**
```json
{
  "restauranteId": 1,
  "restauranteX": 10,
  "restauranteY": 10,
  "clima": "SOLEADO",
  "productos": [
    {
      "id": 1,
      "nombre": "Hamburguesa Clásica",
      "precio": 18000
    },
    {
      "id": 4,
      "nombre": "Gaseosa",
      "precio": 5000
    }
  ],
  "clienteId": 101,
  "clienteNombre": "Juan Pérez",
  "clienteCoordenadasX": 15,
  "clienteCoordenadasY": 20,
  "clienteTelefono": "3001234567"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "restauranteId": 1,
  "productos": [
    {
      "id": 1,
      "nombre": "Hamburguesa Clásica",
      "precio": 18000
    },
    {
      "id": 4,
      "nombre": "Gaseosa",
      "precio": 5000
    }
  ],
  "clienteId": 101,
  "clienteNombre": "Juan Pérez",
  "clienteCoordenadasX": 15,
  "clienteCoordenadasY": 20,
  "clienteTelefono": "3001234567",
  "tiempoEstimado": <número>,
  "estado": "ASIGNADO" o "PENDIENTE"
}
```

**Validaciones:**
- ✅ Status code: 201
- ✅ Se retorna un ID de pedido
- ✅ Estado puede ser ASIGNADO o PENDIENTE según disponibilidad de repartidores
- ✅ Tiempo estimado calculado

---

### ✅ Caso 2: Pedido con Lluvia Suave - Pizzería Napoli

**Request:**
```json
{
  "restauranteId": 2,
  "restauranteX": 50,
  "restauranteY": 40,
  "clima": "LLUVIA_SUAVE",
  "productos": [
    {
      "id": 1,
      "nombre": "Pizza Margherita",
      "precio": 25000
    },
    {
      "id": 4,
      "nombre": "Agua Mineral",
      "precio": 4000
    }
  ],
  "clienteId": 102,
  "clienteNombre": "María García",
  "clienteCoordenadasX": 55,
  "clienteCoordenadasY": 45,
  "clienteTelefono": "3109876543"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 2,
  "restauranteId": 2,
  "productos": [...],
  "clienteId": 102,
  "clienteNombre": "María García",
  "clienteCoordenadasX": 55,
  "clienteCoordenadasY": 45,
  "clienteTelefono": "3109876543",
  "tiempoEstimado": <número>,
  "estado": "ASIGNADO" o "PENDIENTE"
}
```

**Validaciones:**
- ✅ Status code: 201
- ✅ Con LLUVIA_SUAVE todos los vehículos pueden ser asignados
- ✅ Con LLUVIA_FUERTE se deberían excluir repartidores en BICICLETA

---

### ✅ Caso 3: Pedido Completo - Sushi Kyoto

**Request:**
```json
{
  "restauranteId": 3,
  "restauranteX": 90,
  "restauranteY": 70,
  "clima": "NUBLADO",
  "productos": [
    {
      "id": 1,
      "nombre": "Roll Philadelphia",
      "precio": 32000
    },
    {
      "id": 3,
      "nombre": "Edamame",
      "precio": 12000
    }
  ],
  "clienteId": 103,
  "clienteNombre": "Carlos Ruiz",
  "clienteCoordenadasX": 85,
  "clienteCoordenadasY": 75,
  "clienteTelefono": "3157654321"
}
```

**Expected Response (201 Created):**
- Similar a casos anteriores

---

### ❌ Caso 4: Error - Restaurante No Existe

**Request:**
```json
{
  "restauranteId": 999,
  "restauranteX": 10,
  "restauranteY": 10,
  "clima": "SOLEADO",
  "productos": [
    {
      "id": 1,
      "nombre": "Test",
      "precio": 10000
    }
  ],
  "clienteId": 999,
  "clienteNombre": "Test User",
  "clienteCoordenadasX": 15,
  "clienteCoordenadasY": 20,
  "clienteTelefono": "3001234567"
}
```

**Expected Response (404 Not Found):**
```json
{
  "timestamp": "2026-03-27T16:20:21.158367427Z",
  "status": 404,
  "error": "Restaurante no encontrado",
  "detail": "Restaurante no encontrado con id: 999"
}
```

**Validaciones:**
- ✅ Status code: 404
- ✅ Error message indica que el restaurante no existe
- ✅ NO se crea el pedido en la base de datos

---

### ❌ Caso 5: Error - Campos Faltantes

**Request:**
```json
{
  "restauranteId": 1,
  "productos": [
    {
      "id": 1,
      "nombre": "Hamburguesa",
      "precio": 18000
    }
  ]
}
```

**Expected Response (400 Bad Request):**
```json
{
  "timestamp": "2026-03-27T...",
  "status": 400,
  "error": "Datos inválidos",
  "detail": "El nombre del cliente es obligatorio"
}
```

**Validaciones:**
- ✅ Status code: 400
- ✅ Error message indica qué campos son obligatorios

---

### ✅ Caso 6: Pedido Sin Clima (Default)

**Request:**
```json
{
  "restauranteId": 1,
  "restauranteX": 10,
  "restauranteY": 10,
  "productos": [
    {
      "id": 2,
      "nombre": "Hamburguesa BBQ",
      "precio": 22000
    }
  ],
  "clienteId": 104,
  "clienteNombre": "Ana López",
  "clienteCoordenadasX": 12,
  "clienteCoordenadasY": 15,
  "clienteTelefono": "3201112233"
}
```

**Validaciones:**
- ✅ Si clima es null, todos los repartidores disponibles pueden ser asignados

---

## 🚚 Delivery Service - Asignar Repartidor

### Endpoint
```
POST http://localhost:8080/delivery
Content-Type: application/json
```

---

### ✅ Caso 7: Asignación Exitosa - Sin restricciones

**Request:**
```json
{
  "pedidoId": 1,
  "restauranteX": 25,
  "restauranteY": 40,
  "clima": "SOLEADO"
}
```

**Expected Response (200 OK):**
```json
{
  "pedidoId": 1,
  "estado": "ASIGNADO",
  "repartidorId": 1,
  "nombreRepartidor": "Carlos Mendoza"
}
```

**Validaciones:**
- ✅ Repartidor más cercano activo es asignado
- ✅ Estado = "ASIGNADO"

---

### ✅ Caso 8: Asignación con LLUVIA

**Request:**
```json
{
  "pedidoId": 2,
  "restauranteX": 60,
  "restauranteY": 15,
  "clima": "LLUVIA"
}
```

**Expected Response (200 OK):**
```json
{
  "pedidoId": 2,
  "estado": "ASIGNADO",
  "repartidorId": <no debería ser ID 2 (bicicleta)>,
  "nombreRepartidor": "<nombre del repartidor en AUTO o MOTO>"
}
```

**Validaciones:**
- ✅ No se asigna el repartidor ID 2 (Ana Rodríguez - BICICLETA)
- ✅ Solo AUTO o MOTO

---

### ✅ Caso 9: Sin Repartidores Disponibles

**Contexto:** Después de asignar a todos los repartidores activos

**Request:**
```json
{
  "pedidoId": 10,
  "restauranteX": 100,
  "restauranteY": 100,
  "clima": "SOLEADO"
}
```

**Expected Response (200 OK):**
```json
{
  "pedidoId": 10,
  "estado": "PENDIENTE",
  "repartidorId": null,
  "nombreRepartidor": null
}
```

**Validaciones:**
- ✅ Estado = "PENDIENTE"
- ✅ repartidorId y nombreRepartidor son null

---

### ❌ Caso 10: Error - Campos Faltantes

**Request:**
```json
{
  "pedidoId": 1
}
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "La coordenada X es obligatoria"
}
```

---

### ✅ Caso 11: Asignación Sin Clima

**Request:**
```json
{
  "pedidoId": 3,
  "restauranteX": 45,
  "restauranteY": 55
}
```

**Expected Response (200 OK):**
```json
{
  "pedidoId": 3,
  "estado": "ASIGNADO",
  "repartidorId": <id del más cercano>,
  "nombreRepartidor": "<nombre>"
}
```

**Validaciones:**
- ✅ Sin clima, todos los repartidores activos son candidatos

---

## 🔄 Delivery Service - Actualizar Estado Repartidor

### Endpoint
```
PUT http://localhost:8080/delivery/{id}/state
Content-Type: application/json
```

---

### ✅ Caso 12: Repartidor Completa Entrega

**Request:**
```
PUT http://localhost:8080/delivery/1/state
```
```json
{
  "evento": "ENTREGA_COMPLETADA"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Carlos Mendoza",
  "estado": "ACTIVO",
  "vehiculo": "MOTO",
  "x": 25,
  "y": 40
}
```

**Validaciones:**
- ✅ Estado cambia de EN_ENTREGA a ACTIVO
- ✅ Repartidor queda disponible para nuevas asignaciones

---

### ✅ Caso 13: Repartidor Comienza Entrega

**Request:**
```
PUT http://localhost:8080/delivery/2/state
```
```json
{
  "evento": "ACEPTA_PEDIDO"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 2,
  "nombre": "Ana Rodríguez",
  "estado": "EN_ENTREGA",
  "vehiculo": "BICICLETA",
  "x": 60,
  "y": 15
}
```

**Validaciones:**
- ✅ Estado cambia de ACTIVO a EN_ENTREGA

---

### ❌ Caso 14: Error - Repartidor No Existe

**Request:**
```
PUT http://localhost:8080/delivery/999/state
```
```json
{
  "evento": "ENTREGA_COMPLETADA"
}
```

**Expected Response (404 Not Found):**
```json
{
  "error": "Repartidor no encontrado"
}
```

---

### ❌ Caso 15: Error - Evento Inválido

**Request:**
```
PUT http://localhost:8080/delivery/1/state
```
```json
{
  "evento": "EVENTO_INVALIDO"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Evento no válido: EVENTO_INVALIDO"
}
```

---

### ✅ Caso 16: Repartidor se Desconecta

**Request:**
```
PUT http://localhost:8080/delivery/5/state
```
```json
{
  "evento": "DESCONECTAR"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 5,
  "nombre": "Pedro Sánchez",
  "estado": "INACTIVO",
  "vehiculo": "AUTO",
  "x": 45,
  "y": 55
}
```

---

## 🔁 Flujo Completo de Integración

### Escenario: Pedido Completo Exitoso

1. **Crear pedido** (Order Service)
   ```
   POST http://localhost:8081/orders
   ```
   - Guarda: `pedidoId` del response

2. **Verificar asignación automática**
   - Si estado = "ASIGNADO": El order-service ya llamó automáticamente al delivery-service
   - Si estado = "PENDIENTE": No hay repartidores disponibles

3. **Repartidor completa entrega** (Delivery Service)
   ```
   PUT http://localhost:8080/delivery/{repartidorId}/state
   ```
   ```json
   {
     "evento": "ENTREGA_COMPLETADA"
   }
   ```

4. **Verificar disponibilidad**
   - El repartidor vuelve a estar ACTIVO
   - Puede recibir nuevas asignaciones

---

## 📊 Datos de Prueba Disponibles

### Restaurantes (foodtech_orders)
| ID | Nombre | X | Y |
|----|--------|---|---|
| 1 | La Hamburguesería | 10 | 10 |
| 2 | Pizzería Napoli | 50 | 40 |
| 3 | Sushi Kyoto | 90 | 70 |

### Repartidores (foodtech_db)
| ID | Nombre | Estado | Vehículo | X | Y |
|----|--------|--------|----------|---|---|
| 1 | Carlos Mendoza | ACTIVO | MOTO | 25 | 40 |
| 2 | Ana Rodríguez | ACTIVO | BICICLETA | 60 | 15 |
| 3 | Luis Fernández | EN_ENTREGA | AUTO | 80 | 75 |
| 4 | María González | EN_ENTREGA | MOTO | 10 | 90 |
| 5 | Pedro Sánchez | INACTIVO | AUTO | 45 | 55 |

---

## 🎯 Orden Sugerido de Pruebas

1. **Pruebas básicas de Order Service** (Casos 1-3)
2. **Pruebas con clima** (Caso 2 con LLUVIA)
3. **Validaciones de error** (Casos 4-5)
4. **Asignación directa** (Casos 7-9)
5. **Gestión de estados** (Casos 12-16)
6. **Flujo completo** (Escenario integración)

---

## 🔧 Tips para Postman

1. **Variables de entorno:**
   ```
   order_service_url = http://localhost:8081
   delivery_service_url = http://localhost:8080
   ```

2. **Guardar IDs en variables:**
   ```javascript
   // En Tests tab después de crear pedido:
   pm.environment.set("pedido_id", pm.response.json().id);
   pm.environment.set("repartidor_id", pm.response.json().repartidorId);
   ```

3. **Pre-request Scripts útiles:**
   ```javascript
   // Generar coordenadas aleatorias
   pm.environment.set("random_x", Math.floor(Math.random() * 100));
   pm.environment.set("random_y", Math.floor(Math.random() * 100));
   ```

4. **Test Scripts básicos:**
   ```javascript
   pm.test("Status code is 201", () => {
       pm.response.to.have.status(201);
   });
   
   pm.test("Response has id", () => {
       pm.expect(pm.response.json()).to.have.property('id');
   });
   ```

---

## ✅ Checklist de Validación

- [ ] Todos los casos exitosos retornan status 2xx
- [ ] Casos de error retornan status 4xx apropiado
- [ ] Filtro de clima funciona (excluye bicicletas en lluvia)
- [ ] Asignación por distancia funciona correctamente
- [ ] Estados de repartidor se actualizan correctamente
- [ ] Integración order-service → delivery-service funciona
- [ ] Manejo de repartidores no disponibles
- [ ] Validaciones de campos requeridos funcionan
