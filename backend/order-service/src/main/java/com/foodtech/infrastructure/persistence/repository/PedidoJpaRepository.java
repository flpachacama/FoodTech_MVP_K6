package com.foodtech.infrastructure.persistence;

import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {

    Optional<PedidoEntity> findFirstByRepartidorIdAndEstadoIn(Long repartidorId, List<EstadoPedido> estados);
}
