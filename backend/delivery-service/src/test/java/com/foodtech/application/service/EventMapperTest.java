package com.foodtech.application.service;

import com.foodtech.domain.model.EstadoRepartidor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventMapperTest {

    @Test
    void mapToEstado_cuandoEventoNull_lanzaIllegalArgument() {
        assertThatThrownBy(() -> EventMapper.mapToEstado(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    void mapToEstado_cuandoEventoEntregado_retornaActivo() {
        assertThat(EventMapper.mapToEstado("ENTREGADO")).isEqualTo(EstadoRepartidor.ACTIVO);
    }

    @Test
    void mapToEstado_cuandoEventoCancelado_retornaActivo() {
        assertThat(EventMapper.mapToEstado("CANCELADO")).isEqualTo(EstadoRepartidor.ACTIVO);
    }

    @Test
    void mapToEstado_cuandoEventoInvalido_lanzaIllegalArgument() {
        assertThatThrownBy(() -> EventMapper.mapToEstado("DESCONOCIDO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DESCONOCIDO");
    }
}
