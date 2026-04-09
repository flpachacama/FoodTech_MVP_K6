package com.foodtech.domain.exception;

public class RepartidorNotFoundException extends RuntimeException {

    public RepartidorNotFoundException(Long id) {
        super("Repartidor no encontrado con id: " + id);
    }
}
