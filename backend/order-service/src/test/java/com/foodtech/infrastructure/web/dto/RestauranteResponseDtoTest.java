package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestauranteResponseDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        ProductoMenuDto producto = ProductoMenuDto.builder().id(1L).nombre("Pizza").precio(15.0).build();
        RestauranteResponseDto dto = RestauranteResponseDto.builder()
                .id(1L)
                .nombre("La Parrilla")
                .coordenadaX(4.6)
                .coordenadaY(-74.0)
                .menu(List.of(producto))
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("La Parrilla", dto.getNombre());
        assertEquals(4.6, dto.getCoordenadaX());
        assertEquals(-74.0, dto.getCoordenadaY());
        assertEquals(1, dto.getMenu().size());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        RestauranteResponseDto dto = new RestauranteResponseDto(2L, "Sushi House", 5.0, -73.0, List.of());

        assertEquals(2L, dto.getId());
        assertEquals("Sushi House", dto.getNombre());
        assertTrue(dto.getMenu().isEmpty());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        RestauranteResponseDto dto = new RestauranteResponseDto();
        dto.setId(3L);
        dto.setNombre("Burger");
        dto.setCoordenadaX(1.0);
        dto.setCoordenadaY(2.0);
        dto.setMenu(List.of());

        assertEquals(3L, dto.getId());
        assertEquals("Burger", dto.getNombre());
        assertEquals(1.0, dto.getCoordenadaX());
        assertEquals(2.0, dto.getCoordenadaY());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest1", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(2L, "Rest2", 3.0, 4.0, null);

        assertNotEquals(a, b);
    }

    @Test
    void toString_contieneValoresClave() {
        RestauranteResponseDto dto = new RestauranteResponseDto(10L, "Italiano", 5.5, -74.5, List.of());

        String str = dto.toString();

        assertTrue(str.contains("10"));
        assertTrue(str.contains("Italiano"));
    }

    @Test
    void equals_conNull_retornaFalse() {
        assertFalse(new RestauranteResponseDto(1L, "R", 1.0, 2.0, List.of()).equals(null));
    }

    @Test
    void equals_mismaReferencia_retornaTrue() {
        RestauranteResponseDto dto = new RestauranteResponseDto(1L, "R", 1.0, 2.0, List.of());
        assertTrue(dto.equals(dto));
    }

    @Test
    void equals_diferenteTipo_retornaFalse() {
        assertFalse(new RestauranteResponseDto(1L, "R", 1.0, 2.0, List.of()).equals(42));
    }

    @Test
    void equals_objetoVacioContraObjetoPoblado_retornaFalse() {
        RestauranteResponseDto empty = new RestauranteResponseDto();
        RestauranteResponseDto full = new RestauranteResponseDto(1L, "R", 1.0, 2.0, List.of());
        assertNotEquals(empty, full);
        assertNotEquals(full, empty);
    }

    @Test
    void equals_dosObjetosVacios_retornaTrue() {
        assertEquals(new RestauranteResponseDto(), new RestauranteResponseDto());
    }

    @Test
    void hashCode_objetoVacio_esConsistente() {
        assertEquals(new RestauranteResponseDto().hashCode(), new RestauranteResponseDto().hashCode());
    }

    @Test
    void builder_sinCampos_retornaObjetoNoNulo() {
        assertNotNull(RestauranteResponseDto.builder().build());
    }

    @Test
    void hashCode_objetoPoblado_esConsistente() {
        RestauranteResponseDto dto = new RestauranteResponseDto(1L, "Rest", 4.6, -74.0, List.of());
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void equals_cuandoNombreEsDistinto_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Nombre1", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Nombre2", 1.0, 2.0, List.of());
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoCoordenadaXEsDistinta_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 9.0, 2.0, List.of());
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoCoordenadaYEsDistinta_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 9.0, List.of());
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoMenuEsDistinto_retornaFalse() {
        ProductoMenuDto prod = new ProductoMenuDto(1L, "Pizza", 15.0);
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of(prod));
        assertNotEquals(a, b);
    }

    @Test
    void equals_cuandoNombreNullVsNonNull_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, null, 1.0, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test
    void equals_cuandoCoordenadaXNullVsNonNull_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", null, 2.0, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test
    void equals_cuandoCoordenadaYNullVsNonNull_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", 1.0, null, List.of());
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }

    @Test
    void equals_cuandoMenuNullVsNonNull_retornaFalse() {
        RestauranteResponseDto a = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, null);
        RestauranteResponseDto b = new RestauranteResponseDto(1L, "Rest", 1.0, 2.0, List.of());
        assertNotEquals(a, b);
        assertNotEquals(b, a);
    }
}
