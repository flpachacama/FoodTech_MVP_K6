package com.foodtech.infrastructure.persistence.entity;

import com.foodtech.domain.model.EstadoPedido;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Column(name = "restaurante_id", nullable = false)
    private Long restauranteId;

    @Column(name = "repartidor_id")
    private Long repartidorId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "cliente_nombre", nullable = false)
    private String clienteNombre;

    @Column(name = "cliente_coordenadas_x")
    private Double clienteCoordenadasX;

    @Column(name = "cliente_coordenadas_y")
    private Double clienteCoordenadasY;

    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;

    /**
     * Lista de productos serializada como JSON.
     * Se usa @Column(columnDefinition) para almacenar el JSON en DB.
     */
    @Column(name = "productos", columnDefinition = "TEXT")
    private String productos;
}
