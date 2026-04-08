package com.foodtech.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.domain.model.Pedido;
import com.foodtech.domain.model.ProductoPedido;
import com.foodtech.domain.port.output.PedidoRepository;
import com.foodtech.infrastructure.persistence.PedidoJpaRepository;
import com.foodtech.infrastructure.persistence.entity.PedidoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PedidoDataAdapter implements PedidoRepository {

    private final PedidoJpaRepository pedidoJpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity entity = toEntity(pedido);
        PedidoEntity saved = pedidoJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return pedidoJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Pedido> findPedidoActivoByRepartidorId(Long repartidorId) {
        return pedidoJpaRepository
                .findFirstByRepartidorIdAndEstadoIn(
                        repartidorId,
                        List.of(EstadoPedido.ASIGNADO))
                .map(this::toDomain);
    }

    // ─── Mappers ────────────────────────────────────────────────────────────────

    private PedidoEntity toEntity(Pedido pedido) {
        return PedidoEntity.builder()
                .id(pedido.getId())
                .estado(pedido.getEstado())
                .restauranteId(pedido.getRestauranteId())
                .repartidorId(pedido.getRepartidorId())
                .clienteId(pedido.getClienteId())
                .clienteNombre(pedido.getClienteNombre())
                .clienteCoordenadasX(pedido.getClienteCoordenadasX())
                .clienteCoordenadasY(pedido.getClienteCoordenadasY())
                .tiempoEstimado(pedido.getTiempoEstimado())
                .productos(serializeProductos(pedido.getProductos()))
                .build();
    }

    private Pedido toDomain(PedidoEntity entity) {
        return Pedido.builder()
                .id(entity.getId())
                .estado(entity.getEstado())
                .restauranteId(entity.getRestauranteId())
                .repartidorId(entity.getRepartidorId())
                .clienteId(entity.getClienteId())
                .clienteNombre(entity.getClienteNombre())
                .clienteCoordenadasX(entity.getClienteCoordenadasX())
                .clienteCoordenadasY(entity.getClienteCoordenadasY())
                .tiempoEstimado(entity.getTiempoEstimado())
                .productos(deserializeProductos(entity.getProductos()))
                .build();
    }

    private String serializeProductos(List<ProductoPedido> productos) {
        if (productos == null) return "[]";
        try {
            return objectMapper.writeValueAsString(productos);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializando productos del pedido", e);
        }
    }

    private List<ProductoPedido> deserializeProductos(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<ProductoPedido>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error deserializando productos del pedido", e);
        }
    }
}
