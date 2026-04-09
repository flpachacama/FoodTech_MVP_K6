package com.foodtech.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordenadaTest {

    @Test
    void shouldCreateCoordenada_WhenValuesAreValid() {
        Coordenada a = new Coordenada(-12.5, 0.75);
        assertEquals(-12.5, a.x(), 1e-9);
        assertEquals(0.75, a.y(), 1e-9);

        Coordenada b = new Coordenada(100.25, -100.75);
        assertEquals(100.25, b.x(), 1e-9);
        assertEquals(-100.75, b.y(), 1e-9);
    }

    @Test
    void distancia_shouldReturnZero_whenSameCoordinates() {
        Coordenada a = new Coordenada(10.5, -20.25);
        assertEquals(0.0, a.distanciaA(a), 1e-6);
    }

    @Test
    void distancia_shouldReturnApproxFiveKm_for3_4_5TriangleAtEquator() {
        // Cerca del ecuador (lat≈0): 1° ≈ 111 km en ambos ejes
        // Δlat = 4/111° → 4 km, Δlng = 3/111° → 3 km → hipotenusa ≈ 5 km
        Coordenada a = new Coordenada(0.0, 0.0);
        Coordenada b = new Coordenada(3.0 / 111.0, 4.0 / 111.0);
        assertEquals(5.0, a.distanciaA(b), 0.05);
    }

    @Test
    void distancia_shouldBeSymmetric_betweenPoints() {
        // Dos puntos reales en Bogotá: la simetría debe mantenerse independiente del eje
        Coordenada a = new Coordenada(-74.0627, 4.6482);
        Coordenada b = new Coordenada(-74.0317, 4.6953);
        double d1 = a.distanciaA(b);
        double d2 = b.distanciaA(a);
        assertEquals(d1, d2, 1e-9);
        assertTrue(d1 > 0);
    }

    @Test
    void distancia_shouldReturnApproxSqrt2Km_forOneKmDiagonal() {
        // 1 km en cada eje → diagonal de sqrt(2) km
        Coordenada a = new Coordenada(0.0, 0.0);
        Coordenada b = new Coordenada(1.0 / 111.0, 1.0 / 111.0);
        assertEquals(Math.sqrt(2), a.distanciaA(b), 0.05);
    }

    @Test
    void distancia_shouldThrowException_whenTargetIsNull() {
        Coordenada a = new Coordenada(0.0, 0.0);
        assertThrows(IllegalArgumentException.class, () -> a.distanciaA(null));
    }
}
