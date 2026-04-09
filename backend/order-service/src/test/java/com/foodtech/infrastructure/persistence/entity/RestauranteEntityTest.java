package com.foodtech.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RestauranteEntityTest {

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        RestauranteEntity r = new RestauranteEntity(1L, "La Casa", 10.0, 20.0, "{\"menu\":[]}");

        assertEquals(1L, r.getId());
        assertEquals("La Casa", r.getNombre());
        assertEquals(10.0, r.getCoordenadaX());
        assertEquals(20.0, r.getCoordenadaY());
        assertEquals("{\"menu\":[]}", r.getMenu());
    }

    @Test
    void noArgsConstructor_inicializaCamposNulos() {
        RestauranteEntity r = new RestauranteEntity();

        assertNull(r.getId());
        assertNull(r.getNombre());
        assertNull(r.getCoordenadaX());
        assertNull(r.getMenu());
    }

    @Test
    void builder_conTodosLosCampos_retornaEntidadCorrecta() {
        RestauranteEntity r = RestauranteEntity.builder()
                .id(5L)
                .nombre("Sushi House")
                .coordenadaX(4.7)
                .coordenadaY(-74.1)
                .menu("[]") 
                .build();

        assertEquals(5L, r.getId());
        assertEquals("Sushi House", r.getNombre());
        assertEquals(4.7, r.getCoordenadaX());
        assertEquals(-74.1, r.getCoordenadaY());
        assertEquals("[]", r.getMenu());
    }

    @Test
    void builder_sinCamposOpcionales_camposNulos() {
        RestauranteEntity r = RestauranteEntity.builder()
                .nombre("Test")
                .build();

        assertNull(r.getId());
        assertNull(r.getCoordenadaX());
        assertNull(r.getMenu());
    }

    @Test
    void setters_modificanTodosLosCampos() {
        RestauranteEntity r = new RestauranteEntity();

        r.setId(10L);
        r.setNombre("Burger King");
        r.setCoordenadaX(5.0);
        r.setCoordenadaY(-73.0);
        r.setMenu("[{\"id\":1}]");

        assertEquals(10L, r.getId());
        assertEquals("Burger King", r.getNombre());
        assertEquals(5.0, r.getCoordenadaX());
        assertEquals(-73.0, r.getCoordenadaY());
        assertEquals("[{\"id\":1}]", r.getMenu());
    }
}

