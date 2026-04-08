package com.foodtech.infrastructure.persistence.entity;

import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.TipoVehiculo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que mapea la tabla repartidores.
 */
@Entity
@Table(name = "repartidores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRepartidor estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVehiculo vehiculo;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;
}
