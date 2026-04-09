package com.foodtech.infrastructure.web.controller;

import com.foodtech.application.service.RestauranteService;
import com.foodtech.domain.exception.RestauranteNotFoundException;
import com.foodtech.infrastructure.web.dto.ProductoMenuDto;
import com.foodtech.infrastructure.web.dto.RestauranteResponseDto;
import com.foodtech.infrastructure.web.exception.OrderExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestauranteController.class)
@Import(OrderExceptionHandler.class)
class RestauranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestauranteService restauranteService;

    @Test
    void getAllRestaurantes_retorna200ConLista() throws Exception {
        RestauranteResponseDto dto = RestauranteResponseDto.builder()
                .id(1L).nombre("La Parrilla").coordenadaX(4.6).coordenadaY(-74.0)
                .menu(List.of(ProductoMenuDto.builder().id(1L).nombre("Pizza").precio(15.0).build()))
                .build();
        when(restauranteService.getAllRestaurantes()).thenReturn(List.of(dto));

        mockMvc.perform(get("/restaurants").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("La Parrilla"));
    }

    @Test
    void getAllRestaurantes_conListaVacia_retorna200() throws Exception {
        when(restauranteService.getAllRestaurantes()).thenReturn(List.of());

        mockMvc.perform(get("/restaurants").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getRestauranteById_cuandoExiste_retorna200() throws Exception {
        RestauranteResponseDto dto = RestauranteResponseDto.builder()
                .id(5L).nombre("Sushi House").coordenadaX(5.0).coordenadaY(-73.0).menu(List.of()).build();
        when(restauranteService.getRestauranteById(5L)).thenReturn(dto);

        mockMvc.perform(get("/restaurants/5").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Sushi House"));
    }

    @Test
    void getRestauranteById_cuandoNoExiste_retorna404() throws Exception {
        when(restauranteService.getRestauranteById(99L))
                .thenThrow(new RestauranteNotFoundException(99L));

        mockMvc.perform(get("/restaurants/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
