package com.foodtech.domain.service;

import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AsignacionService {

    public List<Repartidor> priorizarRepartidores(List<Repartidor> repartidores,
                                                   Coordenada restauranteUbicacion,
                                                   Clima clima) {
        return repartidores.stream()
            .filter(r -> r != null && r.getUbicacion() != null && r.getVehiculo() != null)
            .filter(r -> esVehiculoApto(r.getVehiculo(), clima))
            .sorted((r1, r2) -> {
                double d1 = r1.getUbicacion().distanciaA(restauranteUbicacion);
                double d2 = r2.getUbicacion().distanciaA(restauranteUbicacion);
                double t1 = calcularTiempoEstimado(d1, r1.getVehiculo().getVelocidadKmH());
                double t2 = calcularTiempoEstimado(d2, r2.getVehiculo().getVelocidadKmH());
                return Double.compare(t1, t2);
            })
            .collect(Collectors.toList());
    }

    private double calcularTiempoEstimado(double distancia, int velocidad) {
        return distancia / velocidad;
    }

    public int calcularTiempoEstimadoMinutos(Repartidor repartidor, Coordenada destino) {
        double distancia = repartidor.getUbicacion().distanciaA(destino);
        double tiempoHoras = calcularTiempoEstimado(distancia, repartidor.getVehiculo().getVelocidadKmH());
        return (int) Math.round(tiempoHoras * 60); 
    }

    private boolean esVehiculoApto(TipoVehiculo vehiculo, Clima clima) {
        if (clima == null) {
            return true;
        }
        return switch (clima) {
            case LLUVIA_FUERTE -> vehiculo == TipoVehiculo.AUTO;
            case LLUVIA_SUAVE -> vehiculo == TipoVehiculo.MOTO || vehiculo == TipoVehiculo.AUTO;
            case SOLEADO -> true;
        };
    }
}
