package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepartidorResponseDTOTest {

    @Test
    void debeConstruirConBuilder_yLeerTodosLosCampos() {
        RepartidorResponseDTO dto = RepartidorResponseDTO.builder()
                .id(1L)
                .nombre("Carlos Mendoza")
                .estado("ACTIVO")
                .vehiculo("MOTO")
                .x(15.5)
                .y(-30.25)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNombre()).isEqualTo("Carlos Mendoza");
        assertThat(dto.getEstado()).isEqualTo("ACTIVO");
        assertThat(dto.getVehiculo()).isEqualTo("MOTO");
        assertThat(dto.getX()).isEqualTo(15.5);
        assertThat(dto.getY()).isEqualTo(-30.25);
    }

    @Test
    void debeCrearConConstructorNoArgs_yCamposNulos() {
        RepartidorResponseDTO dto = new RepartidorResponseDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNombre()).isNull();
        assertThat(dto.getEstado()).isNull();
        assertThat(dto.getVehiculo()).isNull();
        assertThat(dto.getX()).isNull();
        assertThat(dto.getY()).isNull();
    }

    @Test
    void debeCrearConConstructorAllArgs() {
        RepartidorResponseDTO dto = new RepartidorResponseDTO(2L, "Ana", "EN_ENTREGA", "BICICLETA", 5.0, 8.0);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getNombre()).isEqualTo("Ana");
        assertThat(dto.getEstado()).isEqualTo("EN_ENTREGA");
        assertThat(dto.getVehiculo()).isEqualTo("BICICLETA");
        assertThat(dto.getX()).isEqualTo(5.0);
        assertThat(dto.getY()).isEqualTo(8.0);
    }

    @Test
    void debeConstruirConCamposNulos_sinExcepcion() {
        RepartidorResponseDTO dto = RepartidorResponseDTO.builder()
                .id(null).nombre(null).estado(null).vehiculo(null)
                .x(null).y(null).build();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getEstado()).isNull();
        assertThat(dto.getVehiculo()).isNull();
        assertThat(dto.getX()).isNull();
        assertThat(dto.getY()).isNull();
    }
}
