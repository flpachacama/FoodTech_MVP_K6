package com.foodtech.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteResponseDto {

    private Long id;
    private String nombre;
    private Double coordenadaX;
    private Double coordenadaY;
    private List<ProductoMenuDto> menu;
}
