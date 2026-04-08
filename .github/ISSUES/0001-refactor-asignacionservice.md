Title: Refactorizar `AsignacionService` - extraer comparator y limpieza de lógica de prioridad

Description:

Motivación:
 - Actualmente la lógica de priorización de repartidores (cálculo de tiempo estimado y ordenamiento) está incrustada en el stream dentro de `AsignacionService`. Para mejorar la legibilidad y facilitar pruebas unitarias, sería conveniente extraer esta lógica a un comparador/utility y crear tests unitarios dedicados.

Propuesta de cambios (refactor):
 - Extraer un `Comparator<Repartidor>` (por ejemplo `RepartidorComparators.byEstimatedTime(Coordenada restaurante)`) que encapsule:
   - cálculo de distancia (`Coordenada.distanciaA`) y tiempo estimado (`calcularTiempoEstimado(distancia, velocidad)`).
 - Reemplazar la llamada `.sorted(Comparator.comparingDouble(...))` por `.sorted(RepartidorComparators.byEstimatedTime(restaurante))`.
 - Añadir tests unitarios para `RepartidorComparators` que validen orden en distintos escenarios (distintos vehiculos, empates, misma posición).
 - (Opcional) Mover `calcularTiempoEstimado` a una clase utilitaria para que sea reutilizable y testeable por separado.

Beneficios:
 - Código más limpio y adherente a Single Responsibility Principle.
 - Comparadores reutilizables y testeables de forma aislada.
 - Facilita futuras optimizaciones (cacheo de distancias, métricas diferentes).

Notas:
 - Ya se ha aplicado un cambio inicial que usa `Comparator.comparingDouble` en `AsignacionService` como paso intermedio. Este issue agrupa la refactorización completa.

Files to touch:
 - `backend/src/main/java/com/foodtech/domain/service/AsignacionService.java`
 - `backend/src/main/java/com/foodtech/domain/service/RepartidorComparators.java` (nuevo)
 - `backend/src/test/java/com/foodtech/domain/service/RepartidorComparatorsTest.java` (nuevo)
