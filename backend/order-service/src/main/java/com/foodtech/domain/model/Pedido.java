package com.foodtech.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Long id;
    private EstadoPedido estado;
    private Long restauranteId;
    private Long repartidorId;
    private List<ProductoPedido> productos;
    private Long clienteId;
    private String clienteNombre;
    private Double clienteCoordenadasX;
    private Double clienteCoordenadasY;
    private Integer tiempoEstimado;
}
