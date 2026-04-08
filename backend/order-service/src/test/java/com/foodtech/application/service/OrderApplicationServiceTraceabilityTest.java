package com.foodtech.application.service;

import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.domain.model.Pedido;
import com.foodtech.domain.model.ProductoPedido;
import com.foodtech.domain.exception.PedidoCancelException;
import com.foodtech.domain.port.output.DeliveryClient;
import com.foodtech.domain.port.output.DeliveryClient.DeliveryAssignmentResponse;
import com.foodtech.domain.port.output.PedidoRepository;
import com.foodtech.domain.service.TiempoDeliveryCalculator;
import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.infrastructure.web.dto.OrderResponseDto;
import com.foodtech.infrastructure.web.dto.ProductoPedidoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTraceabilityTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private RestauranteJpaRepository restauranteRepository;

    @Mock
    private TiempoDeliveryCalculator tiempoCalculator;

    @InjectMocks
    private OrderApplicationService service;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(tiempoCalculator.calcularMinutos(any(), any(), any(), any())).thenReturn(0);
    }

    // HU7 - Generar pedido
    @Test
    @DisplayName("TC-020 - Carrito con productos genera pedido con items y estado esperado")
    void shouldCreateOrderWithProducts_TC020() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();
        request.setProductos(List.of(
                createProduct(1L, "Hamburguesa", "8.50"),
                createProduct(2L, "Papas", "4.00")
        ));

        Pedido savedPending = Pedido.builder()
                .id(101L)
                .restauranteId(request.getRestauranteId())
                .clienteNombre(request.getClienteNombre())
                .clienteCoordenadasX(request.getClienteCoordenadasX())
                .clienteCoordenadasY(request.getClienteCoordenadasY())
                .productos(List.of(
                        ProductoPedido.builder().id(1L).nombre("Hamburguesa").precio(new BigDecimal("8.50")).build(),
                        ProductoPedido.builder().id(2L).nombre("Papas").precio(new BigDecimal("4.00")).build()
                ))
                .estado(EstadoPedido.PENDIENTE)
                .build();

        when(pedidoRepository.save(any())).thenReturn(savedPending);
        when(deliveryClient.assign(any()))
                .thenReturn(new DeliveryAssignmentResponse(101L, "ASIGNADO", 9L, "Rider", 18));

        // Act
        OrderResponseDto response = service.createOrder(request);

        // Assert
        assertEquals(EstadoPedido.ASIGNADO, response.getEstado());
        assertEquals(2, response.getProductos().size());
    }

    // HU7 - Generar pedido
    @Test
    @DisplayName("TC-021 - Si se elimina el unico producto y el carrito queda vacio, no permite confirmar")
    void shouldBlockOrderWhenSingleItemWasRemoved_TC021() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();
        request.setProductos(List.of());

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createOrder(request));

        // Assert
        assertTrue(ex.getMessage().contains("al menos un producto"));
    }

    // HU7 - Generar pedido
    @Test
    @DisplayName("TC-022 - Carrito vacio no permite avanzar")
    void shouldBlockOrderWhenCartIsEmpty_TC022() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();
        request.setProductos(List.of());

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createOrder(request));

        // Assert
        assertTrue(ex.getMessage().contains("al menos un producto"));
    }

    // HU7 - Generar pedido
    @Test
    @DisplayName("TC-023 - Producto invalido en carrito es rechazado")
    void shouldRejectInvalidProductEntry_TC023() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();
        request.setProductos(Arrays.asList((ProductoPedidoDto) null));

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createOrder(request));

        // Assert
        assertTrue(ex.getMessage().contains("productos inválidos"));
    }

    // HU8 - Confirmar pedido
    @Test
    @DisplayName("TC-024 - Confirmacion valida crea pedido ASIGNADO y con ETA")
    void shouldConfirmValidOrder_TC024() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();

        Pedido savedPending = Pedido.builder()
                .id(202L)
                .restauranteId(request.getRestauranteId())
                .clienteNombre(request.getClienteNombre())
                .clienteCoordenadasX(request.getClienteCoordenadasX())
                .clienteCoordenadasY(request.getClienteCoordenadasY())
                .productos(List.of())
                .estado(EstadoPedido.PENDIENTE)
                .build();

        when(pedidoRepository.save(any())).thenReturn(savedPending);
        when(deliveryClient.assign(any()))
                .thenReturn(new DeliveryAssignmentResponse(202L, "ASIGNADO", 5L, "Mario", 12));

        // Act
        OrderResponseDto response = service.createOrder(request);

        // Assert
        assertEquals(EstadoPedido.ASIGNADO, response.getEstado());
        assertEquals(12, response.getTiempoEstimado());
    }

    // HU8 - Confirmar pedido
    @Test
    @DisplayName("TC-026 - Confirmacion sin telefono bloquea campos obligatorios")
    void shouldValidateRequiredPhoneField_TC026() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();
        request.setClienteTelefono(" ");

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createOrder(request));

        // Assert
        assertTrue(ex.getMessage().contains("teléfono"));
    }

    // HU8 - Confirmar pedido
    @Test
    @DisplayName("TC-027 - Coordenadas fuera de rango son rechazadas")
    void shouldRejectOutOfRangeCoordinates_TC027() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();
        request.setClienteCoordenadasX(-200.0);

        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createOrder(request));

        // Assert
        assertTrue(ex.getMessage().contains("coordenadas"));
    }

    // HU8 - Confirmar pedido
    @Test
    @DisplayName("TC-025 - Pedido confirmado sin candidatos queda PENDIENTE y sin ETA")
    void shouldRemainPendingWhenNoDeliveryCandidates_TC025() {
        // Arrange
        stubRestauranteExistente();
        OrderRequestDto request = createValidRequest();

        Pedido savedPending = Pedido.builder()
                .id(203L)
                .restauranteId(request.getRestauranteId())
                .clienteNombre(request.getClienteNombre())
                .clienteCoordenadasX(request.getClienteCoordenadasX())
                .clienteCoordenadasY(request.getClienteCoordenadasY())
                .productos(List.of(
                        ProductoPedido.builder().id(1L).nombre("Hamburguesa").precio(new BigDecimal("8.50")).build()
                ))
                .estado(EstadoPedido.PENDIENTE)
                .build();

        when(pedidoRepository.save(any())).thenReturn(savedPending);
        when(deliveryClient.assign(any()))
                .thenReturn(new DeliveryAssignmentResponse(203L, "PENDIENTE", null, null, null));

        // Act
        OrderResponseDto response = service.createOrder(request);

        // Assert
        assertEquals(EstadoPedido.PENDIENTE, response.getEstado());
        assertEquals(0, response.getTiempoEstimado());
    }

    // HU6 - Actualizar estado del repartidor / Pedido entregado
    @Test
    @DisplayName("TC-018 - Marcar pedido como entregado libera al repartidor")
    void shouldMarkOrderDeliveredAndReleaseCourier_TC018() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .id(300L)
                .restauranteId(10L)
                .repartidorId(55L)
                .clienteId(77L)
                .clienteNombre("Ana Garcia")
                .clienteCoordenadasX(10.0)
                .clienteCoordenadasY(20.0)
                .productos(List.of())
                .estado(EstadoPedido.ASIGNADO)
                .build();

        when(pedidoRepository.findById(300L)).thenReturn(java.util.Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = service.deliverOrder(300L);

        // Assert
        assertEquals(EstadoPedido.ENTREGADO, response.getEstado());
        verify(deliveryClient).releaseRepartidor(55L, "ENTREGADO");
    }

    // HU9 - Cancelar pedido
    @Test
    @DisplayName("TC-028 - Pedido PENDIENTE se cancela correctamente")
    void shouldCancelPendingOrder_TC028() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .id(400L)
                .restauranteId(10L)
                .repartidorId(null)
                .clienteId(77L)
                .clienteNombre("Ana Garcia")
                .clienteCoordenadasX(10.0)
                .clienteCoordenadasY(20.0)
                .productos(List.of())
                .estado(EstadoPedido.PENDIENTE)
                .build();

        when(pedidoRepository.findById(400L)).thenReturn(java.util.Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = service.cancelOrder(400L);

        // Assert
        assertEquals(EstadoPedido.CANCELADO, response.getEstado());
        verify(deliveryClient, never()).releaseRepartidor(anyLong(), anyString());
    }

    // HU9 - Cancelar pedido
    @Test
    @DisplayName("TC-029 - Pedido ASIGNADO cancela y libera repartidor")
    void shouldCancelAssignedOrderAndReleaseCourier_TC029() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .id(401L)
                .restauranteId(10L)
                .repartidorId(55L)
                .clienteId(77L)
                .clienteNombre("Ana Garcia")
                .clienteCoordenadasX(10.0)
                .clienteCoordenadasY(20.0)
                .productos(List.of())
                .estado(EstadoPedido.ASIGNADO)
                .build();

        when(pedidoRepository.findById(401L)).thenReturn(java.util.Optional.of(pedido));
        when(pedidoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = service.cancelOrder(401L);

        // Assert
        assertEquals(EstadoPedido.CANCELADO, response.getEstado());
        verify(deliveryClient).releaseRepartidor(55L, "CANCELADO");
    }

    // HU9 - Cancelar pedido
    @Test
    @DisplayName("TC-030 - Pedido ENTREGADO no se puede cancelar")
    void shouldRejectCancellationForDeliveredOrder_TC030() {
        // Arrange
        Pedido pedido = Pedido.builder()
                .id(402L)
                .restauranteId(10L)
                .repartidorId(55L)
                .clienteId(77L)
                .clienteNombre("Ana Garcia")
                .clienteCoordenadasX(10.0)
                .clienteCoordenadasY(20.0)
                .productos(List.of())
                .estado(EstadoPedido.ENTREGADO)
                .build();

        when(pedidoRepository.findById(402L)).thenReturn(java.util.Optional.of(pedido));

        // Act + Assert
        org.junit.jupiter.api.Assertions.assertThrows(PedidoCancelException.class, () -> service.cancelOrder(402L));
    }

    private void stubRestauranteExistente() {
        when(restauranteRepository.existsById(anyLong())).thenReturn(true);
    }

    private OrderRequestDto createValidRequest() {
        return OrderRequestDto.builder()
                .restauranteId(10L)
                .restauranteX(5.0)
                .restauranteY(8.0)
                .clima("SOLEADO")
                .clienteId(77L)
                .clienteNombre("Ana Garcia")
                .clienteTelefono("3001234567")
                .clienteCoordenadasX(10.0)
                .clienteCoordenadasY(20.0)
                .productos(List.of(createProduct(1L, "Hamburguesa", "8.50")))
                .build();
    }

    private ProductoPedidoDto createProduct(Long id, String nombre, String precio) {
        return ProductoPedidoDto.builder()
                .id(id)
                .nombre(nombre)
                .precio(new BigDecimal(precio))
                .build();
    }
}
