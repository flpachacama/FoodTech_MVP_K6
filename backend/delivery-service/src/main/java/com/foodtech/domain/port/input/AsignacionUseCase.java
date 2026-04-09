package com.foodtech.domain.port.input;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Repartidor;

import java.util.List;

public interface AsignacionUseCase {

    List<Repartidor> obtenerRepartidoresPriorizados(Coordenada restauranteUbicacion, Clima clima);
}
