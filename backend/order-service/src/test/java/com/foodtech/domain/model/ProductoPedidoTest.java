package com.foodtech.domain.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoPedidoTest {

    @Test
    void debeConstruirProductoYtenerCamposCorrectos() throws Exception {
        ProductoPedido p = new ProductoPedido(1L, "Pizza", BigDecimal.valueOf(125.50));

        Field idField = ProductoPedido.class.getDeclaredField("id");
        Field nombreField = ProductoPedido.class.getDeclaredField("nombre");
        Field precioField = ProductoPedido.class.getDeclaredField("precio");
        idField.setAccessible(true);
        nombreField.setAccessible(true);
        precioField.setAccessible(true);

        assertEquals(1L, idField.get(p));
        assertEquals("Pizza", nombreField.get(p));
        assertEquals(BigDecimal.valueOf(125.50), precioField.get(p));
    }

    @Test
    void debePermitirCamposNulos() throws Exception {
        ProductoPedido p = new ProductoPedido(null, null, null);
        Field nombreField = ProductoPedido.class.getDeclaredField("nombre");
        nombreField.setAccessible(true);
        assertNull(nombreField.get(p));
    }

}
