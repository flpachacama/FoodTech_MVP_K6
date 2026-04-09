package com.foodtech.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TiempoDeliveryCalculatorTest {

    private TiempoDeliveryCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TiempoDeliveryCalculator();
    }

    @Test
    void calcularMinutos_conCoordenadaOrigenNull_retornaCero() {
        assertThat(calculator.calcularMinutos(null, 4.0, 10.0, 20.0)).isEqualTo(0);
    }

    @Test
    void calcularMinutos_conOrigenYNull_retornaCero() {
        assertThat(calculator.calcularMinutos(1.0, null, 10.0, 20.0)).isEqualTo(0);
    }

    @Test
    void calcularMinutos_conDestinoXNull_retornaCero() {
        assertThat(calculator.calcularMinutos(1.0, 2.0, null, 20.0)).isEqualTo(0);
    }

    @Test
    void calcularMinutos_conDestinoYNull_retornaCero() {
        assertThat(calculator.calcularMinutos(1.0, 2.0, 10.0, null)).isEqualTo(0);
    }

    @Test
    void calcularMinutos_todoNull_retornaCero() {
        assertThat(calculator.calcularMinutos(null, null, null, null)).isEqualTo(0);
    }

    @Test
    void calcularMinutos_mismoPunto_retornaCero() {
        assertThat(calculator.calcularMinutos(10.0, 4.6, 10.0, 4.6)).isEqualTo(0);
    }

    @Test
    void calcularMinutos_distanciaRealConocida_retornaMinutosPositivos() {
        int minutos = calculator.calcularMinutos(-74.06, 4.64, -74.08, 4.66);

        assertThat(minutos).isGreaterThan(0);
    }

    @Test
    void calcularMinutos_distanciaLarga_retornaMinutosMayores() {
        int corta = calculator.calcularMinutos(0.0, 0.0, 0.01, 0.01);
        int larga = calculator.calcularMinutos(0.0, 0.0, 1.0, 1.0);

        assertThat(larga).isGreaterThan(corta);
    }
}
