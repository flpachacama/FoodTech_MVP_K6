package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductoPedidoDtoTest {

    private ProductoPedidoDto buildSample() {
        return ProductoPedidoDto.builder()
                .id(1L)
                .nombre("Hamburguesa")
                .precio(BigDecimal.valueOf(8.50))
                .build();
    }

    @Test
    void builder_conTodosLosCampos_gettersRetornanValoresCorrectos() {
        ProductoPedidoDto dto = buildSample();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNombre()).isEqualTo("Hamburguesa");
        assertThat(dto.getPrecio()).isEqualByComparingTo(BigDecimal.valueOf(8.50));
    }

    @Test
    void allArgsConstructor_conTodosLosCampos_gettersRetornanValoresCorrectos() {
        ProductoPedidoDto dto = new ProductoPedidoDto(2L, "Pizza", BigDecimal.valueOf(12.00));

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getNombre()).isEqualTo("Pizza");
        assertThat(dto.getPrecio()).isEqualByComparingTo(BigDecimal.valueOf(12.00));
    }

    @Test
    void setters_modificanCamposCorrectamente() {
        ProductoPedidoDto dto = new ProductoPedidoDto();

        dto.setId(3L);
        dto.setNombre("Ensalada");
        dto.setPrecio(BigDecimal.valueOf(5.00));

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getNombre()).isEqualTo("Ensalada");
        assertThat(dto.getPrecio()).isEqualByComparingTo(BigDecimal.valueOf(5.00));
    }

    @Test
    void equals_dosObjetosConMismosCampos_retornaTrue() {
        ProductoPedidoDto a = buildSample();
        ProductoPedidoDto b = buildSample();

        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_objetosDistintos_retornaFalse() {
        ProductoPedidoDto a = buildSample();
        ProductoPedidoDto b = buildSample();
        b.setNombre("Otro producto");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_noEsNuloYContieneId() {
        ProductoPedidoDto dto = buildSample();

        assertThat(dto.toString()).isNotNull().contains("1");
    }

    @Test
    void equals_conNull_retornaFalse() {
        assertThat(buildSample().equals(null)).isFalse();
    }

    @Test
    void equals_mismaReferencia_retornaTrue() {
        ProductoPedidoDto dto = buildSample();
        assertThat(dto.equals(dto)).isTrue();
    }

    @Test
    void equals_diferenteTipo_retornaFalse() {
        assertThat(buildSample().equals("distinto")).isFalse();
    }

    @Test
    void equals_objetoVacioContraObjetoPoblado_retornaFalse() {
        ProductoPedidoDto empty = new ProductoPedidoDto();
        ProductoPedidoDto full = buildSample();
        assertThat(empty.equals(full)).isFalse();
        assertThat(full.equals(empty)).isFalse();
    }

    @Test
    void equals_dosObjetosVacios_retornaTrue() {
        assertThat(new ProductoPedidoDto().equals(new ProductoPedidoDto())).isTrue();
    }

    @Test
    void hashCode_objetoVacio_noLanzaExcepcion() {
        assertThat(new ProductoPedidoDto().hashCode()).isEqualTo(new ProductoPedidoDto().hashCode());
    }

    @Test
    void builder_sinCampos_retornaObjetoNoNulo() {
        assertThat(ProductoPedidoDto.builder().build()).isNotNull();
    }

    @Test
    void hashCode_objetoPoblado_esConsistente() {
        ProductoPedidoDto dto = buildSample();
        assertThat(dto.hashCode()).isEqualTo(dto.hashCode());
    }

    @Test
    void equals_cuandoNombreEsDistinto_retornaFalse() {
        ProductoPedidoDto a = new ProductoPedidoDto(1L, "nombre1", BigDecimal.TEN);
        ProductoPedidoDto b = new ProductoPedidoDto(1L, "nombre2", BigDecimal.TEN);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoPrecioEsDistinto_retornaFalse() {
        ProductoPedidoDto a = new ProductoPedidoDto(1L, "nombre", BigDecimal.ONE);
        ProductoPedidoDto b = new ProductoPedidoDto(1L, "nombre", BigDecimal.TEN);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_cuandoNombreNullVsNonNull_retornaFalse() {
        ProductoPedidoDto a = new ProductoPedidoDto(1L, null, BigDecimal.TEN);
        ProductoPedidoDto b = new ProductoPedidoDto(1L, "nombre", BigDecimal.TEN);
        assertThat(a).isNotEqualTo(b);
        assertThat(b).isNotEqualTo(a);
    }

    @Test
    void equals_cuandoPrecioNullVsNonNull_retornaFalse() {
        ProductoPedidoDto a = new ProductoPedidoDto(1L, "nombre", null);
        ProductoPedidoDto b = new ProductoPedidoDto(1L, "nombre", BigDecimal.TEN);
        assertThat(a).isNotEqualTo(b);
        assertThat(b).isNotEqualTo(a);
    }
}
