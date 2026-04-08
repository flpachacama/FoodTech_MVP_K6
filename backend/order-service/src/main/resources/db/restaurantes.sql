-- =============================================================
-- Script SQL: Creación de la tabla restaurantes
-- Microservicio: order-service
-- Paquete: com.foodtech.order
-- =============================================================

CREATE TABLE IF NOT EXISTS restaurantes (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(255)    NOT NULL,
    coordenada_x  DOUBLE          NULL,
    coordenada_y  DOUBLE          NULL,
    menu          TEXT            NULL,
    PRIMARY KEY (id)
);
