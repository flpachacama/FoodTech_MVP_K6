package com.foodtech.infrastructure.persistence.entity;

import com.foodtech.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoEntityTest {

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        PedidoEntity e = new PedidoEntity(1L, EstadoPedido.PENDIENTE, 2L, null, 3L, "Juan", 1.0, 2.0, 30, "[]");

        assertEquals(1L, e.getId());
        assertEquals(EstadoPedido.PENDIENTE, e.getEstado());
        assertEquals(2L, e.getRestauranteId());
        assertNull(e.getRepartidorId());
        assertEquals(3L, e.getClienteId());
        assertEquals("Juan", e.getClienteNombre());
        assertEquals(1.0, e.getClienteCoordenadasX());
        assertEquals(2.0, e.getClienteCoordenadasY());
        assertEquals(30, e.getTiempoEstimado());
        assertEquals("[]", e.getProductos());
    }

    @Test
    void noArgsConstructor_inicializaCamposNulos() {
        PedidoEntity e = new PedidoEntity();

        assertNull(e.getId());
        assertNull(e.getEstado());
        assertNull(e.getRestauranteId());
        assertNull(e.getClienteNombre());
    }

    @Test
    void builder_conTodosLosCampos_retornaEntidadCorrecta() {
        PedidoEntity e = PedidoEntity.builder()
                .id(10L)
                .estado(EstadoPedido.ASIGNADO)
                .restauranteId(5L)
                .repartidorId(7L)
                .clienteId(20L)
                .clienteNombre("María")
                .clienteCoordenadasX(4.6)
                .clienteCoordenadasY(-74.0)
                .tiempoEstimado(20)
                .productos("[{\"id\":1}]")
                .build();

        assertEquals(10L, e.getId());
        assertEquals(EstadoPedido.ASIGNADO, e.getEstado());
        assertEquals(5L, e.getRestauranteId());
        assertEquals(7L, e.getRepartidorId());
        assertEquals("María", e.getClienteNombre());
        assertEquals(20, e.getTiempoEstimado());
    }

    @Test
    void builder_sinCamposOpcionales_camposNulos() {
        PedidoEntity e = PedidoEntity.builder()
                .estado(EstadoPedido.PENDIENTE)
                .restauranteId(1L)
                .clienteId(2L)
                .clienteNombre("Test")
                .build();

        assertNull(e.getId());
        assertNull(e.getRepartidorId());
        assertNull(e.getClienteCoordenadasX());
        assertNull(e.getTiempoEstimado());
    }

    @Test
    void setters_modificanTodosLosCampos() {
        PedidoEntity e = new PedidoEntity();

        e.setId(99L);
        e.setEstado(EstadoPedido.CANCELADO);
        e.setRestauranteId(3L);
        e.setRepartidorId(8L);
        e.setClienteId(15L);
        e.setClienteNombre("Pedro");
        e.setClienteCoordenadasX(5.0);
        e.setClienteCoordenadasY(-73.0);
        e.setTiempoEstimado(45);
        e.setProductos("[]");

        assertEquals(99L, e.getId());
        assertEquals(EstadoPedido.CANCELADO, e.getEstado());
        assertEquals(3L, e.getRestauranteId());
        assertEquals(8L, e.getRepartidorId());
        assertEquals(15L, e.getClienteId());
        assertEquals("Pedro", e.getClienteNombre());
        assertEquals(5.0, e.getClienteCoordenadasX());
        assertEquals(-73.0, e.getClienteCoordenadasY());
        assertEquals(45, e.getTiempoEstimado());
        assertEquals("[]", e.getProductos());
    }
}

