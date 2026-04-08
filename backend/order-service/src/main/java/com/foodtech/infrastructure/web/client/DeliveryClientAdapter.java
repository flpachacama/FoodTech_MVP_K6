package com.foodtech.infrastructure.web.client;

import com.foodtech.domain.port.output.DeliveryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryClientAdapter implements DeliveryClient {

    private final RestTemplate restTemplate;

    @Value("${delivery.service.url:http://localhost:8080}")
    private String deliveryServiceUrl;

    @Override
    public DeliveryAssignmentResponse assign(DeliveryAssignmentRequest request) {
        String url = deliveryServiceUrl + "/delivery";
        try {
            DeliveryAssignmentResponse response = restTemplate.postForObject(
                    url, request, DeliveryAssignmentResponse.class);
            if (response == null) {
                throw new RuntimeException("El servicio de delivery no devolvió respuesta");
            }
            return response;
        } catch (RestClientResponseException e) {
            throw new RuntimeException(
                    "Error en delivery-service [" + e.getStatusCode() + "]: " + e.getResponseBodyAsString(), e);
        }
    }

    @Override
    public void releaseRepartidor(Long repartidorId, String evento) {
        String url = deliveryServiceUrl + "/delivery/" + repartidorId + "/state";
        Map<String, String> requestBody = Map.of("evento", evento);
        
        log.info("Liberando repartidor {} con evento {}", repartidorId, evento);
        
        try {
            restTemplate.put(url, requestBody);
            log.info("Repartidor {} liberado exitosamente con evento {}", repartidorId, evento);
        } catch (RestClientResponseException e) {
            log.error("Error al liberar repartidor {} - Status: {}, Body: {}", 
                    repartidorId, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error inesperado al liberar repartidor {}: {}", 
                    repartidorId, e.getMessage());
        }
    }
}
