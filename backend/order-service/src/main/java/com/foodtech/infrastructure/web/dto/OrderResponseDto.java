package com.foodtech.infrastructure.web.dto;

import com.foodtech.domain.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;
    private Long restauranteId;
    private Long repartidorId;
    private List<ProductoPedidoDto> productos;
    private Long clienteId;
    private String clienteNombre;
    private Double clienteCoordenadasX;
    private Double clienteCoordenadasY;
    private String clienteTelefono;
    private Integer tiempoEstimado;
    private EstadoPedido estado;
}
