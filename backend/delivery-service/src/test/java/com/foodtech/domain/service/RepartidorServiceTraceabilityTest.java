package com.foodtech.domain.service;

import com.foodtech.domain.exception.RepartidorNotFoundException;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.port.output.RepartidorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepartidorServiceTraceabilityTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private RepartidorService service;

    // HU6 - Actualizar estado del repartidor
    @Test
    @DisplayName("TC-017 - Repartidor ACTIVO asignado a pedido pasa a EN_ENTREGA")
    void shouldChangeToEnEntregaWhenAssigned_TC017() {
        // Arrange
        Repartidor existente = repartidor(1L, EstadoRepartidor.ACTIVO);
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(repartidorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Repartidor result = service.cambiarEstado(1L, EstadoRepartidor.EN_ENTREGA);

        // Assert
        assertEquals(EstadoRepartidor.EN_ENTREGA, result.getEstado());
        ArgumentCaptor<Repartidor> captor = ArgumentCaptor.forClass(Repartidor.class);
        verify(repartidorRepository).save(captor.capture());
        assertEquals(EstadoRepartidor.EN_ENTREGA, captor.getValue().getEstado());
    }

    // HU6 - Actualizar estado del repartidor
    @Test
    @DisplayName("TC-018 - Repartidor EN_ENTREGA vuelve a ACTIVO al finalizar entrega")
    void shouldReturnToActivoWhenDeliveryEnds_TC018() {
        // Arrange
        Repartidor existente = repartidor(1L, EstadoRepartidor.EN_ENTREGA);
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(repartidorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Repartidor result = service.cambiarEstado(1L, EstadoRepartidor.ACTIVO);

        // Assert
        assertEquals(EstadoRepartidor.ACTIVO, result.getEstado());
        verify(repartidorRepository).save(any());
    }

    // HU6 - Actualizar estado del repartidor
    @Test
    @DisplayName("TC-019 - Repartidor EN_ENTREGA vuelve a ACTIVO al cancelar pedido")
    void shouldReturnToActivoWhenOrderCancelled_TC019() {
        // Arrange
        Repartidor existente = repartidor(1L, EstadoRepartidor.EN_ENTREGA);
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(repartidorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Repartidor result = service.cambiarEstado(1L, EstadoRepartidor.ACTIVO);

        // Assert
        assertEquals(EstadoRepartidor.ACTIVO, result.getEstado());
        verify(repartidorRepository).save(any());
    }

    @Test
    @DisplayName("TC-017/018/019 - Repartidor inexistente lanza error")
    void shouldThrowWhenRepartidorNotFound() {
        // Arrange
        when(repartidorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RepartidorNotFoundException.class, () -> service.cambiarEstado(99L, EstadoRepartidor.ACTIVO));
        verify(repartidorRepository, never()).save(any());
    }

    private Repartidor repartidor(Long id, EstadoRepartidor estado) {
        return Repartidor.builder()
                .id(id)
                .nombre("Juan")
                .estado(estado)
                .vehiculo(TipoVehiculo.MOTO)
                .ubicacion(new Coordenada(10, 10))
                .build();
    }
}
