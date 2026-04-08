package com.foodtech.application.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.port.input.AsignacionUseCase;
import com.foodtech.domain.port.output.RepartidorRepository;
import com.foodtech.domain.service.AsignacionService;
import com.foodtech.domain.port.input.RepartidorUseCase;
import com.foodtech.infrastructure.web.dto.RepartidorListResponseDto;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AsignacionApplicationService implements AsignacionUseCase {

    private final RepartidorRepository repartidorRepository;
    private final AsignacionService asignacionService;
    private final RepartidorUseCase repartidorUseCase;

    public AsignacionApplicationService(RepartidorRepository repartidorRepository,
                                       AsignacionService asignacionService,
                                       RepartidorUseCase repartidorUseCase) {
        this.repartidorRepository = Objects.requireNonNull(repartidorRepository);
        this.asignacionService = Objects.requireNonNull(asignacionService);
        this.repartidorUseCase = Objects.requireNonNull(repartidorUseCase);
    }

    @Override
    public List<Repartidor> obtenerRepartidoresPriorizados(Coordenada restauranteUbicacion, Clima clima) {
        List<Repartidor> activos = repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO);
        
        if (activos == null || activos.isEmpty()) {
            return Collections.emptyList();
        }
        return asignacionService.priorizarRepartidores(activos, restauranteUbicacion, clima);
    }

    public Repartidor asignarRepartidor(Coordenada restauranteUbicacion, Clima clima) {
        List<Repartidor> activos = repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO);
        if (activos == null || activos.isEmpty()) {
            return null;
        }

        List<Repartidor> priorizados = asignacionService.priorizarRepartidores(activos, restauranteUbicacion, clima);
        if (priorizados == null || priorizados.isEmpty()) {
            return null;
        }

        Repartidor candidato = priorizados.get(0);
        return repartidorUseCase.cambiarEstado(candidato.getId(), EstadoRepartidor.EN_ENTREGA);
    }

    public Repartidor procesarEventoRepartidor(Long repartidorId, String evento) {
        EstadoRepartidor nuevoEstado = EventMapper.mapToEstado(evento);
        return repartidorUseCase.cambiarEstado(repartidorId, nuevoEstado);
    }

    public List<RepartidorListResponseDto> getAllRepartidores() {
        return repartidorRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<RepartidorListResponseDto> getRepartidorById(Long id) {
        return repartidorRepository.findById(id)
                .map(this::toDto);
    }

    private RepartidorListResponseDto toDto(com.foodtech.domain.model.Repartidor r) {
        return RepartidorListResponseDto.builder()
                .id(r.getId())
                .nombre(r.getNombre())
                .estado(r.getEstado() != null ? r.getEstado().name() : null)
                .vehiculo(r.getVehiculo() != null ? r.getVehiculo().name() : null)
                .ubicacionX(r.getUbicacion() != null ? r.getUbicacion().x() : null)
                .ubicacionY(r.getUbicacion() != null ? r.getUbicacion().y() : null)
                .build();
    }
}
