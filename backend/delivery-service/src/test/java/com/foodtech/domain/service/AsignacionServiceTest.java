package com.foodtech.domain.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el servicio de dominio AsignacionService.
 * Tests puros sin dependencias externas ni mocks.
 */
class AsignacionServiceTest {

    private AsignacionService asignacionService;

    @BeforeEach
    void setUp() {
        asignacionService = new AsignacionService();
    }

    @Test
    void priorizarRepartidores_ListaVacia_returnsEmptyList() {
        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Collections.emptyList(), 
            new Coordenada(0, 0), 
            Clima.SOLEADO
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void priorizarRepartidores_OrdenCorrecto_sortsByTimeEstimated() {
        Repartidor r1 = Repartidor.builder().id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(10,10)).build();
        Repartidor r2 = Repartidor.builder().id(2L).nombre("R2").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(20,20)).build();
        Repartidor r3 = Repartidor.builder().id(3L).nombre("R3").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(5,5)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(r1, r2, r3), 
            new Coordenada(0,0), 
            Clima.SOLEADO
        );

        assertEquals(3, result.size());
        assertEquals("R3", result.get(0).getNombre());
        assertEquals("R1", result.get(1).getNombre());
        assertEquals("R2", result.get(2).getNombre());
    }

    @Test
    void priorizarRepartidores_EmpateDistancia_handlesTieGracefully() {
        Repartidor r1 = Repartidor.builder().id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,0)).build();
        Repartidor r2 = Repartidor.builder().id(2L).nombre("R2").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(0,1)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(r1, r2), 
            new Coordenada(0,0), 
            Clima.SOLEADO
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("R1")));
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("R2")));
    }

    @Test
    void priorizarRepartidores_MismoPunto_zeroDistanceComesFirst() {
        Repartidor r = Repartidor.builder().id(1L).nombre("R").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(10,10)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Collections.singletonList(r), 
            new Coordenada(10,10), 
            Clima.SOLEADO
        );

        assertEquals(1, result.size());
        assertEquals("R", result.get(0).getNombre());
    }

    @Test
    void debePriorizarMotoLejanaSobreBiciCercana() {
        Repartidor bici = Repartidor.builder().id(1L).nombre("Bici").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(10,0)).build();
        Repartidor moto = Repartidor.builder().id(2L).nombre("Moto").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(12,0)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(bici, moto), 
            new Coordenada(0,0), 
            Clima.SOLEADO
        );

        assertFalse(result.isEmpty());
        assertEquals("Moto", result.get(0).getNombre());
    }

    @Test
    void debePriorizarPorCercaniaSiEsMismoVehiculo() {
        Repartidor near = Repartidor.builder().id(1L).nombre("Near").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(5,0)).build();
        Repartidor far = Repartidor.builder().id(2L).nombre("Far").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(15,0)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(far, near), 
            new Coordenada(0,0), 
            Clima.SOLEADO
        );

        assertEquals(2, result.size());
        assertEquals("Near", result.get(0).getNombre());
    }

    @Test
    void debeManejarEmpateDeTiempoEstimado() {
        Repartidor auto = Repartidor.builder().id(1L).nombre("Auto").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(30,0)).build();
        Repartidor moto = Repartidor.builder().id(2L).nombre("Moto").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(20,0)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(auto, moto), 
            new Coordenada(0,0), 
            Clima.SOLEADO
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("Auto")));
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("Moto")));
    }

    @Test
    void debeCombinarFiltroClimaYPrioridadTiempo() {
        Repartidor bici = Repartidor.builder().id(1L).nombre("Bici").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,0)).build();
        Repartidor moto = Repartidor.builder().id(2L).nombre("Moto").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(5,0)).build();
        Repartidor auto = Repartidor.builder().id(3L).nombre("Auto").estado(EstadoRepartidor.ACTIVO)
            .vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(20,0)).build();

        List<Repartidor> result = asignacionService.priorizarRepartidores(
            Arrays.asList(bici, moto, auto), 
            new Coordenada(0,0), 
            Clima.LLUVIA_SUAVE
        );

        assertEquals(2, result.size());
        assertEquals("Moto", result.get(0).getNombre());
        assertEquals("Auto", result.get(1).getNombre());
    }
}
