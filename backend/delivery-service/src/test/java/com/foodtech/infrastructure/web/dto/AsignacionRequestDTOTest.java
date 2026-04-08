package com.foodtech.infrastructure.web.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AsignacionRequestDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void debeConstruirYLeerCampos_cuandoSeUsaBuilder() {
        AsignacionRequestDTO dto = AsignacionRequestDTO.builder()
                .pedidoId(1L)
                .restauranteX(25.5)
                .restauranteY(-40.75)
                .clima("SOLEADO")
                .build();

        assertThat(dto).isNotNull();
        assertThat(dto.getPedidoId()).isEqualTo(1L);
        assertThat(dto.getRestauranteX()).isEqualTo(25.5);
        assertThat(dto.getRestauranteY()).isEqualTo(-40.75);
        assertThat(dto.getClima()).isEqualTo("SOLEADO");
    }

    @Test
    void debeDetectarCamposNulos_cuandoRequeridosFaltan() {
        AsignacionRequestDTO dto = AsignacionRequestDTO.builder()
                .pedidoId(null)
                .restauranteX(null)
                .restauranteY(null)
                .clima(null)
                .build();

        Set<ConstraintViolation<AsignacionRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("pedidoId"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("restauranteX"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("restauranteY"));
    }

    @Test
    void debeCrearConConstructorNoArgs_yCamposNulos() {
        AsignacionRequestDTO dto = new AsignacionRequestDTO();

        assertThat(dto.getPedidoId()).isNull();
        assertThat(dto.getRestauranteX()).isNull();
        assertThat(dto.getRestauranteY()).isNull();
        assertThat(dto.getClima()).isNull();
    }

    @Test
    void debeCrearConConstructorAllArgs() {
        AsignacionRequestDTO dto = new AsignacionRequestDTO(5L, 10.0, -5.0, "LLUVIA_FUERTE");

        assertThat(dto.getPedidoId()).isEqualTo(5L);
        assertThat(dto.getRestauranteX()).isEqualTo(10.0);
        assertThat(dto.getRestauranteY()).isEqualTo(-5.0);
        assertThat(dto.getClima()).isEqualTo("LLUVIA_FUERTE");
    }
}
