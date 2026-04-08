package com.foodtech.infrastructure.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionRequestDTO {

    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    @NotNull(message = "La coordenada X es obligatoria")
    @JsonDeserialize(using = StrictDoubleDeserializer.class)
    private Double restauranteX;

    @NotNull(message = "La coordenada Y es obligatoria")
    @JsonDeserialize(using = StrictDoubleDeserializer.class)
    private Double restauranteY;

    private String clima;
}
