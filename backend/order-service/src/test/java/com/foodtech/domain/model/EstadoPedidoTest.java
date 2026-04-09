package com.foodtech.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class EstadoPedidoTest {

    @Test
    void debeContenerLosEstadosEsperados() {
        EstadoPedido[] esperado = new EstadoPedido[]{
                EstadoPedido.PENDIENTE,
                EstadoPedido.ASIGNADO,
                EstadoPedido.ENTREGADO,
                EstadoPedido.CANCELADO
        };

        assertArrayEquals(esperado, EstadoPedido.values());
    }

}
