package com.foodtech.domain.exception;

public class RestauranteNotFoundException extends RuntimeException {

    public RestauranteNotFoundException(Long restauranteId) {
        super("Restaurante no encontrado con id: " + restauranteId);
    }
}
