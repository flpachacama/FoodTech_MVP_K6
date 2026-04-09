package com.foodtech.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.domain.exception.RestauranteNotFoundException;
import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.infrastructure.persistence.entity.RestauranteEntity;
import com.foodtech.infrastructure.web.dto.ProductoMenuDto;
import com.foodtech.infrastructure.web.dto.RestauranteResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestauranteServiceTraceabilityTest {

    @Mock
    private RestauranteJpaRepository restauranteRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // HU10 - Visualizar y seleccionar restaurante
    @Test
    @DisplayName("TC-031 - Se visualizan todos los restaurantes con nombre y ubicacion")
    void shouldReturnAllRestaurants_TC031() {
        // Arrange
        RestauranteService service = new RestauranteService(restauranteRepository, objectMapper);
        when(restauranteRepository.findAll()).thenReturn(List.of(
                restaurante(1L, "La Parrilla", 10.0, 20.0, "[]"),
                restaurante(2L, "Sushi Go", 30.0, 40.0, "[]")
        ));

        // Act
        List<RestauranteResponseDto> result = service.getAllRestaurantes();

        // Assert
        assertEquals(2, result.size());
        assertEquals("La Parrilla", result.get(0).getNombre());
        assertEquals(10, result.get(0).getCoordenadaX());
        assertEquals(20, result.get(0).getCoordenadaY());
    }

    // HU10 - Visualizar y seleccionar restaurante
    @Test
    @DisplayName("TC-032 - Restaurante seleccionado muestra su menu con productos y precios")
    void shouldReturnRestaurantMenu_TC032() {
        // Arrange
        RestauranteService service = new RestauranteService(restauranteRepository, objectMapper);
        String menuJson = "[{\"id\":1,\"nombre\":\"Hamburguesa\",\"precio\":8.5},{\"id\":2,\"nombre\":\"Papas\",\"precio\":4.0}]";
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante(1L, "La Parrilla", 10.0, 20.0, menuJson)));

        // Act
        RestauranteResponseDto result = service.getRestauranteById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("La Parrilla", result.getNombre());
        assertEquals(2, result.getMenu().size());
        assertEquals("Hamburguesa", result.getMenu().get(0).getNombre());
        assertEquals(8.5, result.getMenu().get(0).getPrecio());
    }

    // HU10 - Visualizar y seleccionar restaurante
    @Test
    @DisplayName("TC-033 - Si no hay restaurantes, retorna lista vacia")
    void shouldReturnEmptyWhenNoRestaurants_TC033() {
        // Arrange
        RestauranteService service = new RestauranteService(restauranteRepository, objectMapper);
        when(restauranteRepository.findAll()).thenReturn(List.of());

        // Act
        List<RestauranteResponseDto> result = service.getAllRestaurantes();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Caso complementario - Restaurante inexistente lanza exception de negocio")
    void shouldThrowWhenRestaurantNotFound() {
        // Arrange
        RestauranteService service = new RestauranteService(restauranteRepository, objectMapper);
        when(restauranteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RestauranteNotFoundException.class, () -> service.getRestauranteById(99L));
    }

    private RestauranteEntity restaurante(Long id, String nombre, Double x, Double y, String menu) {
        return RestauranteEntity.builder()
                .id(id)
                .nombre(nombre)
                .coordenadaX(x)
                .coordenadaY(y)
                .menu(menu)
                .build();
    }
}
