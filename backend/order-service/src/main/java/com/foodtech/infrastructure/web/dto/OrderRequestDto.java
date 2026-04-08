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
public class OrderRequestDto {

    private Long restauranteId;
    private Double restauranteX;
    private Double restauranteY;
    private String clima;
    private List<ProductoPedidoDto> productos;
    private Long clienteId;
    private String clienteNombre;
    private Double clienteCoordenadasX;
    private Double clienteCoordenadasY;
    private String clienteTelefono;
}
