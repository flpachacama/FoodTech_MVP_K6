package com.foodtech.domain.port.input;

import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.EstadoRepartidor;

public interface RepartidorUseCase {

    Repartidor cambiarEstado(Long repartidorId, EstadoRepartidor nuevoEstado);

}
