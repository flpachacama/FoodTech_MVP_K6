package com.foodtech.infrastructure.web.dto;

import com.foodtech.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliverOrderResponseDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        DeliverOrderResponseDto dto = DeliverOrderResponseDto.builder()
                .id(1L)
                .estado(EstadoPedido.ENTREGADO)
                .mensaje("Pedido entregado")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(EstadoPedido.ENTREGADO, dto.getEstado());
        assertEquals("Pedido entregado", dto.getMensaje());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto(2L, EstadoPedido.ENTREGADO, "Entregado OK");

        assertEquals(2L, dto.getId());
        assertEquals(EstadoPedido.ENTREGADO, dto.getEstado());
        assertEquals("Entregado OK", dto.getMensaje());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto();
        dto.setId(3L);
        dto.setEstado(EstadoPedido.ENTREGADO);
        dto.setMensaje("Entregado al cliente");

        assertEquals(3L, dto.getId());
        assertEquals(EstadoPedido.ENTREGADO, dto.getEstado());
        assertEquals("Entregado al cliente", dto.getMensaje());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg1");
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(2L, EstadoPedido.PENDIENTE, "msg2");

        assertNotEquals(a, b);
    }

    @Test
    void toString_contieneValoresClave() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto(5L, EstadoPedido.ENTREGADO, "entregado");

        String str = dto.toString();

        assertTrue(str.contains("5"));
        assertTrue(str.contains("ENTREGADO"));
        assertTrue(str.contains("entregado"));
    }

    @Test
    void equals_conNull_retornaFalse() {
        assertFalse(new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "m").equals(null));
    }

    @Test
    void equals_mismaReferencia_retornaTrue() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "m");
        assertTrue(dto.equals(dto));
    }

    @Test
    void equals_diferenteTipo_retornaFalse() {
        assertFalse(new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "m").equals(42));
    }

    @Test
    void equals_objetoVacioContraObjetoPoblado_retornaFalse() {
        DeliverOrderResponseDto empty = new DeliverOrderResponseDto();
        DeliverOrderResponseDto full = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");
        assertNotEquals(empty, full);
        assertNotEquals(full, empty);
    }

    @Test
    void equals_dosObjetosVacios_retornaTrue() {
        assertEquals(new DeliverOrderResponseDto(), new DeliverOrderResponseDto());
    }

    @Test
    void hashCode_objetoVacio_esConsistente() {
        assertEquals(new DeliverOrderResponseDto().hashCode(), new DeliverOrderResponseDto().hashCode());
    }

    @Test
    void builder_sinCampos_retornaObjetoNoNulo() {
        assertNotNull(DeliverOrderResponseDto.builder().build());
    }

    @Test
    void hashCode_objetoPoblado_esConsistente() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "ok");
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void equals_cuandoEstadoEsDistinto_retornaFalse() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(1L, EstadoPedido.PENDIENTE, "msg");
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoMensajeEsDistinto_retornaFalse() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg1");
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg2");
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoEstadoNullVsNonNull_retornaFalse() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, null, "msg");
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test
    void equals_cuandoMensajeNullVsNonNull_retornaFalse() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, null);
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }
}
