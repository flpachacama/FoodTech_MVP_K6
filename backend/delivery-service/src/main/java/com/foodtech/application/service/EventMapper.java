package com.foodtech.application.service;

import com.foodtech.domain.model.EstadoRepartidor;

/**
 * Traduce eventos externos (p. ej. desde Order) a estados internos de dominio.
 */
public final class EventMapper {

    private EventMapper() {}

    /**
     * Mapea un evento externo a un {@link EstadoRepartidor} del dominio.
     * Lanzará IllegalArgumentException si el evento no es válido.
     */
    public static EstadoRepartidor mapToEstado(String evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento inválido: null");
        }

        return switch (evento) {
            case "ENTREGADO" -> EstadoRepartidor.ACTIVO;
            case "CANCELADO" -> EstadoRepartidor.ACTIVO;
            default -> throw new IllegalArgumentException("Evento inválido: " + evento);
        };
    }
}
