package com.foodtech.infrastructure.web.dto;

import com.foodtech.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancelOrderResponseDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        CancelOrderResponseDto dto = CancelOrderResponseDto.builder()
                .id(1L)
                .estado(EstadoPedido.CANCELADO)
                .mensaje("Pedido cancelado")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(EstadoPedido.CANCELADO, dto.getEstado());
        assertEquals("Pedido cancelado", dto.getMensaje());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto(2L, EstadoPedido.CANCELADO, "Cancelado OK");

        assertEquals(2L, dto.getId());
        assertEquals(EstadoPedido.CANCELADO, dto.getEstado());
        assertEquals("Cancelado OK", dto.getMensaje());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto();
        dto.setId(3L);
        dto.setEstado(EstadoPedido.CANCELADO);
        dto.setMensaje("Cancelado por customer");

        assertEquals(3L, dto.getId());
        assertEquals(EstadoPedido.CANCELADO, dto.getEstado());
        assertEquals("Cancelado por customer", dto.getMensaje());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");
        CancelOrderResponseDto b = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg1");
        CancelOrderResponseDto b = new CancelOrderResponseDto(2L, EstadoPedido.PENDIENTE, "msg2");

        assertNotEquals(a, b);
    }

    @Test
    void toString_contieneValoresClave() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "test");

        String str = dto.toString();

        assertTrue(str.contains("1"));
        assertTrue(str.contains("CANCELADO"));
        assertTrue(str.contains("test"));
    }

    @Test
    void equals_conNull_retornaFalse() {
        assertFalse(new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "m").equals(null));
    }

    @Test
    void equals_mismaReferencia_retornaTrue() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "m");
        assertTrue(dto.equals(dto));
    }

    @Test
    void equals_diferenteTipo_retornaFalse() {
        assertFalse(new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "m").equals(42));
    }

    @Test
    void equals_objetoVacioContraObjetoPoblado_retornaFalse() {
        CancelOrderResponseDto empty = new CancelOrderResponseDto();
        CancelOrderResponseDto full = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");
        assertNotEquals(empty, full);
        assertNotEquals(full, empty);
    }

    @Test
    void equals_dosObjetosVacios_retornaTrue() {
        assertEquals(new CancelOrderResponseDto(), new CancelOrderResponseDto());
    }

    @Test
    void hashCode_objetoVacio_esConsistente() {
        assertEquals(new CancelOrderResponseDto().hashCode(), new CancelOrderResponseDto().hashCode());
    }

    @Test
    void builder_sinCampos_retornaObjetoNoNulo() {
        assertNotNull(CancelOrderResponseDto.builder().build());
    }

    @Test
    void hashCode_objetoPoblado_esConsistente() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void equals_cuandoEstadoEsDistinto_retornaFalse() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");
        CancelOrderResponseDto b = new CancelOrderResponseDto(1L, EstadoPedido.PENDIENTE, "msg");
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoMensajeEsDistinto_retornaFalse() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg1");
        CancelOrderResponseDto b = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg2");
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoEstadoNullVsNonNull_retornaFalse() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, null, "msg");
        CancelOrderResponseDto b = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test
    void equals_cuandoMensajeNullVsNonNull_retornaFalse() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, null);
        CancelOrderResponseDto b = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }
}
