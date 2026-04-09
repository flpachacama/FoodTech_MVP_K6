package com.foodtech.domain.port.output;

import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de Repartidor.
 */
public interface RepartidorRepository {

    Optional<Repartidor> findById(Long id);

    List<Repartidor> findByEstado(EstadoRepartidor estado);

    List<Repartidor> findAll();

    Repartidor save(Repartidor repartidor);
}
