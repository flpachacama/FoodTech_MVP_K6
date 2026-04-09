package com.foodtech.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorListResponseDto {
    private Long id;
    private String nombre;
    private String estado;
    private String vehiculo;
    private Double ubicacionX;
    private Double ubicacionY;
}
