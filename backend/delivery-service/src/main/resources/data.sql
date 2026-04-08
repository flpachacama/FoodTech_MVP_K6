-- =============================================================
-- Datos iniciales para la tabla repartidores
-- Coordenadas reales Bogotá: x = lng, y = lat
-- Distribuidos cerca de las zonas de los restaurantes
--   Zona Chapinero (lng -74.06, lat 4.64-4.66)
--   Zona Usaquén   (lng -74.03, lat 4.69-4.71)
--   Zona Rosa      (lng -74.05, lat 4.66-4.68)
--   Zona Teusaquillo (lng -74.07, lat 4.63-4.65)
-- Idempotente: no inserta si ya existen (basado en id)
-- =============================================================

-- Zona Chapinero (cerca La Hamburguesería)
INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (1, 'Carlos Mendoza', 'ACTIVO', 'MOTO', -74.0648, 4.6465)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (2, 'Ana Rodríguez', 'ACTIVO', 'BICICLETA', -74.0601, 4.6510)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (3, 'Luis Fernández', 'EN_ENTREGA', 'AUTO', -74.0590, 4.6440)
ON CONFLICT (id) DO NOTHING;

-- Zona Usaquén (cerca Pizzería Napoli)
INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (4, 'María González', 'EN_ENTREGA', 'MOTO', -74.0340, 4.6935)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (5, 'Pedro Sánchez', 'INACTIVO', 'AUTO', -74.0295, 4.6970)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (6, 'Pedro Marquez', 'ACTIVO', 'BICICLETA', -74.0310, 4.6982)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (7, 'Laura Jimenez', 'ACTIVO', 'MOTO', -74.0285, 4.6910)
ON CONFLICT (id) DO NOTHING;

-- Zona Rosa (cerca Sushi Kyoto)
INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (8, 'Carla Vargas', 'ACTIVO', 'BICICLETA', -74.0518, 4.6695)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (9, 'Omar Ortiz', 'ACTIVO', 'AUTO', -74.0552, 4.6658)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (10, 'Javier Angarita', 'ACTIVO', 'MOTO', -74.0570, 4.6640)
ON CONFLICT (id) DO NOTHING;

-- Zona Teusaquillo (cerca Perros Calientes El Vecino)
INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (11, 'Sofía Herrera', 'ACTIVO', 'MOTO', -74.0735, 4.6340)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (12, 'Diego Ramírez', 'ACTIVO', 'BICICLETA', -74.0700, 4.6310)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (13, 'Valentina Torres', 'ACTIVO', 'AUTO', -74.0750, 4.6360)
ON CONFLICT (id) DO NOTHING;

-- Dispersos por Bogotá centro-norte
INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (14, 'Sebastián Castro', 'ACTIVO', 'MOTO', -74.0450, 4.6780)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (15, 'Camila Morales', 'ACTIVO', 'BICICLETA', -74.0480, 4.6590)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (16, 'Andrés Gutiérrez', 'ACTIVO', 'AUTO', -74.0390, 4.6820)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (17, 'Isabella Vargas', 'ACTIVO', 'MOTO', -74.0660, 4.6550)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (18, 'Mateo Pineda', 'EN_ENTREGA', 'AUTO', -74.0420, 4.6900)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y)
VALUES (19, 'Lucía Navarro', 'ACTIVO', 'BICICLETA', -74.0495, 4.6730)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (20, 'Felipe Ríos', 'INACTIVO', 'MOTO', 22.22, 78.78)
ON CONFLICT (id) DO NOTHING;