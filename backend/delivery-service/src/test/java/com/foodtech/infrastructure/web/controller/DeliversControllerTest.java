package com.foodtech.infrastructure.web.controller;

import com.foodtech.application.service.AsignacionApplicationService;
import com.foodtech.infrastructure.web.dto.RepartidorListResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliversControllerTest {

    @Mock
    private AsignacionApplicationService asignacionApplicationService;

    @InjectMocks
    private DeliversController controller;

    @Test
    void getAllDelivers_retornaListaYStatus200() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(1L).nombre("Carlos").estado("ACTIVO").vehiculo("MOTO")
                .ubicacionX(10.0).ubicacionY(20.0).build();

        when(asignacionApplicationService.getAllRepartidores()).thenReturn(List.of(dto));

        ResponseEntity<List<RepartidorListResponseDto>> response = controller.getAllDelivers();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getNombre()).isEqualTo("Carlos");
    }

    @Test
    void getDeliverById_cuandoExiste_retorna200() {
        RepartidorListResponseDto dto = RepartidorListResponseDto.builder()
                .id(2L).nombre("Ana").estado("EN_ENTREGA").vehiculo("BICICLETA")
                .ubicacionX(5.0).ubicacionY(8.0).build();

        when(asignacionApplicationService.getRepartidorById(2L)).thenReturn(Optional.of(dto));

        ResponseEntity<RepartidorListResponseDto> response = controller.getDeliverById(2L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getNombre()).isEqualTo("Ana");
    }

    @Test
    void getDeliverById_cuandoNoExiste_retorna404() {
        when(asignacionApplicationService.getRepartidorById(99L)).thenReturn(Optional.empty());

        ResponseEntity<RepartidorListResponseDto> response = controller.getDeliverById(99L);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }
}
