package com.foodtech.domain.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long pedidoId) {
        super("Pedido no encontrado con id: " + pedidoId);
    }
}
