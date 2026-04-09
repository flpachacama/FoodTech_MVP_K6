package com.foodtech.domain.model;

/**
 * Tipo de vehículo de un repartidor con velocidad asociada en km/h.
 */
public enum TipoVehiculo {
    BICICLETA(10),
    MOTO(20),
    AUTO(30);

    private final int velocidadKmH;

    TipoVehiculo(int velocidadKmH) {
        this.velocidadKmH = velocidadKmH;
    }

    public int getVelocidadKmH() {
        return velocidadKmH;
    } 
}
