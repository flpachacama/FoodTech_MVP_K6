package com.foodtech.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionResponseDTO {

    private Long pedidoId;
    private String estado;
    private Long repartidorId;
    private String nombreRepartidor;
    private Integer tiempoEstimado;
}
