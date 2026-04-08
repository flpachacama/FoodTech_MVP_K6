package com.foodtech.infrastructure.web.client;

import com.foodtech.domain.port.output.DeliveryClient.DeliveryAssignmentRequest;
import com.foodtech.domain.port.output.DeliveryClient.DeliveryAssignmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
class DeliveryClientAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DeliveryClientAdapter adapter;

    private DeliveryAssignmentRequest requestBase;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "deliveryServiceUrl", "http://localhost:8080");
        requestBase = new DeliveryAssignmentRequest(1L, 10.0, 20.0, "SOLEADO");
    }

    @Test
    void assign_cuandoDeliveryRespondeOk_retornaResponse() {
        DeliveryAssignmentResponse expected =
                new DeliveryAssignmentResponse(1L, "ASIGNADO", 7L, "Carlos", 15);

        when(restTemplate.postForObject(
                eq("http://localhost:8080/delivery"),
                eq(requestBase),
                eq(DeliveryAssignmentResponse.class)))
                .thenReturn(expected);

        DeliveryAssignmentResponse result = adapter.assign(requestBase);

        assertThat(result).isNotNull();
        assertThat(result.estado()).isEqualTo("ASIGNADO");
        assertThat(result.repartidorId()).isEqualTo(7L);
        assertThat(result.tiempoEstimado()).isEqualTo(15);
    }

    @Test
    void assign_cuandoDeliveryRetornaNull_lanzaRuntimeException() {
        when(restTemplate.postForObject(anyString(), any(), eq(DeliveryAssignmentResponse.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> adapter.assign(requestBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no devolvió respuesta");
    }

    @Test
    void assign_cuandoDeliveryLanzaRestClientResponseException_wrappea() {
        when(restTemplate.postForObject(anyString(), any(), eq(DeliveryAssignmentResponse.class)))
                .thenThrow(HttpServerErrorException.create(
                        INTERNAL_SERVER_ERROR, "Internal Server Error",
                        org.springframework.http.HttpHeaders.EMPTY,
                        "Service unavailable".getBytes(),
                        java.nio.charset.StandardCharsets.UTF_8));

        assertThatThrownBy(() -> adapter.assign(requestBase))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error en delivery-service")
                .hasMessageContaining("500");
    }

    @Test
    void releaseRepartidor_cuandoOk_noLanzaExcepcion() {
        doNothing().when(restTemplate).put(anyString(), any());

        adapter.releaseRepartidor(7L, "ENTREGADO");

        verify(restTemplate).put("http://localhost:8080/delivery/7/state", java.util.Map.of("evento", "ENTREGADO"));
    }

    @Test
    void releaseRepartidor_cuandoRestClientResponseException_tragarExcepcionSinPropagar() {
        doThrow(HttpServerErrorException.create(
                INTERNAL_SERVER_ERROR, "Error",
                org.springframework.http.HttpHeaders.EMPTY,
                new byte[0],
                java.nio.charset.StandardCharsets.UTF_8))
                .when(restTemplate).put(anyString(), any());

        adapter.releaseRepartidor(7L, "CANCELADO");
    }

    @Test
    void releaseRepartidor_cuandoExcepcionGenerica_tragarExcepcionSinPropagar() {
        doThrow(new RuntimeException("Timeout de red"))
                .when(restTemplate).put(anyString(), any());

        adapter.releaseRepartidor(7L, "CANCELADO");
    }
}
