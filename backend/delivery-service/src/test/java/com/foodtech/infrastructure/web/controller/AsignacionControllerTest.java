package com.foodtech.infrastructure.web.controller;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.service.AsignacionService;
import com.foodtech.domain.port.input.AsignacionUseCase;
import com.foodtech.infrastructure.web.dto.AsignacionRequestDTO;
import com.foodtech.infrastructure.web.dto.AsignacionResponseDTO;
import com.foodtech.infrastructure.web.dto.RepartidorListResponseDto;
import com.foodtech.infrastructure.web.dto.RepartidorResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignacionControllerTest {

        @Mock
        private AsignacionUseCase asignacionUseCase;

        @Mock
        private com.foodtech.application.service.AsignacionApplicationService asignacionApplicationService;

        @Mock
        private com.foodtech.domain.port.input.RepartidorUseCase repartidorUseCase;

        @Mock
        private com.foodtech.domain.service.AsignacionService asignacionService;

    @InjectMocks
    private AsignacionController controller;

    @Test
    void debeRetornarASIGNADO_cuandoHayCandidatos() {
        AsignacionRequestDTO request = AsignacionRequestDTO.builder()
                .pedidoId(1L)
                                .restauranteX(25.5)
                                .restauranteY(40.25)
                .clima(Clima.SOLEADO.name())
                .build();

        Repartidor candidato = Repartidor.builder()
                .id(1L)
                .nombre("Carlos Mendoza")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.MOTO)
                .ubicacion(new Coordenada(25.5, 40.25))
                .build();

        when(asignacionApplicationService.asignarRepartidor(eq(new Coordenada(25.5, 40.25)), eq(Clima.SOLEADO)))
                .thenReturn(candidato);
        when(asignacionService.calcularTiempoEstimadoMinutos(any(), any())).thenReturn(15);

        AsignacionResponseDTO resp = controller.asignarRepartidor(request);

        assertThat(resp).isNotNull();
        assertThat(resp.getEstado()).isEqualTo("ASIGNADO");
        assertThat(resp.getRepartidorId()).isEqualTo(1L);
        assertThat(resp.getNombreRepartidor()).isEqualTo("Carlos Mendoza");
    }

    @Test
    void debeRetornarPENDIENTE_cuandoNoHayCandidatos() {
        AsignacionRequestDTO request = AsignacionRequestDTO.builder()
                .pedidoId(2L)
                .restauranteX(-10.5)
                .restauranteY(10.75)
                .clima(Clima.LLUVIA_SUAVE.name())
                .build();

        when(asignacionApplicationService.asignarRepartidor(eq(new Coordenada(-10.5, 10.75)), eq(Clima.LLUVIA_SUAVE)))
                .thenReturn(null);

        AsignacionResponseDTO resp = controller.asignarRepartidor(request);

        assertThat(resp).isNotNull();
        assertThat(resp.getEstado()).isEqualTo("PENDIENTE");
        assertThat(resp.getRepartidorId()).isNull();
        assertThat(resp.getNombreRepartidor()).isNull();
    }

    @Test
    void debeLlamarUseCaseConClimaNull_cuandoRequestNoTieneClima() {
        AsignacionRequestDTO request = AsignacionRequestDTO.builder()
                .pedidoId(3L)
                .restauranteX(5.25)
                .restauranteY(-5.25)
                .clima(null)
                .build();

        when(asignacionApplicationService.asignarRepartidor(eq(new Coordenada(5.25, -5.25)), isNull()))
                .thenReturn(null);

        AsignacionResponseDTO resp = controller.asignarRepartidor(request);

        assertThat(resp.getEstado()).isEqualTo("PENDIENTE");
        verify(asignacionApplicationService).asignarRepartidor(eq(new Coordenada(5.25, -5.25)), isNull());
    }

    @Test
    void updateEstado_cuandoEventoValido_retorna200ConDto() {
        Repartidor actualizado = Repartidor.builder()
                .id(5L).nombre("Diego").estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(1.0, 2.0)).build();

        when(asignacionApplicationService.procesarEventoRepartidor(5L, "ENTREGADO"))
                .thenReturn(actualizado);

        EstadoUpdateRequest request = new EstadoUpdateRequest("ENTREGADO");
        ResponseEntity<?> response = controller.updateEstado(5L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        RepartidorResponseDTO body = (RepartidorResponseDTO) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(5L);
        assertThat(body.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    void getAllRepartidores_retornaListaYStatus200() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(1L).nombre("Ana").estado("ACTIVO").build();

        when(asignacionApplicationService.getAllRepartidores()).thenReturn(List.of(dto));

        ResponseEntity<List<RepartidorListResponseDto>> response = controller.getAllRepartidores();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void updateEstado_cuandoRepartidorConCamposNulos_retorna200ConNulos() {
        Repartidor actualizado = Repartidor.builder()
                .id(7L).nombre("Diego").estado(null)
                .vehiculo(null).ubicacion(null).build();

        when(asignacionApplicationService.procesarEventoRepartidor(7L, "ENTREGADO"))
                .thenReturn(actualizado);

        EstadoUpdateRequest request = new EstadoUpdateRequest("ENTREGADO");
        ResponseEntity<?> response = controller.updateEstado(7L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        RepartidorResponseDTO body = (RepartidorResponseDTO) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getEstado()).isNull();
        assertThat(body.getVehiculo()).isNull();
        assertThat(body.getX()).isNull();
        assertThat(body.getY()).isNull();
    }
}
