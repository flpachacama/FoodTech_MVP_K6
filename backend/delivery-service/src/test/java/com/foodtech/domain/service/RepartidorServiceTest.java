package com.foodtech.domain.service;

import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.port.output.RepartidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepartidorServiceTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private RepartidorService repartidorService;

    @Test
    void debeRetornarRepartidorActualizado_cuandoRepartidorExiste() {
        Repartidor existente = Repartidor.builder()
                .id(1L)
                .nombre("Juan")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.MOTO)
                .ubicacion(new Coordenada(10, 10))
                .build();

        Repartidor actualizado = Repartidor.builder()
                .id(1L)
                .nombre("Juan")
                .estado(EstadoRepartidor.EN_ENTREGA)
                .vehiculo(TipoVehiculo.MOTO)
                .ubicacion(new Coordenada(10, 10))
                .build();

        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(repartidorRepository.save(any())).thenReturn(actualizado);

        Repartidor result = repartidorService.cambiarEstado(1L, EstadoRepartidor.EN_ENTREGA);

        assertNotNull(result);
        assertEquals(EstadoRepartidor.EN_ENTREGA, result.getEstado());

        ArgumentCaptor<Repartidor> captor = ArgumentCaptor.forClass(Repartidor.class);
        verify(repartidorRepository, times(1)).save(captor.capture());
        Repartidor saved = captor.getValue();

        assertEquals(1L, saved.getId());
        assertEquals("Juan", saved.getNombre());
        assertEquals(EstadoRepartidor.EN_ENTREGA, saved.getEstado());
    }

    @Test
    void debeLanzarRepartidorNotFoundException_cuandoRepartidorNoExiste() {
        when(repartidorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(com.foodtech.domain.exception.RepartidorNotFoundException.class, () ->
                repartidorService.cambiarEstado(99L, EstadoRepartidor.EN_ENTREGA)
        );

        verify(repartidorRepository, never()).save(any());
    }
}
