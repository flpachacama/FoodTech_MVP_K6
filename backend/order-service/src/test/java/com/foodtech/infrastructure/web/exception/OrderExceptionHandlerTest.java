package com.foodtech.infrastructure.web.exception;

import com.foodtech.domain.exception.PedidoCancelException;
import com.foodtech.domain.exception.PedidoDeliverException;
import com.foodtech.domain.exception.PedidoNotFoundException;
import com.foodtech.domain.exception.RestauranteNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderExceptionHandlerTest {

    private OrderExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new OrderExceptionHandler();
    }

    @Test
    void handleRestauranteNotFound_retorna404ConMensaje() {
        RestauranteNotFoundException ex = new RestauranteNotFoundException(5L);

        ResponseEntity<Map<String, Object>> response = handler.handleRestauranteNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).containsEntry("error", "Restaurante no encontrado");
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsEntry("status", 404);
    }

    @Test
    void handlePedidoNotFound_retorna404ConMensaje() {
        PedidoNotFoundException ex = new PedidoNotFoundException(10L);

        ResponseEntity<Map<String, Object>> response = handler.handlePedidoNotFound(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).containsEntry("error", "Pedido no encontrado");
    }

    @Test
    void handlePedidoCancel_retorna400ConMensaje() {
        PedidoCancelException ex = new PedidoCancelException(3L, "ya entregado");

        ResponseEntity<Map<String, Object>> response = handler.handlePedidoCancel(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "No se puede cancelar el pedido");
    }

    @Test
    void handlePedidoDeliver_retorna400ConMensaje() {
        PedidoDeliverException ex = new PedidoDeliverException(4L, "ya entregado");

        ResponseEntity<Map<String, Object>> response = handler.handlePedidoDeliver(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "No se puede marcar como entregado");
    }

    @Test
    void handleValidation_retorna400ConDetalleDeLosCampos() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "clienteNombre", "no debe estar vacío");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "Solicitud inválida");
        assertThat(response.getBody().get("detail").toString()).contains("clienteNombre");
    }

    @Test
    void handleValidation_conVariosErrores_concatenaConSemicolon() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError e1 = new FieldError("obj", "clienteNombre", "no debe estar vacío");
        FieldError e2 = new FieldError("obj", "restauranteId", "no debe ser nulo");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(e1, e2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertThat(response.getBody().get("detail").toString())
                .contains("clienteNombre")
                .contains("restauranteId")
                .contains(";");
    }

    @Test
    void handleIllegalArgument_retorna400ConMensaje() {
        IllegalArgumentException ex = new IllegalArgumentException("restauranteId requerido");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "Datos inválidos");
        assertThat(response.getBody()).containsEntry("detail", "restauranteId requerido");
    }

    @Test
    void handleIllegalState_retorna502ConMensaje() {
        IllegalStateException ex = new IllegalStateException("Error al comunicarse con delivery");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalState(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(502);
        assertThat(response.getBody()).containsEntry("error", "Error en servicio de delivery");
    }

    @Test
    void handleGeneral_retorna500ConMensaje() {
        Exception ex = new RuntimeException("error inesperado");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).containsEntry("error", "Error interno del servidor");
        assertThat(response.getBody()).containsEntry("detail", "error inesperado");
    }
}
