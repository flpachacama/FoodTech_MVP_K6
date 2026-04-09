package com.foodtech.domain.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    @Test
    void debeCrearPedidoYtenerCamposCorrectos() throws Exception {
        ProductoPedido producto = new ProductoPedido(1L, "Burger", null);
        List<ProductoPedido> productos = List.of(producto);
        Pedido pedido = new Pedido(10L, EstadoPedido.PENDIENTE, 2L, null, productos, 5L, "Juan", 1.0, 2.0, 30);

        Field idField = Pedido.class.getDeclaredField("id");
        idField.setAccessible(true);
        assertEquals(10L, idField.get(pedido));

        Field productosField = Pedido.class.getDeclaredField("productos");
        productosField.setAccessible(true);
        assertEquals(productos, productosField.get(pedido));
    }

    @Test
    void debeAceptarListaProductosVacia() throws Exception {
        Pedido pedido = new Pedido(11L, EstadoPedido.PENDIENTE, 2L, null, Collections.emptyList(), 5L, "Ana", null, null, 0);
        Field productosField = Pedido.class.getDeclaredField("productos");
        productosField.setAccessible(true);
        assertTrue(((List) productosField.get(pedido)).isEmpty());
    }

}
