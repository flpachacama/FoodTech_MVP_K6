package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsignacionResponseDTOTest {

    @Test
    void debeConstruirYLeerCampos_cuandoSeUsaBuilder() {
        AsignacionResponseDTO dto = AsignacionResponseDTO.builder()
                .pedidoId(1L)
                .estado("ASIGNADO")
                .repartidorId(2L)
                .nombreRepartidor("Carlos")
                .tiempoEstimado(20)
                .build();

        assertThat(dto).isNotNull();
        assertThat(dto.getPedidoId()).isEqualTo(1L);
        assertThat(dto.getEstado()).isEqualTo("ASIGNADO");
        assertThat(dto.getRepartidorId()).isEqualTo(2L);
        assertThat(dto.getNombreRepartidor()).isEqualTo("Carlos");
        assertThat(dto.getTiempoEstimado()).isEqualTo(20);
    }

    @Test
    void debeCrearConConstructorNoArgs_yCamposNulos() {
        AsignacionResponseDTO dto = new AsignacionResponseDTO();

        assertThat(dto.getPedidoId()).isNull();
        assertThat(dto.getEstado()).isNull();
        assertThat(dto.getRepartidorId()).isNull();
        assertThat(dto.getNombreRepartidor()).isNull();
        assertThat(dto.getTiempoEstimado()).isNull();
    }

    @Test
    void debeCrearConConstructorAllArgs() {
        AsignacionResponseDTO dto = new AsignacionResponseDTO(3L, "PENDIENTE", null, null, null);

        assertThat(dto.getPedidoId()).isEqualTo(3L);
        assertThat(dto.getEstado()).isEqualTo("PENDIENTE");
        assertThat(dto.getRepartidorId()).isNull();
        assertThat(dto.getNombreRepartidor()).isNull();
        assertThat(dto.getTiempoEstimado()).isNull();
    }
}

