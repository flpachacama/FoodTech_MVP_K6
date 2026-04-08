package com.foodtech.domain.port.output;

import com.foodtech.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {

    Pedido save(Pedido pedido);

    Optional<Pedido> findById(Long id);

    List<Pedido> findAll();

    Optional<Pedido> findPedidoActivoByRepartidorId(Long repartidorId);

}
