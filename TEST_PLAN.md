# TEST PLAN - FoodTech MVP

## 1. Identificación del Plan

- **Nombre del proyecto:** FoodTech MVP
- **Sistema bajo prueba (SUT):** Plataforma de delivery con algoritmo de asignación automática de repartidores
- **Version del plan:** 1.1
- **Fecha:** 24 de marzo de 2026

## 2. Contexto

FoodTech MVP busca reducir tiempos de entrega y mejorar la asignación de pedidos mediante reglas de negocio claras:

- Seleccionar repartidores por cercanía al restaurante.
- Priorizar por tiempo estimado según tipo de transporte.
- Aplicar restricciones por clima.
- Controlar estado operativo del repartidor.

El problema de negocio que resuelve es la asignación ineficiente de pedidos en escenarios de alta variabilidad, disminuyendo retrasos, reasignaciones y cancelaciones.

## 3. Alcance de las Pruebas

### IN 

Se cubren historias de usuario funcionales del MVP:

- HU1 Gestion de estado de repartidores
- HU2 Filtrado por cercanía
- HU3 Restricciones por clima
- HU4 Cálculo de prioridad
- HU5 Asignación automática de pedidos
- HU6 Actualización de estado del repartidor
- HU7 Generación de pedido
- HU8 Confirmación de pedido y tiempo estimado
- HU9 Cancelación de pedido
- HU10 Visualización y selección de restaurantes

### OUT

- HU11 Visualización del pedido en interfaz repartidor
- HU12 Marcar pedido como entregado

## 4. Estrategia de Pruebas

Se aplicará una estrategia por capas para detectar defectos temprano y reducir trabajo repetitivo.

### 4.1 Pruebas funcionales E2E y de aceptación - Serenity BDD + Cucumber

**Objetivo:** validar flujos de negocio completos desde perspectiva usuario/sistema usando criterios Gherkin de `USER_STORIES.md`.

**Aplicación en el proyecto:**

- Definir features por HU prioritaria (HU1-HU10).
- Automatizar escenarios críticos:
  - Asignación exitosa con repartidor valido
  - Sin repartidores disponibles
  - Restricción por clima en `LLUVIA_FUERTE`
  - Cambio de estado `ACTIVO -> EN_ENTREGA -> ACTIVO`
  - Cancelación y liberación de repartidor
- Generar evidencia ejecutiva en reportes Serenity para demo de sprint y gate de release.

### 4.2 Pruebas de API - Karate

**Objetivo:** validar contratos, reglas de negocio y respuestas de microservicios `order` y `delivery`.

**Aplicación en el proyecto:**

- Pruebas de endpoints principales:
  - `POST /orders`
  - `PUT /orders/{id}/cancel`
  - `PUT /orders/{id}/delivered`
  - `POST /delivery`
  - `PUT /delivery/{id}/state`
  - `GET /orders/{id}/active-order`
- Cobertura de casos positivos, negativos y bordes:
  - Campos obligatorios
  - Estado inválido
  - Sin candidatos
  - Reglas de clima
- Validar consistencia de estados entre servicios.

### 4.3 Pruebas de rendimiento baseline - k6

**Objetivo:** medir comportamiento bajo carga controlada para detectar cuellos de botella temprano.

**Aplicación en el proyecto:**

- Escenarios iniciales:
  - Rampa de creación de pedidos
  - Cancelaciones concurrentes
  - Picos de asignación en clima adverso
- Métricas observadas:
  - `p95` de tiempo de respuesta por endpoint critico
  - tasa de error
  - throughput
- Resultado esperado del MVP: establecer línea base y riesgos de escalabilidad para siguientes iteraciones.

## 5. Criterios de Entrada y Salida

### Criterios de Entrada

Para iniciar pruebas de una HU/sprint se requiere:

- Historia refinada con criterios de aceptación claros.
- Ambiente local estable y accesible para QA.
- Datos de prueba disponibles (repartidores, restaurantes, clima, pedidos).
- Build desplegable del sprint con cambios integrados.
- Casos de prueba revisados por QA.

### Criterios de Salida

Para cerrar pruebas de una HU/sprint se requiere:

- 100% de escenarios críticos ejecutados.
- 100% de pruebas bloqueantes en estado aprobado.
- Sin defectos críticos ni altos abiertos.
- Evidencia publicada.
- Trazabilidad HU -> caso de prueba -> resultado.

## 6. Entorno de Pruebas

### Configuración del entorno

- Ejecución local en entorno de desarrollo (MVP).
- Dos microservicios activos:
  - `order`
  - `delivery`
- Frontend web para flujo consumidor.

### Datos simulados

- Repartidores con estados: `ACTIVO`, `INACTIVO`, `EN_ENTREGA`.
- Vehículos: `BICICLETA`, `MOTO`, `AUTO`.
- Clima: `SOLEADO`, `LLUVIA_SUAVE`, `LLUVIA_FUERTE`.
- Coordenadas `X,Y` simuladas para restaurantes, clientes y repartidores.
- Pedidos en estados: `PENDIENTE`, `ASIGNADO`, `ENTREGADO`, `CANCELADO`.

### Ejecución local

- Ejecución por suite funcional (Serenity+Cucumber), API (Karate) y rendimiento (k6).
- Corridas mínimas por HU en pipeline local de QA antes de cierre de sprint.

## 7. Herramientas

- **Serenity BDD:** Orquesta pruebas de aceptación y genera reportes ejecutivos con evidencia clara para negocio y equipo técnico.
- **Cucumber:** Define escenarios en lenguaje natural (Gherkin), facilitando alineación entre QA y DEV.
- **Karate:** Automatiza pruebas API y validación de contratos sin alta complejidad de código.
- **k6:** Ejecuta pruebas de carga y rendimiento para identificar latencia, errores bajo concurrencia y límites del MVP.

## 8. Roles y Responsabilidades

### QA

- Diseñar estrategia de pruebas por sprint y por HU.
- Definir y mantener casos de prueba manuales/automatizados.
- Automatizar suites con Serenity+Cucumber y Karate.
- Ejecutar pruebas funcionales, API y baseline de rendimiento.
- Reportar defectos con evidencia, severidad y criterio de reproducción.
- Gestionar métricas de calidad y recomendar decision de salida (Go/No-Go).

### DEV

- Implementar funcionalidades y criterios de aceptación acordados.
- Corregir defectos priorizados dentro del sprint.
- Mantener estabilidad técnica del entorno para pruebas.
- Soportar análisis de causa raíz en defectos críticos.
- Participar en definición de datos de prueba y observabilidad básica.

## 9. Cronograma y Estimación

La planificación QA se alinea a Story Points del backlog y se ejecuta de forma incremental por fases.

| Fase                         | HU incluidas | SP QA estimados |
|------------------------------|---|----------------:|
| Fase 1 - Modelo base         | HU1, HU2, HU3 |              11 |
| Fase 1 - Algoritmo           | HU4, HU5, HU6 |              13 |
| Fase 2 - Flujo de pedidos    | HU7, HU8, HU9 |              16 |
| Fase 2 - UX consumidor       | HU10 |               3 |
| **Total QA**                 | **HU1-HU12** |          **43** |

### Distribución recomendada por micro - sprint

- **Micro - sprint 1:** HU1-HU5 + automatización base + regresión parcial (21 SP QA / 14 SP DEV)
- **Micro - sprint 2:** HU6-HU10 + prueba de integración de flujo completo + regresión lineal + baseline k6 (22 SP QA / 20 SP DEV)

## 10. Entregables de Prueba

- Matriz de cobertura HU vs casos de prueba.
- Casos de prueba funcionales (Gherkin) versionados.
- Scripts automatizados:
  - Serenity BDD + Cucumber
  - Karate
  - k6
- Reportes de ejecución por sprint y reporte final de regresión.
- Registro de defectos con severidad/prioridad y estado.
- Métricas de calidad:
  - porcentaje de escenarios aprobados
  - defectos por severidad
  - tendencia de trabajo repetitivo
  - estabilidad de endpoints críticos

## 11. Riesgos y Contingencias

| Riesgo | Impacto | Mitigacion preventiva | Contingencia |
|---|---|---|---|
| Error en calculo de distancia | Asignaciones incorrectas y mayor tiempo de entrega | Pruebas de formula con casos controlados, limites y regresion automatizada HU2/HU4 | Hotfix priorizado + recalculo controlado + monitoreo reforzado |
| Escalabilidad insuficiente | Lentitud o errores en picos de pedidos | Baseline temprano con k6 en endpoints criticos y ajustes iterativos | Limitar concurrencia temporal, priorizar colas y optimizar consultas |
| Falta de repartidores disponibles | Pedidos en estado pendiente y mala experiencia usuario | Validar mensajes claros y flujo PENDIENTE en HU5/HU8; preparar reglas de fallback | Notificar disponibilidad limitada y habilitar reintento manual del usuario |
| Alta demanda en horarios pico | Aumento de latencia y cancelaciones | Pruebas de carga por ventanas pico, pruebas de cancelacion concurrente | Plan operativo de contingencia y priorizacion de pedidos por SLA |
| Error en captura/aplicacion de clima | Exclusiones incorrectas de vehiculos y riesgo operativo | Casos cruzados clima-transporte (HU3) y validaciones de consistencia en API | Desactivar regla defectuosa por feature flag temporal y aplicar correccion urgente |

---

## Enfoque de mejora continua

Este plan se revisa al cierre de cada micro - sprint en retrospectiva técnica QA-DEV para ajustar cobertura, priorización de riesgo y deuda de automatización. El objetivo es prevenir defectos desde refinamiento, no solo detectarlos en validación final.

---
- **Equipo:**
  - QA : Freddy Leonel
  - DEV : Omar Ortiz