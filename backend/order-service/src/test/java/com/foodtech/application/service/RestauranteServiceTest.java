package com.foodtech.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.domain.exception.RestauranteNotFoundException;
import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.infrastructure.persistence.entity.RestauranteEntity;
import com.foodtech.infrastructure.web.dto.ProductoMenuDto;
import com.foodtech.infrastructure.web.dto.RestauranteResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestauranteServiceTest {

    @Mock
    private RestauranteJpaRepository restauranteRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RestauranteService restauranteService;

    @Test
    void getAllRestaurantes_conDosEntidades_retornaLista() throws Exception {
        RestauranteEntity e1 = RestauranteEntity.builder()
                .id(1L).nombre("La Parrilla").coordenadaX(4.6).coordenadaY(-74.0).menu(null).build();
        RestauranteEntity e2 = RestauranteEntity.builder()
                .id(2L).nombre("Sushi House").coordenadaX(4.7).coordenadaY(-74.1).menu("").build();
        when(restauranteRepository.findAll()).thenReturn(List.of(e1, e2));

        List<RestauranteResponseDto> result = restauranteService.getAllRestaurantes();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("La Parrilla", result.get(0).getNombre());
    }

    @Test
    void getAllRestaurantes_conListaVacia_retornaListaVacia() {
        when(restauranteRepository.findAll()).thenReturn(List.of());

        List<RestauranteResponseDto> result = restauranteService.getAllRestaurantes();

        assertTrue(result.isEmpty());
        verify(restauranteRepository, times(1)).findAll();
    }

    @Test
    void getRestauranteById_cuandoExiste_retornaDto() throws Exception {
        RestauranteEntity entity = RestauranteEntity.builder()
                .id(10L).nombre("Burger King").coordenadaX(5.0).coordenadaY(-73.0).menu(null).build();
        when(restauranteRepository.findById(10L)).thenReturn(Optional.of(entity));

        RestauranteResponseDto result = restauranteService.getRestauranteById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Burger King", result.getNombre());
    }

    @Test
    void getRestauranteById_cuandoNoExiste_lanzaRestauranteNotFoundException() {
        when(restauranteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RestauranteNotFoundException.class,
                () -> restauranteService.getRestauranteById(99L));
    }

    @Test
    void getRestauranteById_cuandoMenuNull_retornaMenuVacio() {
        RestauranteEntity entity = RestauranteEntity.builder()
                .id(20L).nombre("Test").coordenadaX(1.0).coordenadaY(1.0).menu(null).build();
        when(restauranteRepository.findById(20L)).thenReturn(Optional.of(entity));

        RestauranteResponseDto result = restauranteService.getRestauranteById(20L);

        assertNotNull(result.getMenu());
        assertTrue(result.getMenu().isEmpty());
    }

    @Test
    void getRestauranteById_cuandoMenuBlank_retornaMenuVacio() {
        RestauranteEntity entity = RestauranteEntity.builder()
                .id(21L).nombre("Test").coordenadaX(1.0).coordenadaY(1.0).menu("   ").build();
        when(restauranteRepository.findById(21L)).thenReturn(Optional.of(entity));

        RestauranteResponseDto result = restauranteService.getRestauranteById(21L);

        assertNotNull(result.getMenu());
        assertTrue(result.getMenu().isEmpty());
    }

    @Test
    void getRestauranteById_cuandoMenuJsonValido_retornaProductos() throws Exception {
        String menuJson = "[{\"id\":1,\"nombre\":\"Pizza\",\"precio\":15.0}]";
        RestauranteEntity entity = RestauranteEntity.builder()
                .id(22L).nombre("Pizzeria").coordenadaX(1.0).coordenadaY(1.0).menu(menuJson).build();
        when(restauranteRepository.findById(22L)).thenReturn(Optional.of(entity));

        ProductoMenuDto producto = ProductoMenuDto.builder().id(1L).nombre("Pizza").precio(15.0).build();
        when(objectMapper.readValue(eq(menuJson), any(TypeReference.class))).thenReturn(List.of(producto));

        RestauranteResponseDto result = restauranteService.getRestauranteById(22L);

        assertEquals(1, result.getMenu().size());
        assertEquals("Pizza", result.getMenu().get(0).getNombre());
    }

    @Test
    void getRestauranteById_cuandoMenuJsonInvalido_retornaMenuVacio() throws Exception {
        String menuJson = "{invalid-json}";
        RestauranteEntity entity = RestauranteEntity.builder()
                .id(23L).nombre("Bad JSON").coordenadaX(1.0).coordenadaY(1.0).menu(menuJson).build();
        when(restauranteRepository.findById(23L)).thenReturn(Optional.of(entity));
        when(objectMapper.readValue(eq(menuJson), any(TypeReference.class)))
                .thenThrow(new RuntimeException("JSON parse error"));

        RestauranteResponseDto result = restauranteService.getRestauranteById(23L);

        assertNotNull(result.getMenu());
        assertTrue(result.getMenu().isEmpty());
    }
}
