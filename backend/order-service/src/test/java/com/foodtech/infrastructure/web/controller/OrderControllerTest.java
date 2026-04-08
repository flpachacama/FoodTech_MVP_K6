package com.foodtech.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.domain.exception.PedidoCancelException;
import com.foodtech.domain.exception.PedidoDeliverException;
import com.foodtech.domain.exception.PedidoNotFoundException;
import com.foodtech.domain.exception.RestauranteNotFoundException;
import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.domain.port.input.OrderUseCase;
import com.foodtech.infrastructure.web.dto.CancelOrderResponseDto;
import com.foodtech.infrastructure.web.dto.DeliverOrderResponseDto;
import com.foodtech.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.infrastructure.web.dto.OrderResponseDto;
import com.foodtech.infrastructure.web.dto.ProductoPedidoDto;
import com.foodtech.infrastructure.web.exception.OrderExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(OrderExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderUseCase orderUseCase;

    private OrderRequestDto requestValido;
    private OrderResponseDto responseBase;

    @BeforeEach
    void setUp() {
        requestValido = OrderRequestDto.builder()
                .restauranteId(1L)
                .restauranteX(10.0)
                .restauranteY(20.0)
                .clima("SOLEADO")
                .clienteId(99L)
                .clienteNombre("Ana García")
                .clienteTelefono("600000001")
                .clienteCoordenadasX(5.0)
                .clienteCoordenadasY(8.0)
                .productos(List.of(ProductoPedidoDto.builder()
                        .id(1L).nombre("Hamburguesa").precio(BigDecimal.valueOf(8.50)).build()))
                .build();

        responseBase = OrderResponseDto.builder()
                .id(42L)
                .restauranteId(1L)
                .repartidorId(7L)
                .clienteId(99L)
                .clienteNombre("Ana García")
                .clienteTelefono("600000001")
                .clienteCoordenadasX(5.0)
                .clienteCoordenadasY(8.0)
                .tiempoEstimado(20)
                .estado(EstadoPedido.ASIGNADO)
                .productos(List.of(ProductoPedidoDto.builder()
                        .id(1L).nombre("Hamburguesa").precio(BigDecimal.valueOf(8.50)).build()))
                .build();
    }

    @Test
    void createOrder_conRequestValido_retorna201YCuerpoEsperado() throws Exception {
        when(orderUseCase.createOrder(any())).thenReturn(responseBase);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.estado").value("ASIGNADO"))
                .andExpect(jsonPath("$.repartidorId").value(7L))
                .andExpect(jsonPath("$.tiempoEstimado").value(20));
    }

    @Test
    void createOrder_cuandoRestauranteNoExiste_retorna404() throws Exception {
        when(orderUseCase.createOrder(any()))
                .thenThrow(new RestauranteNotFoundException(1L));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Restaurante no encontrado"));
    }

    @Test
    void createOrder_cuandoDeliveryFalla_retorna502() throws Exception {
        when(orderUseCase.createOrder(any()))
                .thenThrow(new IllegalStateException("Error al comunicarse con el servicio de delivery"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Error en servicio de delivery"));
    }

    @Test
    void createOrder_cuandoDatosInvalidos_retorna400ConIllegalArgument() throws Exception {
        when(orderUseCase.createOrder(any()))
                .thenThrow(new IllegalArgumentException("El restauranteId es obligatorio"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Datos inválidos"));
    }

    @Test
    void createOrder_cuandoExcepcionGenerica_retorna500() throws Exception {
        when(orderUseCase.createOrder(any()))
                .thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Error interno del servidor"));
    }

    @Test
    void cancelOrder_conPedidoAsignado_retorna200() throws Exception {
        CancelOrderResponseDto cancelResponse = CancelOrderResponseDto.builder()
                .id(42L)
                .estado(EstadoPedido.CANCELADO)
                .mensaje("Pedido cancelado exitosamente")
                .build();
        when(orderUseCase.cancelOrder(eq(42L))).thenReturn(cancelResponse);

        mockMvc.perform(put("/orders/42/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.estado").value("CANCELADO"))
                .andExpect(jsonPath("$.mensaje").value("Pedido cancelado exitosamente"));
    }

    @Test
    void cancelOrder_cuandoPedidoYaCancelado_retorna400() throws Exception {
        when(orderUseCase.cancelOrder(eq(42L)))
                .thenThrow(new PedidoCancelException(42L, "el pedido ya fue cancelado anteriormente"));

        mockMvc.perform(put("/orders/42/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("No se puede cancelar el pedido"));
    }

    @Test
    void cancelOrder_cuandoPedidoYaEntregado_retorna400() throws Exception {
        when(orderUseCase.cancelOrder(eq(55L)))
                .thenThrow(new PedidoCancelException(55L, "el pedido ya ha sido entregado"));

        mockMvc.perform(put("/orders/55/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("No se puede cancelar el pedido"));
    }

    @Test
    void cancelOrder_cuandoPedidoNoEncontrado_retorna404() throws Exception {
        when(orderUseCase.cancelOrder(eq(99L)))
                .thenThrow(new PedidoNotFoundException(99L));

        mockMvc.perform(put("/orders/99/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Pedido no encontrado"));
    }

    @Test
    void deliverOrder_conPedidoAsignado_retorna200() throws Exception {
        DeliverOrderResponseDto deliverResponse = DeliverOrderResponseDto.builder()
                .id(42L)
                .estado(EstadoPedido.ENTREGADO)
                .mensaje("Pedido marcado como entregado exitosamente")
                .build();
        when(orderUseCase.deliverOrder(eq(42L))).thenReturn(deliverResponse);

        mockMvc.perform(put("/orders/42/deliver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.estado").value("ENTREGADO"))
                .andExpect(jsonPath("$.mensaje").value("Pedido marcado como entregado exitosamente"));
    }

    @Test
    void deliverOrder_cuandoPedidoPendiente_retorna400() throws Exception {
        when(orderUseCase.deliverOrder(eq(10L)))
                .thenThrow(new PedidoDeliverException(10L, "el pedido no tiene repartidor asignado"));

        mockMvc.perform(put("/orders/10/deliver"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("No se puede marcar como entregado"));
    }

    @Test
    void deliverOrder_cuandoPedidoCancelado_retorna400() throws Exception {
        when(orderUseCase.deliverOrder(eq(11L)))
                .thenThrow(new PedidoDeliverException(11L, "el pedido fue cancelado"));

        mockMvc.perform(put("/orders/11/deliver"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("No se puede marcar como entregado"));
    }

    @Test
    void deliverOrder_cuandoPedidoYaEntregado_retorna400() throws Exception {
        when(orderUseCase.deliverOrder(eq(12L)))
                .thenThrow(new PedidoDeliverException(12L, "el pedido ya ha sido entregado"));

        mockMvc.perform(put("/orders/12/deliver"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("No se puede marcar como entregado"));
    }

    @Test
    void deliverOrder_cuandoPedidoNoEncontrado_retorna404() throws Exception {
        when(orderUseCase.deliverOrder(eq(99L)))
                .thenThrow(new PedidoNotFoundException(99L));

        mockMvc.perform(put("/orders/99/deliver"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Pedido no encontrado"));
    }

    @Test
    void getOrderByRepartidorId_conPedidoActivo_retorna200() throws Exception {
        when(orderUseCase.getOrderByRepartidorId(eq(7L))).thenReturn(responseBase);

        mockMvc.perform(get("/orders/repartidor/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.repartidorId").value(7L))
                .andExpect(jsonPath("$.estado").value("ASIGNADO"));
    }

    @Test
    void getOrderByRepartidorId_cuandoSinPedidoActivo_retorna404() throws Exception {
        when(orderUseCase.getOrderByRepartidorId(eq(99L)))
                .thenThrow(new PedidoNotFoundException(99L));

        mockMvc.perform(get("/orders/repartidor/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Pedido no encontrado"));
    }
}
