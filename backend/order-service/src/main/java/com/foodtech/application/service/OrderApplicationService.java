package com.foodtech.application.service;

import com.foodtech.domain.exception.PedidoCancelException;
import com.foodtech.domain.exception.PedidoDeliverException;
import com.foodtech.domain.exception.PedidoNotFoundException;
import com.foodtech.domain.exception.RestauranteNotFoundException;
import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.domain.model.Pedido;
import com.foodtech.domain.model.ProductoPedido;
import com.foodtech.domain.port.input.OrderUseCase;
import com.foodtech.domain.port.output.DeliveryClient;
import com.foodtech.domain.port.output.DeliveryClient.DeliveryAssignmentRequest;
import com.foodtech.domain.port.output.DeliveryClient.DeliveryAssignmentResponse;
import com.foodtech.domain.port.output.PedidoRepository;
import com.foodtech.domain.service.TiempoDeliveryCalculator;
import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.infrastructure.web.dto.CancelOrderResponseDto;
import com.foodtech.infrastructure.web.dto.DeliverOrderResponseDto;
import com.foodtech.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.infrastructure.web.dto.OrderResponseDto;
import com.foodtech.infrastructure.web.dto.ProductoPedidoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderApplicationService implements OrderUseCase {

    private final PedidoRepository pedidoRepository;
    private final DeliveryClient deliveryClient;
    private final RestauranteJpaRepository restauranteRepository;
    private final TiempoDeliveryCalculator tiempoCalculator;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto request) {

        validateRequest(request);
        log.info("[createOrder] Creando pedido para cliente='{}' restauranteId={}",
                request.getClienteNombre(), request.getRestauranteId());

        Pedido pedido = toDomain(request);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.info("[createOrder] Pedido persistido con id={}", pedidoGuardado.getId());

        DeliveryAssignmentResponse deliveryResponse;
        try {
            DeliveryAssignmentRequest deliveryRequest = new DeliveryAssignmentRequest(
                    pedidoGuardado.getId(),
                    request.getRestauranteX(),
                    request.getRestauranteY(),
                    request.getClima() != null ? request.getClima() : "SOLEADO"
            );
            deliveryResponse = deliveryClient.assign(deliveryRequest);
            log.info("[createOrder] Respuesta delivery: estado={} repartidorId={}",
                    deliveryResponse.estado(), deliveryResponse.repartidorId());
        } catch (Exception ex) {
            log.error("[createOrder] Fallo al llamar delivery-service: {}", ex.getMessage());
            throw new IllegalStateException(
                    "Error al comunicarse con el servicio de delivery: " + ex.getMessage(), ex
            );
        }

        EstadoPedido estadoFinal = resolveEstado(deliveryResponse.estado());

        int tiempoRestauranteCliente = tiempoCalculator.calcularMinutos(
                request.getRestauranteX(), request.getRestauranteY(),
                request.getClienteCoordenadasX(), request.getClienteCoordenadasY());
        int tiempoTotal = (deliveryResponse.tiempoEstimado() != null ? deliveryResponse.tiempoEstimado() : 0)
                + tiempoRestauranteCliente;
        log.info("[createOrder] tiempoRepartidorRestaurante={} min, tiempoRestauranteCliente={} min, tiempoTotal={} min",
                deliveryResponse.tiempoEstimado(), tiempoRestauranteCliente, tiempoTotal);

        Pedido pedidoActualizado = Pedido.builder()
                .id(pedidoGuardado.getId())
                .restauranteId(pedidoGuardado.getRestauranteId())
                .repartidorId(deliveryResponse.repartidorId())
                .clienteId(pedidoGuardado.getClienteId())
                .clienteNombre(pedidoGuardado.getClienteNombre())
                .clienteCoordenadasX(pedidoGuardado.getClienteCoordenadasX())
                .clienteCoordenadasY(pedidoGuardado.getClienteCoordenadasY())
                .productos(pedidoGuardado.getProductos())
                .estado(estadoFinal)
                .tiempoEstimado(tiempoTotal)
                .build();

        pedidoRepository.save(pedidoActualizado);

        return toResponse(pedidoActualizado, request);
    }

    private void validateRequest(OrderRequestDto request) {
        if (request.getRestauranteId() == null) {
            throw new IllegalArgumentException("El restauranteId es obligatorio");
        }

        if (!restauranteRepository.existsById(request.getRestauranteId())) {
            throw new RestauranteNotFoundException(request.getRestauranteId());
        }
        
        if (request.getClienteNombre() == null || request.getClienteNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (request.getClienteTelefono() == null || request.getClienteTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono del cliente es obligatorio");
        }
        if (request.getClienteCoordenadasX() == null || request.getClienteCoordenadasY() == null) {
            throw new IllegalArgumentException("Las coordenadas del cliente son obligatorias");
        }
        if (!isValidCoordinate(request.getClienteCoordenadasX()) || !isValidCoordinate(request.getClienteCoordenadasY())) {
            throw new IllegalArgumentException("Las coordenadas del cliente son inválidas");
        }
        if (request.getProductos() == null || request.getProductos().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe incluir al menos un producto");
        }
        if (request.getProductos().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("El pedido contiene productos inválidos");
        }
    }

    private boolean isValidCoordinate(Double coordinate) {
        return coordinate >= -180 && coordinate <= 180;
    }

    private Pedido toDomain(OrderRequestDto request) {
        List<ProductoPedido> productos = request.getProductos().stream()
                .map(dto -> ProductoPedido.builder()
                        .id(dto.getId())
                        .nombre(dto.getNombre())
                        .precio(dto.getPrecio())
                        .build())
                .toList();

        return Pedido.builder()
                .restauranteId(request.getRestauranteId())
                .clienteId(request.getClienteId())
                .clienteNombre(request.getClienteNombre())
                .clienteCoordenadasX(request.getClienteCoordenadasX())
                .clienteCoordenadasY(request.getClienteCoordenadasY())
                .productos(productos)
                .estado(EstadoPedido.PENDIENTE)
                .build();
    }

    @Override
    public OrderResponseDto getOrderByRepartidorId(Long repartidorId) {
        log.info("[getOrderByRepartidorId] Buscando pedido activo para repartidorId={}", repartidorId);
        Pedido pedido = pedidoRepository.findPedidoActivoByRepartidorId(repartidorId)
                .orElseThrow(() -> {
                    log.warn("[getOrderByRepartidorId] Sin pedido activo para repartidorId={}", repartidorId);
                    return new PedidoNotFoundException(repartidorId);
                });
        return toPedidoResponse(pedido);
    }

    private OrderResponseDto toPedidoResponse(Pedido pedido) {
        List<ProductoPedidoDto> productosDto = pedido.getProductos().stream()
                .map(p -> ProductoPedidoDto.builder()
                        .id(p.getId())
                        .nombre(p.getNombre())
                        .precio(p.getPrecio())
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .id(pedido.getId())
                .restauranteId(pedido.getRestauranteId())
                .repartidorId(pedido.getRepartidorId())
                .clienteId(pedido.getClienteId())
                .clienteNombre(pedido.getClienteNombre())
                .clienteCoordenadasX(pedido.getClienteCoordenadasX())
                .clienteCoordenadasY(pedido.getClienteCoordenadasY())
                .clienteTelefono(null)   // clienteTelefono no se persiste en BD
                .productos(productosDto)
                .estado(pedido.getEstado())
                .tiempoEstimado(pedido.getTiempoEstimado())
                .build();
    }

    private EstadoPedido resolveEstado(String estadoDelivery) {
        if ("ASIGNADO".equalsIgnoreCase(estadoDelivery)) {
            return EstadoPedido.ASIGNADO;
        }
        return EstadoPedido.PENDIENTE;
    }

    private OrderResponseDto toResponse(Pedido pedido, OrderRequestDto request) {
        List<ProductoPedidoDto> productosDto = pedido.getProductos().stream()
                .map(p -> ProductoPedidoDto.builder()
                        .id(p.getId())
                        .nombre(p.getNombre())
                        .precio(p.getPrecio())
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .id(pedido.getId())
                .restauranteId(pedido.getRestauranteId())
                .repartidorId(pedido.getRepartidorId())
                .clienteId(pedido.getClienteId())
                .clienteNombre(pedido.getClienteNombre())
                .clienteCoordenadasX(request.getClienteCoordenadasX())
                .clienteCoordenadasY(request.getClienteCoordenadasY())
                .clienteTelefono(request.getClienteTelefono())
                .productos(productosDto)
                .estado(pedido.getEstado())
                .tiempoEstimado(pedido.getTiempoEstimado())
                .build();
    }

    @Override
    public CancelOrderResponseDto cancelOrder(Long pedidoId) {
        log.info("[cancelOrder] Iniciando cancelación del pedido id={}", pedidoId);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> {
                    log.error("[cancelOrder] Pedido no encontrado id={}", pedidoId);
                    return new PedidoNotFoundException(pedidoId);
                });

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            log.error("[cancelOrder] No se puede cancelar pedido {} - ya está ENTREGADO", pedidoId);
            throw new PedidoCancelException(pedidoId, "el pedido ya ha sido entregado");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            log.error("[cancelOrder] No se puede cancelar pedido {} - ya está CANCELADO", pedidoId);
            throw new PedidoCancelException(pedidoId, "el pedido ya fue cancelado anteriormente");
        }

        if (pedido.getRepartidorId() != null) {
            log.info("[cancelOrder] Liberando repartidor id={}", pedido.getRepartidorId());
            deliveryClient.releaseRepartidor(pedido.getRepartidorId(), "CANCELADO");
        }

        Pedido pedidoCancelado = Pedido.builder()
                .id(pedido.getId())
                .restauranteId(pedido.getRestauranteId())
                .repartidorId(null)
                .clienteId(pedido.getClienteId())
                .clienteNombre(pedido.getClienteNombre())
                .clienteCoordenadasX(pedido.getClienteCoordenadasX())
                .clienteCoordenadasY(pedido.getClienteCoordenadasY())
                .productos(pedido.getProductos())
                .estado(EstadoPedido.CANCELADO)
                .tiempoEstimado(null)
                .build();

        pedidoRepository.save(pedidoCancelado);
        log.info("[cancelOrder] Pedido {} cancelado exitosamente", pedidoId);

        return CancelOrderResponseDto.builder()
                .id(pedidoCancelado.getId())
                .estado(pedidoCancelado.getEstado())
                .mensaje("Pedido cancelado exitosamente")
                .build();
    }

    @Override
    public DeliverOrderResponseDto deliverOrder(Long pedidoId) {
        log.info("[deliverOrder] Iniciando marcado como entregado del pedido id={}", pedidoId);

        // ── 1. Buscar pedido ─────────────────────────────────────────────────────────
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> {
                    log.error("[deliverOrder] Pedido no encontrado id={}", pedidoId);
                    return new PedidoNotFoundException(pedidoId);
                });

        // ── 2. Validar que pueda ser marcado como entregado ──────────────────────────
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            log.error("[deliverOrder] No se puede marcar pedido {} - ya está ENTREGADO", pedidoId);
            throw new PedidoDeliverException(pedidoId, "el pedido ya ha sido entregado");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            log.error("[deliverOrder] No se puede marcar pedido {} - está CANCELADO", pedidoId);
            throw new PedidoDeliverException(pedidoId, "el pedido fue cancelado");
        }
        if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
            log.error("[deliverOrder] No se puede marcar pedido {} - está PENDIENTE sin repartidor", pedidoId);
            throw new PedidoDeliverException(pedidoId, "el pedido no tiene repartidor asignado");
        }

        // ── 3. Liberar repartidor ────────────────────────────────────────────────────
        if (pedido.getRepartidorId() != null) {
            log.info("[deliverOrder] Liberando repartidor id={}", pedido.getRepartidorId());
            deliveryClient.releaseRepartidor(pedido.getRepartidorId(), "ENTREGADO");
        }

        // ── 4. Actualizar pedido a ENTREGADO ─────────────────────────────────────────
        Pedido pedidoEntregado = Pedido.builder()
                .id(pedido.getId())
                .restauranteId(pedido.getRestauranteId())
                .repartidorId(pedido.getRepartidorId())
                .clienteId(pedido.getClienteId())
                .clienteNombre(pedido.getClienteNombre())
                .clienteCoordenadasX(pedido.getClienteCoordenadasX())
                .clienteCoordenadasY(pedido.getClienteCoordenadasY())
                .productos(pedido.getProductos())
                .estado(EstadoPedido.ENTREGADO)
                .tiempoEstimado(null)
                .build();

        pedidoRepository.save(pedidoEntregado);
        log.info("[deliverOrder] Pedido {} marcado como ENTREGADO exitosamente", pedidoId);

        // ── 5. Retornar respuesta ────────────────────────────────────────────────────
        return DeliverOrderResponseDto.builder()
                .id(pedidoEntregado.getId())
                .estado(pedidoEntregado.getEstado())
                .mensaje("Pedido marcado como entregado exitosamente")
                .build();
    }
}
