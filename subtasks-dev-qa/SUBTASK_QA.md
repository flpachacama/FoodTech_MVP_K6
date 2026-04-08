## HU1 - Gestionar estado de repartidores

* Diseñar casos de prueba por cada estado
* Validar que EN_ENTREGA e INACTIVO no sean seleccionados
* Probar cambios de estado en distintos escenarios
* Validar integridad de estados en flujo completo
------------------------------------------------------------
## HU2 - Filtrar repartidores por cercanía

* Validar cálculo correcto de distancias
* Probar ordenamiento correcto
* Validar comportamiento sin repartidores activos
* Probar casos límite
------------------------------------------------------------
## HU3 - Aplicar restricciones por clima

* Probar cada tipo de clima
* Validar exclusión de bici y moto en lluvia fuerte
* Validar combinaciones clima + transporte
* Verificar comportamiento sin candidatos
------------------------------------------------------------
## HU4 - Calcular prioridad de repartidores 

* Validar cálculo correcto del tiempo estimado
* Probar diferentes combinaciones de transporte
* Validar comportamiento en empates
* Verificar consistencia de resultados
------------------------------------------------------------
## HU5 - Asignar pedido automaticamente

* Validar asignación correcta
* Probar escenarios sin repartidores
* Validar que siempre se elige el mejor candidato
* Verificar consistencia del flujo
------------------------------------------------------------
## HU6 - Actualizar estado del repartidor

* Probar transición ACTIVO -> EN_ENTREGA
* Probar transición EN_ENTREGA -> ACTIVO
* Validar cambios en cancelación
* Detectar estados inconsistentes
------------------------------------------------------------
## HU7 - Generar Pedido

* Probar agregar múltiples productos
* Validar eliminación de productos
* Verificar cálculo de total
* Validar restricción de carrito vacío
------------------------------------------------------------
## HU8 - Ingresar datos y confirmar pedido

* Validar campos obligatorios
* Probar confirmación exitosa
* Probar sin repartidores disponibles
* Validar visualización del tiempo estimado
------------------------------------------------------------
## HU9 - Cancelar pedido

* Validar cancelación antes de asignación
* Verificar cambio de estado
* Validar liberación del repartidor
* Probar multiples cancelaciones
------------------------------------------------------------
## HU10 - Visualizar y seleccionar restaurante

* Validar visualización en mapa
* Probar selección de restaurante
* Verificar carga de menú
* Validar caso sin restaurantes
------------------------------------------------------------
## HU11 - Visualizar pedido asignado

* Validar datos mostrados
* Probar repartidor sin pedidos
* Verificar consistencia de información
* Validar visualización del tiempo
------------------------------------------------------------
## HU12 - Marcar pedido como entregado

* Validar cambio de estado
* Verificar retorno a Activo
* Validar desaparición del tiempo
* Probar flujo completo de entrega
------------------------------------------------------------

## Estimación del esfuerzo de pruebas – Story Points

| HU    | SP (QA) | Justificación                                                                               |
|-------|---------|---------------------------------------------------------------------------------------------|
| HU1   | 3 | Validación de múltiples estados y su impacto en el sistema                                  |
| HU2   | 5 | Validación de cálculos, ordenamiento y casos límite                                         |
| HU3   | 3 | Validación de combinaciones clima-transporte y exclusión correcta                           |
| HU4   | 5 | Validación de fórmula, comparaciones y casos de empate                                      |
| HU5   | 5 | Validación del flujo completo de asignación y selección correcta del mejor candidato        |
| HU6   | 3 | Validación de transiciones de estado en múltiples escenarios                                |
| HU7   | 5 | Validación de múltiples interacciones del usuario                                           |
| HU8   | 8 | Alta cantidad de escenarios: validaciones, asignación, sin repartidores, errores de usuario |
| HU9   | 3 | Validación de flujo alterno y consistencia del estado del sistema                           |
| HU10  | 3 | Validación visual y comportamiento                                                          |
| HU11  | 3 | Validación de datos mostrados y escenarios con o sin pedido                                 |
| HU12  | 3 | Validación end-to-end del flujo de entrega y cambios de estado                              |