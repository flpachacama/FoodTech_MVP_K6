package com.foodtech.infrastructure.web.exception;

import com.foodtech.domain.exception.RepartidorNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void debeRetornarMapaDeErrores_cuandoHayValidationException() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "pedidoId", "El pedidoId es obligatorio"));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        Map<String, String> resp = handler.handleValidationExceptions(ex);

        assertThat(resp).containsEntry("pedidoId", "El pedidoId es obligatorio");
    }

    @Test
    void debeRetornarMensajeClimaInvalido_cuandoIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("bad clima");

        Map<String, String> resp = handler.handleIllegalArgumentException(ex);

        assertThat(resp).containsKey("error");
        assertThat(resp.get("error")).contains("Clima inválido");
    }

    @Test
    void debeRetornarRepartidorNoEncontrado_cuandoRepartidorNotFoundException() {
        RepartidorNotFoundException ex = new RepartidorNotFoundException(99L);

        Map<String, String> resp = handler.handleRepartidorNotFound(ex);

        assertThat(resp).containsKey("error");
        assertThat(resp.get("error")).contains("99");
    }

    @Test
    void debeRetornarErrorInterno_cuandoExceptionGenerica() {
        Exception ex = new Exception("boom");

        Map<String, String> resp = handler.handleGenericException(ex);

        assertThat(resp).containsEntry("error", "Error interno del servidor");
    }

    @Test
    void debeRetornarMensajeCausa_cuandoHttpMessageNotReadableConCausa() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        Throwable cause = new RuntimeException("causa del error");
        when(ex.getCause()).thenReturn(cause);

        Map<String, String> resp = handler.handleHttpMessageNotReadable(ex);

        assertThat(resp).containsKey("error");
        assertThat(resp.get("error")).isEqualTo("causa del error");
    }

    @Test
    void debeRetornarMensajeEx_cuandoHttpMessageNotReadableSinCausa() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(null);
        when(ex.getMessage()).thenReturn("mensaje directo de la excepcion");

        Map<String, String> resp = handler.handleHttpMessageNotReadable(ex);

        assertThat(resp).containsKey("error");
        assertThat(resp.get("error")).isEqualTo("mensaje directo de la excepcion");
    }
}
