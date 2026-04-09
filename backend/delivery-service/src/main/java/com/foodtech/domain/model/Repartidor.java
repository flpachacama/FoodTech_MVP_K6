package com.foodtech.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Entidad de dominio que representa a un repartidor.
 */
@Getter
@Builder
@AllArgsConstructor
public class Repartidor {

    private Long id;
    private String nombre;
    private EstadoRepartidor estado;
    private TipoVehiculo vehiculo;
    private Coordenada ubicacion;
}
