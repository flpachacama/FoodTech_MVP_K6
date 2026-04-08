package com.foodtech.domain.service;

import com.foodtech.domain.exception.RepartidorNotFoundException;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.port.input.RepartidorUseCase;
import com.foodtech.domain.port.output.RepartidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepartidorService implements RepartidorUseCase {

    private final RepartidorRepository repartidorRepository;

    @Override
    public Repartidor cambiarEstado(Long repartidorId, EstadoRepartidor nuevoEstado) {
        Repartidor existente = repartidorRepository.findById(repartidorId)
                .orElseThrow(() -> new RepartidorNotFoundException(repartidorId));

        Repartidor actualizado = Repartidor.builder()
                .id(existente.getId())
                .nombre(existente.getNombre())
                .estado(nuevoEstado)
                .vehiculo(existente.getVehiculo())
                .ubicacion(existente.getUbicacion())
                .build();

        return repartidorRepository.save(actualizado);
    }
}
