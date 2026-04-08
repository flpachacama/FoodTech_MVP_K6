package com.foodtech.domain.exception;

public class PedidoCancelException extends RuntimeException {

    public PedidoCancelException(Long pedidoId, String motivo) {
        super("No se puede cancelar el pedido " + pedidoId + ": " + motivo);
    }
}
