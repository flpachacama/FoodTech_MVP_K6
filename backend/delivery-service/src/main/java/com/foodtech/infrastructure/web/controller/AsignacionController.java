package com.foodtech.infrastructure.web.controller;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.port.input.RepartidorUseCase;
import com.foodtech.infrastructure.web.dto.AsignacionRequestDTO;
import com.foodtech.infrastructure.web.dto.AsignacionResponseDTO;
import com.foodtech.infrastructure.web.dto.RepartidorListResponseDto;
import com.foodtech.infrastructure.web.dto.RepartidorResponseDTO;
import com.foodtech.application.service.AsignacionApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionApplicationService asignacionApplicationService;
    private final RepartidorUseCase repartidorUseCase;
    private final com.foodtech.domain.service.AsignacionService asignacionService;

    @PostMapping
    public AsignacionResponseDTO asignarRepartidor(@Valid @RequestBody AsignacionRequestDTO request) {
        Coordenada coordenada = new Coordenada(request.getRestauranteX(), request.getRestauranteY());
        Clima clima = request.getClima() != null ? Clima.valueOf(request.getClima()) : null;

        Repartidor asignado = asignacionApplicationService.asignarRepartidor(coordenada, clima);

        if (asignado == null) {
            return AsignacionResponseDTO.builder()
                    .pedidoId(request.getPedidoId())
                    .estado("PENDIENTE")
                    .repartidorId(null)
                    .nombreRepartidor(null)
                    .tiempoEstimado(null)
                    .build();
        }

        Integer tiempoEstimado = asignacionService.calcularTiempoEstimadoMinutos(asignado, coordenada);

        return AsignacionResponseDTO.builder()
                .pedidoId(request.getPedidoId())
                .estado("ASIGNADO")
                .repartidorId(asignado.getId())
                .nombreRepartidor(asignado.getNombre())
                .tiempoEstimado(tiempoEstimado)
                .build();
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<?> updateEstado(@PathVariable("id") Long id, @RequestBody EstadoUpdateRequest request) {
        String evento = request == null ? null : request.evento();

        Repartidor actualizado = asignacionApplicationService.procesarEventoRepartidor(id, evento);

        RepartidorResponseDTO dto = RepartidorResponseDTO.builder()
                .id(actualizado.getId())
                .nombre(actualizado.getNombre())
                .estado(actualizado.getEstado() != null ? actualizado.getEstado().name() : null)
                .vehiculo(actualizado.getVehiculo() != null ? actualizado.getVehiculo().name() : null)
                .x(actualizado.getUbicacion() != null ? actualizado.getUbicacion().x() : null)
                .y(actualizado.getUbicacion() != null ? actualizado.getUbicacion().y() : null)
                .build();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/fooders")
    public ResponseEntity<List<RepartidorListResponseDto>> getAllRepartidores() {
        List<RepartidorListResponseDto> repartidores = asignacionApplicationService.getAllRepartidores();
        return ResponseEntity.ok(repartidores);
    }
}
