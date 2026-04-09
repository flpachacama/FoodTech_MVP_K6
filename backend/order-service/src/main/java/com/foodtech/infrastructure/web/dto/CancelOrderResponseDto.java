package com.foodtech.infrastructure.web.dto;

import com.foodtech.domain.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderResponseDto {

    private Long id;
    private EstadoPedido estado;
    private String mensaje;
}
