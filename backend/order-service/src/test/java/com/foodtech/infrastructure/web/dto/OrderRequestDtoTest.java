package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRequestDtoTest {

    private OrderRequestDto buildSample() {
        return OrderRequestDto.builder()
                .restauranteId(1L)
                .restauranteX(10.0)
                .restauranteY(20.0)
                .clima("SOLEADO")
                .clienteId(5L)
                .clienteNombre("Ana García")
                .clienteCoordenadasX(3.0)
                .clienteCoordenadasY(4.0)
                .clienteTelefono("600000001")
                .productos(List.of(
                        ProductoPedidoDto.builder().id(1L).nombre("Burger").precio(BigDecimal.valueOf(8.50)).build()))
                .build();
    }

    @Test
    void builder_conTodosLosCampos_gettersRetornanValoresCorrectos() {
        OrderRequestDto dto = buildSample();

        assertThat(dto.getRestauranteId()).isEqualTo(1L);
        assertThat(dto.getRestauranteX()).isEqualTo(10.0);
        assertThat(dto.getRestauranteY()).isEqualTo(20.0);
        assertThat(dto.getClima()).isEqualTo("SOLEADO");
        assertThat(dto.getClienteId()).isEqualTo(5L);
        assertThat(dto.getClienteNombre()).isEqualTo("Ana García");
        assertThat(dto.getClienteCoordenadasX()).isEqualTo(3.0);
        assertThat(dto.getClienteCoordenadasY()).isEqualTo(4.0);
        assertThat(dto.getClienteTelefono()).isEqualTo("600000001");
        assertThat(dto.getProductos()).hasSize(1);
    }

    @Test
    void allArgsConstructor_conTodosLosCampos_gettersRetornanValoresCorrectos() {
        List<ProductoPedidoDto> productos = List.of(
                ProductoPedidoDto.builder().id(2L).nombre("Pizza").precio(BigDecimal.TEN).build());

        OrderRequestDto dto = new OrderRequestDto(2L, 5.0, 6.0, "LLUVIOSO",
                productos, 8L, "Luis", 1.0, 2.0, "611111111");

        assertThat(dto.getRestauranteId()).isEqualTo(2L);
        assertThat(dto.getClima()).isEqualTo("LLUVIOSO");
        assertThat(dto.getClienteNombre()).isEqualTo("Luis");
        assertThat(dto.getClienteTelefono()).isEqualTo("611111111");
        assertThat(dto.getProductos()).hasSize(1);
    }

    @Test
    void setters_modificanCamposCorrectamente() {
        OrderRequestDto dto = new OrderRequestDto();

        dto.setRestauranteId(3L);
        dto.setRestauranteX(7.0);
        dto.setRestauranteY(8.0);
        dto.setClima("NUBLADO");
        dto.setClienteId(9L);
        dto.setClienteNombre("Pedro");
        dto.setClienteCoordenadasX(5.0);
        dto.setClienteCoordenadasY(6.0);
        dto.setClienteTelefono("622222222");
        dto.setProductos(List.of());

        assertThat(dto.getRestauranteId()).isEqualTo(3L);
        assertThat(dto.getClima()).isEqualTo("NUBLADO");
        assertThat(dto.getClienteNombre()).isEqualTo("Pedro");
    }

    @Test
    void equals_dosObjetosConMismosCampos_retornaTrue() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample();

        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_objetosDistintos_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample();
        b.setClienteNombre("Otro nombre");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_noEsNuloYContieneRestauranteId() {
        OrderRequestDto dto = buildSample();

        assertThat(dto.toString()).isNotNull().contains("1");
    }

    @Test
    void equals_conNull_retornaFalse() {
        assertThat(buildSample().equals(null)).isFalse();
    }

    @Test
    void equals_mismaReferencia_retornaTrue() {
        OrderRequestDto dto = buildSample();
        assertThat(dto.equals(dto)).isTrue();
    }

    @Test
    void equals_diferenteTipo_retornaFalse() {
        assertThat(buildSample().equals("distinto")).isFalse();
    }

    @Test
    void equals_objetoVacioContraObjetoPoblado_retornaFalse() {
        OrderRequestDto empty = new OrderRequestDto();
        OrderRequestDto full = buildSample();
        assertThat(empty.equals(full)).isFalse();
        assertThat(full.equals(empty)).isFalse();
    }

    @Test
    void equals_dosObjetosVacios_retornaTrue() {
        assertThat(new OrderRequestDto().equals(new OrderRequestDto())).isTrue();
    }

    @Test
    void hashCode_objetoVacio_noLanzaExcepcion() {
        assertThat(new OrderRequestDto().hashCode()).isEqualTo(new OrderRequestDto().hashCode());
    }

    @Test
    void builder_sinCampos_retornaObjetoNoNulo() {
        assertThat(OrderRequestDto.builder().build()).isNotNull();
    }

    @Test
    void hashCode_objetoPoblado_esConsistente() {
        OrderRequestDto dto = buildSample();
        assertThat(dto.hashCode()).isEqualTo(dto.hashCode());
    }

    @Test
    void equals_cuandoRestauranteXEsDistinto_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample(); b.setRestauranteX(99.0);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoRestauranteYEsDistinto_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample(); b.setRestauranteY(99.0);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoClimaEsDistinto_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample(); b.setClima("LLUVIOSO");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoProductosDifieren_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample();
        b.setProductos(List.of());
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoClienteIdEsDistinto_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample(); b.setClienteId(99L);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoCoordXEsDistinta_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample(); b.setClienteCoordenadasX(99.0);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoCoordYEsDistinta_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample(); b.setClienteCoordenadasY(99.0);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoTelefonoEsDistinto_retornaFalse() {
        OrderRequestDto a = buildSample();
        OrderRequestDto b = buildSample(); b.setClienteTelefono("999");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoRestauranteIdNullVsNonNull_retornaFalse() {
        OrderRequestDto a = buildSample(); a.setRestauranteId(null);
        OrderRequestDto b = buildSample();
        assertThat(a.equals(b)).isFalse();
        assertThat(b.equals(a)).isFalse();
    }

    @Test
    void equals_cuandoClimaNull_vsNonNull_retornaFalse() {
        OrderRequestDto a = buildSample(); a.setClima(null);
        OrderRequestDto b = buildSample();
        assertThat(a.equals(b)).isFalse();
        assertThat(b.equals(a)).isFalse();
    }

    @Test
    void equals_cuandoClienteNombreNullVsNonNull_retornaFalse() {
        OrderRequestDto a = buildSample(); a.setClienteNombre(null);
        OrderRequestDto b = buildSample();
        assertThat(a.equals(b)).isFalse();
        assertThat(b.equals(a)).isFalse();
    }
}
