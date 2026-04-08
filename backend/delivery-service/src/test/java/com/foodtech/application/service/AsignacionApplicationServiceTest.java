package com.foodtech.application.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.port.output.RepartidorRepository;
import com.foodtech.domain.service.AsignacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionApplicationServiceTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @Mock
    private AsignacionService asignacionService;

    @Mock
    private com.foodtech.domain.port.input.RepartidorUseCase repartidorUseCase;

    @InjectMocks
    private AsignacionApplicationService applicationService;

    @Test
    void obtenerRepartidoresPriorizados_NoActivos_returnsEmpty() {
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO))
            .thenReturn(Collections.emptyList());

        List<Repartidor> result = applicationService.obtenerRepartidoresPriorizados(
            new Coordenada(0, 0), 
            Clima.SOLEADO
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repartidorRepository, times(1)).findByEstado(EstadoRepartidor.ACTIVO);
        verify(asignacionService, never()).priorizarRepartidores(any(), any(), any());
    }

    @Test
    void obtenerRepartidoresPriorizados_ConActivos_delegaAlServicioDeDominio() {
        Repartidor r1 = Repartidor.builder()
            .id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(10,10)).build();
        Repartidor r2 = Repartidor.builder()
            .id(2L).nombre("R2").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(20,20)).build();
        
        List<Repartidor> activos = Arrays.asList(r1, r2);
        List<Repartidor> priorizados = List.of(r1);
        
        Coordenada restaurante = new Coordenada(5, 5);
        Clima clima = Clima.LLUVIA_SUAVE;
        
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(activos);
        when(asignacionService.priorizarRepartidores(activos, restaurante, clima))
            .thenReturn(priorizados);

        List<Repartidor> result = applicationService.obtenerRepartidoresPriorizados(restaurante, clima);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("R1", result.get(0).getNombre());
        
        verify(repartidorRepository, times(1)).findByEstado(EstadoRepartidor.ACTIVO);
        verify(asignacionService, times(1)).priorizarRepartidores(activos, restaurante, clima);
    }

    @Test
    void obtenerRepartidoresPriorizados_RepositorioRetornaNull_returnsEmpty() {
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(null);

        List<Repartidor> result = applicationService.obtenerRepartidoresPriorizados(
            new Coordenada(50, 50), 
            Clima.SOLEADO
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(asignacionService, never()).priorizarRepartidores(any(), any(), any());
    }

    @Test
    void obtenerRepartidoresPriorizados_ConVariosActivos_delegaListaCompleta() {
        Repartidor r1 = Repartidor.builder()
            .id(1L).nombre("Bici").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,1)).build();
        Repartidor r2 = Repartidor.builder()
            .id(2L).nombre("Moto").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(5,5)).build();
        Repartidor r3 = Repartidor.builder()
            .id(3L).nombre("Auto").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(20,20)).build();
        
        List<Repartidor> activos = Arrays.asList(r1, r2, r3);
        List<Repartidor> priorizados = Arrays.asList(r2, r3);
        
        Coordenada restaurante = new Coordenada(0, 0);
        Clima clima = Clima.LLUVIA_FUERTE;
        
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(activos);
        when(asignacionService.priorizarRepartidores(activos, restaurante, clima))
            .thenReturn(priorizados);

        List<Repartidor> result = applicationService.obtenerRepartidoresPriorizados(restaurante, clima);

        assertEquals(2, result.size());
        verify(repartidorRepository).findByEstado(EstadoRepartidor.ACTIVO);
        verify(asignacionService).priorizarRepartidores(
            eq(activos), 
            eq(restaurante), 
            eq(clima)
        );
    }

    @Test
    void obtenerRepartidoresPriorizados_ClimaNull_funcionaCorrectamente() {
        Repartidor r1 = Repartidor.builder()
            .id(1L).nombre("Test").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(10,10)).build();
        
        List<Repartidor> activos = List.of(r1);
        Coordenada restaurante = new Coordenada(5, 5);
        
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(activos);
        when(asignacionService.priorizarRepartidores(activos, restaurante, null))
            .thenReturn(activos);

        List<Repartidor> result = applicationService.obtenerRepartidoresPriorizados(restaurante, null);

        assertEquals(1, result.size());
        verify(asignacionService).priorizarRepartidores(activos, restaurante, null);
    }

    @Test
    void asignarRepartidor_NoActivos_returnsNull() {
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Collections.emptyList());

        Repartidor result = applicationService.asignarRepartidor(new Coordenada(0,0), Clima.SOLEADO);

        assertNull(result);
        verify(repartidorUseCase, never()).cambiarEstado(any(), any());
    }

    @Test
    void asignarRepartidor_PriorizadosVacio_returnsNull() {
        Repartidor r1 = Repartidor.builder()
            .id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(10,10)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(List.of(r1));
        when(asignacionService.priorizarRepartidores(anyList(), any(), any())).thenReturn(Collections.emptyList());

        Repartidor result = applicationService.asignarRepartidor(new Coordenada(5,5), Clima.SOLEADO);

        assertNull(result);
        verify(repartidorUseCase, never()).cambiarEstado(any(), any());
    }

    @Test
    void asignarRepartidor_ConCandidato_llamaCambiarEstadoYRetornaActualizado() {
        Repartidor candidato = Repartidor.builder()
            .id(10L).nombre("C").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(2,2)).build();

        Repartidor actualizado = Repartidor.builder()
            .id(10L).nombre("C").estado(EstadoRepartidor.EN_ENTREGA)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(2,2)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(List.of(candidato));
        when(asignacionService.priorizarRepartidores(anyList(), any(), any())).thenReturn(List.of(candidato));
        when(repartidorUseCase.cambiarEstado(eq(10L), eq(EstadoRepartidor.EN_ENTREGA))).thenReturn(actualizado);

        Repartidor result = applicationService.asignarRepartidor(new Coordenada(1,1), Clima.SOLEADO);

        assertNotNull(result);
        assertEquals(EstadoRepartidor.EN_ENTREGA, result.getEstado());
        verify(repartidorUseCase, times(1)).cambiarEstado(eq(10L), eq(EstadoRepartidor.EN_ENTREGA));
    }

    @Test
    void procesarEventoRepartidor_cuandoEventoValido_delegaAlUseCase() {
        Repartidor actualizado = Repartidor.builder()
            .id(7L).nombre("Eva").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(3, 3)).build();

        when(repartidorUseCase.cambiarEstado(7L, EstadoRepartidor.ACTIVO)).thenReturn(actualizado);

        Repartidor result = applicationService.procesarEventoRepartidor(7L, "ENTREGADO");

        assertNotNull(result);
        assertEquals(EstadoRepartidor.ACTIVO, result.getEstado());
        verify(repartidorUseCase).cambiarEstado(7L, EstadoRepartidor.ACTIVO);
    }

    @Test
    void procesarEventoRepartidor_cuandoEventoInvalido_lanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> applicationService.procesarEventoRepartidor(1L, "INVALIDO"));
    }

    @Test
    void getAllRepartidores_retornaListaDeDtos() {
        Repartidor r = Repartidor.builder()
            .id(1L).nombre("Carlos").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(0, 0)).build();

        when(repartidorRepository.findAll()).thenReturn(List.of(r));

        List<com.foodtech.infrastructure.web.dto.RepartidorListResponseDto> result =
            applicationService.getAllRepartidores();

        assertEquals(1, result.size());
        assertEquals("Carlos", result.get(0).getNombre());
        assertEquals("ACTIVO", result.get(0).getEstado());
    }

    @Test
    void getRepartidorById_cuandoExiste_retornaOptionalConDto() {
        Repartidor r = Repartidor.builder()
            .id(2L).nombre("Lucia").estado(EstadoRepartidor.EN_ENTREGA)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(5, 5)).build();

        when(repartidorRepository.findById(2L)).thenReturn(java.util.Optional.of(r));

        java.util.Optional<com.foodtech.infrastructure.web.dto.RepartidorListResponseDto> result =
            applicationService.getRepartidorById(2L);

        assertTrue(result.isPresent());
        assertEquals("Lucia", result.get().getNombre());
    }

    @Test
    void getRepartidorById_cuandoNoExiste_retornaOptionalVacio() {
        when(repartidorRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        java.util.Optional<com.foodtech.infrastructure.web.dto.RepartidorListResponseDto> result =
            applicationService.getRepartidorById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getAllRepartidores_cuandoCamposNulos_mapeaNulos() {
        Repartidor rSinCampos = Repartidor.builder()
            .id(5L).nombre("Sin datos").estado(null).vehiculo(null).ubicacion(null).build();

        when(repartidorRepository.findAll()).thenReturn(List.of(rSinCampos));

        List<com.foodtech.infrastructure.web.dto.RepartidorListResponseDto> result =
            applicationService.getAllRepartidores();

        assertEquals(1, result.size());
        assertNull(result.get(0).getEstado());
        assertNull(result.get(0).getVehiculo());
        assertNull(result.get(0).getUbicacionX());
        assertNull(result.get(0).getUbicacionY());
    }
}
