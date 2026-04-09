package com.foodtech.domain.port.output;

public interface DeliveryClient {

    DeliveryAssignmentResponse assign(DeliveryAssignmentRequest request);

    void releaseRepartidor(Long repartidorId, String evento);

    record DeliveryAssignmentRequest(
            Long pedidoId,
            Double restauranteX,
            Double restauranteY,
            String clima
    ) {}

    record DeliveryAssignmentResponse(
            Long pedidoId,
            String estado,
            Long repartidorId,
            String nombreRepartidor,
            Integer tiempoEstimado
    ) {}
}
