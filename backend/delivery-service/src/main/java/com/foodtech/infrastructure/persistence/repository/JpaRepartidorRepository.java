package com.foodtech.infrastructure.persistence.repository;

import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.infrastructure.persistence.entity.RepartidorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para RepartidorEntity.
 */
@Repository
public interface JpaRepartidorRepository extends JpaRepository<RepartidorEntity, Long> {

    List<RepartidorEntity> findByEstado(EstadoRepartidor estado);
}
