-- =============================================================
-- Script de inicialización de bases de datos
-- Se ejecuta automáticamente al crear el contenedor de Postgres
-- foodtech_db  → delivery-service (creada por POSTGRES_DB)
-- foodtech_orders → order-service  (creada aquí)
-- =============================================================

SELECT 'CREATE DATABASE foodtech_orders OWNER foodtech_user'
WHERE NOT EXISTS (
    SELECT FROM pg_database WHERE datname = 'foodtech_orders'
)\gexec
