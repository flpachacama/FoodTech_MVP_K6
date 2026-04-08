package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepartidorListResponseDtoTest {

    @Test
    void debeConstruirConBuilder_yLeerTodosLosCampos() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(1L)
                .nombre("Carlos Mendoza")
                .estado("ACTIVO")
                .vehiculo("MOTO")
                .ubicacionX(10.5)
                .ubicacionY(-20.75)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNombre()).isEqualTo("Carlos Mendoza");
        assertThat(dto.getEstado()).isEqualTo("ACTIVO");
        assertThat(dto.getVehiculo()).isEqualTo("MOTO");
        assertThat(dto.getUbicacionX()).isEqualTo(10.5);
        assertThat(dto.getUbicacionY()).isEqualTo(-20.75);
    }

    @Test
    void debeCrearConConstructorNoArgs_yPermitirSetters() {
        RepartidorListResponseDto dto = new RepartidorListResponseDto();
        dto.setId(2L);
        dto.setNombre("Ana García");
        dto.setEstado("EN_ENTREGA");
        dto.setVehiculo("BICICLETA");
        dto.setUbicacionX(5.0);
        dto.setUbicacionY(8.0);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getNombre()).isEqualTo("Ana García");
        assertThat(dto.getEstado()).isEqualTo("EN_ENTREGA");
        assertThat(dto.getVehiculo()).isEqualTo("BICICLETA");
        assertThat(dto.getUbicacionX()).isEqualTo(5.0);
        assertThat(dto.getUbicacionY()).isEqualTo(8.0);
    }

    @Test
    void debeCrearConConstructorAllArgs() {
        RepartidorListResponseDto dto = new RepartidorListResponseDto(3L, "Pedro", "ACTIVO", "AUTO", 1.1, 2.2);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getNombre()).isEqualTo("Pedro");
        assertThat(dto.getEstado()).isEqualTo("ACTIVO");
        assertThat(dto.getVehiculo()).isEqualTo("AUTO");
        assertThat(dto.getUbicacionX()).isEqualTo(1.1);
        assertThat(dto.getUbicacionY()).isEqualTo(2.2);
    }

    @Test
    void equals_debeRetornarTrue_cuandoDosInstanciasTienenMismosValores() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").vehiculo("MOTO")
                .ubicacionX(10.0).ubicacionY(20.0).build();

        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").vehiculo("MOTO")
                .ubicacionX(10.0).ubicacionY(20.0).build();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_debeRetornarFalse_cuandoDiferenteId() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder().id(1L).nombre("Carlos").build();
        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder().id(2L).nombre("Carlos").build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_debeSerIgual_cuandoMismosValores() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").build();
        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").build();

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void toString_debeIncluirNombreYEstado() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").vehiculo("MOTO")
                .ubicacionX(1.0).ubicacionY(2.0).build();

        String result = dto.toString();
        assertThat(result).contains("Carlos").contains("ACTIVO").contains("MOTO");
    }

    @Test
    void debeAceptarCamposNulos_sinExcepcion() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(null).nombre(null).estado(null).vehiculo(null)
                .ubicacionX(null).ubicacionY(null).build();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getNombre()).isNull();
        assertThat(dto.getEstado()).isNull();
        assertThat(dto.getVehiculo()).isNull();
        assertThat(dto.getUbicacionX()).isNull();
        assertThat(dto.getUbicacionY()).isNull();
    }

    @Test
    void equals_cuandoMismaReferencia_retornaTrue() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").build();

        assertThat(dto.equals(dto)).isTrue();
    }

    @Test
    void equals_cuandoObjectoNull_retornaFalse() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").build();

        assertThat(dto.equals(null)).isFalse();
    }

    @Test
    void equals_cuandoDiferenteTipo_retornaFalse() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").build();

        assertThat(dto.equals("string")).isFalse();
    }

    @Test
    void equals_cuandoIdNulaEnUnoYNotNulaEnOtro_retornaFalse() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder()
                .id(null).nombre("Carlos").estado("ACTIVO").build();
        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").build();

        assertThat(dto1).isNotEqualTo(dto2);
        assertThat(dto2).isNotEqualTo(dto1);
    }

    @Test
    void equals_cuandoTodosLosCamposNulos_somoIguales() {
        RepartidorListResponseDto dto1 = new RepartidorListResponseDto();
        RepartidorListResponseDto dto2 = new RepartidorListResponseDto();

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void equals_cuandoNombreDiferente_retornaFalse() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").vehiculo("MOTO")
                .ubicacionX(10.0).ubicacionY(20.0).build();
        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Pedro").estado("ACTIVO").vehiculo("MOTO")
                .ubicacionX(10.0).ubicacionY(20.0).build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_cuandoEstadoNuloEnUnoYNoEnOtro_retornaFalse() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado(null).build();
        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void equals_cuandoUbicacionXDiferente_retornaFalse() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").ubicacionX(1.0).ubicacionY(2.0).build();
        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").ubicacionX(9.0).ubicacionY(2.0).build();

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void hashCode_cuandoCamposNulos_noLanzaExcepcion() {
        RepartidorListResponseDto dto1 = new RepartidorListResponseDto();
        RepartidorListResponseDto dto2 = new RepartidorListResponseDto();

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void hashCode_cuandoCamposMixtos_calculaCorrectamente() {
        RepartidorListResponseDto dto1 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado(null).vehiculo("MOTO")
                .ubicacionX(null).ubicacionY(5.0).build();
        RepartidorListResponseDto dto2 = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado(null).vehiculo("MOTO")
                .ubicacionX(null).ubicacionY(5.0).build();

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
