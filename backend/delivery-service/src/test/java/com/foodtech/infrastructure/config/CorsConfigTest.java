package com.foodtech.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    private final CorsConfig corsConfig = new CorsConfig();

    @Test
    void corsFilter_debeRetornarInstanciaNoNula() {
        CorsFilter filter = corsConfig.corsFilter();

        assertThat(filter).isNotNull();
    }
}
