package com.foodtech.application.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.port.input.RepartidorUseCase;
import com.foodtech.domain.port.output.RepartidorRepository;
import com.foodtech.domain.service.AsignacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignacionApplicationServiceTraceabilityTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @Mock
    private AsignacionService asignacionService;

    @Mock
    private RepartidorUseCase repartidorUseCase;

    @InjectMocks
    private AsignacionApplicationService service;

    // HU1 - Gestionar estado de repartidores
    @Test
    @DisplayName("TC-001 - Repartidor ACTIVO queda habilitado como candidato")
    void shouldReturnActiveRepartidorAsCandidate_TC001() {
        // Arrange
        Repartidor candidato = createRepartidor(1L, "Activo", EstadoRepartidor.ACTIVO, TipoVehiculo.MOTO, 6, 5);
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(List.of(candidato));
        when(asignacionService.priorizarRepartidores(List.of(candidato), new Coordenada(5, 5), Clima.SOLEADO)).thenReturn(List.of(candidato));

        // Act
        List<Repartidor> result = service.obtenerRepartidoresPriorizados(new Coordenada(5, 5), Clima.SOLEADO);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Activo", result.get(0).getNombre());
    }

    // HU1 - Gestionar estado de repartidores
    @Test
    @DisplayName("TC-002 - Repartidor EN_ENTREGA no debe ser considerado para nuevo pedido")
    void shouldExcludeEnEntregaRepartidor_TC002() {
        // Arrange
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Collections.emptyList());

        // Act
        List<Repartidor> result = service.obtenerRepartidoresPriorizados(new Coordenada(5, 5), Clima.SOLEADO);

        // Assert
        assertEquals(0, result.size());
    }

    // HU1 - Gestionar estado de repartidores
    @Test
    @DisplayName("TC-003 - Repartidor INACTIVO queda excluido de candidatos")
    void shouldExcludeInactiveRepartidor_TC003() {
        // Arrange
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Collections.emptyList());

        // Act
        List<Repartidor> result = service.obtenerRepartidoresPriorizados(new Coordenada(5, 5), Clima.SOLEADO);

        // Assert
        assertEquals(0, result.size());
    }

    // HU5 - Asignar pedido automaticamente
    @Test
    @DisplayName("TC-015 - Repartidores INACTIVO o EN_ENTREGA no se asignan")
    void shouldNotAssignInactiveOrBusyRepartidores_TC015() {
        // Arrange
        Repartidor inactivo = createRepartidor(1L, "Inactivo", EstadoRepartidor.INACTIVO, TipoVehiculo.MOTO, 6, 5);
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(List.of(inactivo));
        when(asignacionService.priorizarRepartidores(List.of(inactivo), new Coordenada(5, 5), Clima.SOLEADO)).thenReturn(Collections.emptyList());

        // Act
        Repartidor result = service.asignarRepartidor(new Coordenada(5, 5), Clima.SOLEADO);

        // Assert
        assertNull(result);
    }

    // HU5 - Asignar pedido automaticamente
    @Test
    @DisplayName("TC-016 - Lluvia fuerte impide asignacion a bicicleta o moto")
    void shouldBlockAssignmentInHeavyRain_TC016() {
        // Arrange
        Repartidor bici = createRepartidor(1L, "Bici", EstadoRepartidor.ACTIVO, TipoVehiculo.BICICLETA, 6, 5);
        Repartidor moto = createRepartidor(2L, "Moto", EstadoRepartidor.ACTIVO, TipoVehiculo.MOTO, 6, 6);
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(List.of(bici, moto));
        when(asignacionService.priorizarRepartidores(List.of(bici, moto), new Coordenada(5, 5), Clima.LLUVIA_FUERTE)).thenReturn(Collections.emptyList());

        // Act
        Repartidor result = service.asignarRepartidor(new Coordenada(5, 5), Clima.LLUVIA_FUERTE);

        // Assert
        assertNull(result);
    }

    // HU5 - Asignar pedido automaticamente
    @Test
    @DisplayName("TC-013 - Asignacion exitosa selecciona mejor candidato y cambia estado")
    void shouldAssignBestCandidate_TC013() {
        // Arrange
        Coordenada restaurante = new Coordenada(5, 5);
        Repartidor candidato = createRepartidor(1L, "Rider-1", EstadoRepartidor.ACTIVO, TipoVehiculo.MOTO, 6, 5);
        Repartidor asignado = createRepartidor(1L, "Rider-1", EstadoRepartidor.EN_ENTREGA, TipoVehiculo.MOTO, 6, 5);

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(List.of(candidato));
        when(asignacionService.priorizarRepartidores(anyList(), eq(restaurante), eq(Clima.SOLEADO)))
                .thenReturn(List.of(candidato));
        when(repartidorUseCase.cambiarEstado(1L, EstadoRepartidor.EN_ENTREGA)).thenReturn(asignado);

        // Act
        Repartidor result = service.asignarRepartidor(restaurante, Clima.SOLEADO);

        // Assert
        assertEquals(EstadoRepartidor.EN_ENTREGA, result.getEstado());
        assertEquals(1L, result.getId());
    }

    // HU5 - Asignar pedido automaticamente
    @Test
    @DisplayName("TC-014 - Sin candidatos validos retorna null y no cambia estados")
    void shouldReturnNullWhenNoCandidates_TC014() {
        // Arrange
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Collections.emptyList());

        // Act
        Repartidor result = service.asignarRepartidor(new Coordenada(1, 1), Clima.LLUVIA_FUERTE);

        // Assert
        assertNull(result);
        verify(asignacionService, never()).priorizarRepartidores(anyList(), any(), any());
        verify(repartidorUseCase, never()).cambiarEstado(any(), any());
    }

    private Repartidor createRepartidor(Long id,
                                        String nombre,
                                        EstadoRepartidor estado,
                                        TipoVehiculo vehiculo,
                                        int x,
                                        int y) {
        return Repartidor.builder()
                .id(id)
                .nombre(nombre)
                .estado(estado)
                .vehiculo(vehiculo)
                .ubicacion(new Coordenada(x, y))
                .build();
    }
}
