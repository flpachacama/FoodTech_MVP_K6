package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoMenuDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        ProductoMenuDto dto = ProductoMenuDto.builder()
                .id(1L)
                .nombre("Pizza Margherita")
                .precio(15.99)
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Pizza Margherita", dto.getNombre());
        assertEquals(15.99, dto.getPrecio());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        ProductoMenuDto dto = new ProductoMenuDto(2L, "Burger", 12.50);

        assertEquals(2L, dto.getId());
        assertEquals("Burger", dto.getNombre());
        assertEquals(12.50, dto.getPrecio());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        ProductoMenuDto dto = new ProductoMenuDto();
        dto.setId(3L);
        dto.setNombre("Sushi");
        dto.setPrecio(20.0);

        assertEquals(3L, dto.getId());
        assertEquals("Sushi", dto.getNombre());
        assertEquals(20.0, dto.getPrecio());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        ProductoMenuDto a = new ProductoMenuDto(1L, "Taco", 8.0);
        ProductoMenuDto b = new ProductoMenuDto(1L, "Taco", 8.0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        ProductoMenuDto a = new ProductoMenuDto(1L, "Taco", 8.0);
        ProductoMenuDto b = new ProductoMenuDto(2L, "Burrito", 10.0);

        assertNotEquals(a, b);
    }

    @Test
    void equals_conNull_retornaFalse() {
        assertFalse(new ProductoMenuDto(1L, "P", 5.0).equals(null));
    }

    @Test
    void equals_mismaReferencia_retornaTrue() {
        ProductoMenuDto dto = new ProductoMenuDto(1L, "P", 5.0);
        assertTrue(dto.equals(dto));
    }

    @Test
    void equals_diferenteTipo_retornaFalse() {
        assertFalse(new ProductoMenuDto(1L, "P", 5.0).equals(42));
    }

    @Test
    void equals_objetoVacioContraObjetoPoblado_retornaFalse() {
        ProductoMenuDto empty = new ProductoMenuDto();
        ProductoMenuDto full = new ProductoMenuDto(1L, "Pizza", 10.0);
        assertNotEquals(empty, full);
        assertNotEquals(full, empty);
    }

    @Test
    void equals_dosObjetosVacios_retornaTrue() {
        assertEquals(new ProductoMenuDto(), new ProductoMenuDto());
    }

    @Test
    void hashCode_objetoVacio_esConsistente() {
        assertEquals(new ProductoMenuDto().hashCode(), new ProductoMenuDto().hashCode());
    }

    @Test
    void builder_sinCampos_retornaObjetoNoNulo() {
        assertNotNull(ProductoMenuDto.builder().build());
    }

    @Test
    void hashCode_objetoPoblado_esConsistente() {
        ProductoMenuDto dto = new ProductoMenuDto(1L, "Pizza", 15.0);
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void equals_cuandoNombreEsDistinto_retornaFalse() {
        ProductoMenuDto a = new ProductoMenuDto(1L, "nombre1", 5.0);
        ProductoMenuDto b = new ProductoMenuDto(1L, "nombre2", 5.0);
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoPrecioEsDistinto_retornaFalse() {
        ProductoMenuDto a = new ProductoMenuDto(1L, "nombre", 5.0);
        ProductoMenuDto b = new ProductoMenuDto(1L, "nombre", 10.0);
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoNombreNullVsNonNull_retornaFalse() {
        ProductoMenuDto a = new ProductoMenuDto(1L, null, 5.0);
        ProductoMenuDto b = new ProductoMenuDto(1L, "nombre", 5.0);
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test
    void equals_cuandoPrecioNullVsNonNull_retornaFalse() {
        ProductoMenuDto a = new ProductoMenuDto(1L, "nombre", null);
        ProductoMenuDto b = new ProductoMenuDto(1L, "nombre", 5.0);
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test
    void toString_contieneValoresClave() {
        ProductoMenuDto dto = new ProductoMenuDto(5L, "Empanada", 3.5);

        String str = dto.toString();

        assertTrue(str.contains("5"));
        assertTrue(str.contains("Empanada"));
        assertTrue(str.contains("3.5"));
    }
}
