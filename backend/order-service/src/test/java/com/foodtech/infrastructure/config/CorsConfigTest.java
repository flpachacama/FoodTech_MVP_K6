package com.foodtech.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private final CorsConfig corsConfig = new CorsConfig();

    @Test
    void corsFilter_noEsNulo() {
        CorsFilter filter = corsConfig.corsFilter();

        assertThat(filter).isNotNull();
    }

    @Test
    void corsFilter_retornaInstanciaDistintaEnCadaLlamada() {
        CorsFilter first = corsConfig.corsFilter();
        CorsFilter second = corsConfig.corsFilter();

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
    }
}
