package com.foodtech.domain.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsignacionServiceTraceabilityTest {

    private final AsignacionService service = new AsignacionService();

    // HU2 - Filtrar repartidores por cercania
    @Test
    @DisplayName("TC-004 - Lista ordenada ascendente por distancia")
    void shouldSortRepartidoresByDistance_TC004() {
        // Arrange
        Coordenada restaurante = new Coordenada(5, 5);
        Repartidor near = repartidor(1L, "Near", TipoVehiculo.MOTO, 6, 5);
        Repartidor middle = repartidor(2L, "Middle", TipoVehiculo.MOTO, 8, 8);
        Repartidor far = repartidor(3L, "Far", TipoVehiculo.MOTO, 10, 10);

        // Act
        List<Repartidor> result = service.priorizarRepartidores(Arrays.asList(far, middle, near), restaurante, Clima.SOLEADO);

        // Assert
        assertEquals(3, result.size());
        assertEquals("Near", result.get(0).getNombre());
        assertEquals("Middle", result.get(1).getNombre());
        assertEquals("Far", result.get(2).getNombre());
    }

    // HU2 - Filtrar repartidores por cercania
    @Test
    @DisplayName("TC-005 - Sin repartidores activos retorna lista vacia")
    void shouldReturnEmptyWhenNoActiveRepartidores_TC005() {
        // Arrange
        // Act
        List<Repartidor> result = service.priorizarRepartidores(Collections.emptyList(), new Coordenada(5, 5), Clima.SOLEADO);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // HU2 - Filtrar repartidores por cercania
    @Test
    @DisplayName("TC-006 - Repartidor con coordenadas incompletas se excluye del ordenamiento")
    void shouldExcludeRepartidorWithIncompleteCoordinates_TC006() {
        // Arrange
        Coordenada restaurante = new Coordenada(5, 5);
        Repartidor invalido = Repartidor.builder()
                .id(1L)
                .nombre("Invalido")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.MOTO)
                .ubicacion(null)
                .build();
        Repartidor valido = repartidor(2L, "Valido", TipoVehiculo.MOTO, 6, 5);

        // Act
        List<Repartidor> result = service.priorizarRepartidores(Arrays.asList(invalido, valido), restaurante, Clima.SOLEADO);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Valido", result.get(0).getNombre());
    }

    // HU3 - Aplicar restricciones por clima
    @Test
    @DisplayName("TC-007 - Lluvia fuerte excluye bicicleta y moto")
    void shouldAllowOnlyCarInHeavyRain_TC007() {
        // Arrange
        Coordenada restaurante = new Coordenada(5, 5);
        Repartidor bici = repartidor(1L, "Bici", TipoVehiculo.BICICLETA, 6, 5);
        Repartidor moto = repartidor(2L, "Moto", TipoVehiculo.MOTO, 6, 6);
        Repartidor auto = repartidor(3L, "Auto", TipoVehiculo.AUTO, 5, 6);

        // Act
        List<Repartidor> result = service.priorizarRepartidores(Arrays.asList(bici, moto, auto), restaurante, Clima.LLUVIA_FUERTE);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Auto", result.get(0).getNombre());
    }

    // HU3 - Aplicar restricciones por clima
    @Test
    @DisplayName("TC-008 - Lluvia suave excluye bicicleta y permite moto/autos")
    void shouldExcludeBicycleInLightRain_TC008() {
        // Arrange
        Coordenada restaurante = new Coordenada(5, 5);
        Repartidor bici = repartidor(1L, "Bici", TipoVehiculo.BICICLETA, 6, 5);
        Repartidor moto = repartidor(2L, "Moto", TipoVehiculo.MOTO, 5, 6);
        Repartidor auto = repartidor(3L, "Auto", TipoVehiculo.AUTO, 20, 20);

        // Act
        List<Repartidor> result = service.priorizarRepartidores(Arrays.asList(bici, moto, auto), restaurante, Clima.LLUVIA_SUAVE);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Moto", result.get(0).getNombre());
        assertEquals("Auto", result.get(1).getNombre());
    }

    // HU4 - Calcular prioridad de repartidores
    @Test
    @DisplayName("TC-010 - La moto obtiene mayor prioridad frente a bicicleta mas lenta")
    void shouldPrioritizeMotoOverBike_TC010() {
        // Arrange
        Coordenada restaurante = new Coordenada(0, 0);
        Repartidor bici = repartidor(1L, "Bici", TipoVehiculo.BICICLETA, 10, 0);
        Repartidor moto = repartidor(2L, "Moto", TipoVehiculo.MOTO, 15, 0);

        // Act
        List<Repartidor> result = service.priorizarRepartidores(Arrays.asList(bici, moto), restaurante, Clima.SOLEADO);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Moto", result.get(0).getNombre());
    }

    // HU4 - Calcular prioridad de repartidores
    @Test
    @DisplayName("TC-011 - Entre dos motos, gana la mas cercana")
    void shouldPrioritizeClosestMoto_TC011() {
        // Arrange
        Coordenada restaurante = new Coordenada(0, 0);
        Repartidor near = repartidor(1L, "Near", TipoVehiculo.MOTO, 10, 0);
        Repartidor far = repartidor(2L, "Far", TipoVehiculo.MOTO, 20, 0);

        // Act
        List<Repartidor> result = service.priorizarRepartidores(Arrays.asList(far, near), restaurante, Clima.SOLEADO);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Near", result.get(0).getNombre());
    }

    // HU4 - Calcular prioridad de repartidores
    @Test
    @DisplayName("TC-012 - Empate de ETA selecciona un candidato valido sin fallar")
    void shouldHandleEtaTieWithoutFailure_TC012() {
        // Arrange
        Coordenada restaurante = new Coordenada(0, 0);
        Repartidor a = repartidor(1L, "A", TipoVehiculo.MOTO, 8, 0);
        Repartidor b = repartidor(2L, "B", TipoVehiculo.MOTO, 8, 0);

        // Act
        List<Repartidor> result = service.priorizarRepartidores(Arrays.asList(a, b), restaurante, Clima.SOLEADO);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("A")));
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("B")));
    }

    private Repartidor repartidor(Long id, String nombre, TipoVehiculo vehiculo, int x, int y) {
        return Repartidor.builder()
                .id(id)
                .nombre(nombre)
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(vehiculo)
                .ubicacion(new Coordenada(x, y))
                .build();
    }
}
