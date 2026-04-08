package com.foodtech.domain.exception;

public class PedidoDeliverException extends RuntimeException {

    public PedidoDeliverException(Long pedidoId, String motivo) {
        super("No se puede marcar como entregado el pedido " + pedidoId + ": " + motivo);
    }
}
