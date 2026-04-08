package com.foodtech.domain.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FiltroClimaTest {

    private AsignacionService asignacionService;

    @BeforeEach
    void setUp() {
        asignacionService = new AsignacionService();
    }

    @Test
    void filtroClima_LluviaFuerte_SoloAutos() {
        Repartidor rMoto = Repartidor.builder().id(1L).nombre("MOTO").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(0,0)).build();
        Repartidor rBici = Repartidor.builder().id(2L).nombre("BICI").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,1)).build();
        Repartidor rAuto = Repartidor.builder().id(3L).nombre("AUTO").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(2,2)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(rMoto, rBici, rAuto), 
            new Coordenada(0,0), 
            Clima.LLUVIA_FUERTE
        );

        assertEquals(1, result.size());
        assertEquals(TipoVehiculo.AUTO, result.get(0).getVehiculo());
    }

    @Test
    void filtroClima_LluviaSuave_ExcluyeBici() {
        Repartidor rMoto = Repartidor.builder().id(1L).nombre("MOTO").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(0,0)).build();
        Repartidor rBici = Repartidor.builder().id(2L).nombre("BICI").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,1)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(rMoto, rBici), 
            new Coordenada(0,0), 
            Clima.LLUVIA_SUAVE
        );

        assertEquals(1, result.size());
        assertEquals(TipoVehiculo.MOTO, result.get(0).getVehiculo());
    }

    @Test
    void filtroClima_Soleado_TodosAptos() {
        Repartidor r1 = Repartidor.builder().id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(0,0)).build();
        Repartidor r2 = Repartidor.builder().id(2L).nombre("R2").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(1,1)).build();
        Repartidor r3 = Repartidor.builder().id(3L).nombre("R3").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(2,2)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(r1, r2, r3), 
            new Coordenada(0,0), 
            Clima.SOLEADO
        );

        assertEquals(3, result.size());
    }

    @Test
    void filtroClima_SinCandidatosAptos_returnsEmpty() {
        Repartidor bici1 = Repartidor.builder().id(1L).nombre("B1").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(5,5)).build();
        Repartidor bici2 = Repartidor.builder().id(2L).nombre("B2").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(6,6)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(bici1, bici2), 
            new Coordenada(0,0), 
            Clima.LLUVIA_FUERTE
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
