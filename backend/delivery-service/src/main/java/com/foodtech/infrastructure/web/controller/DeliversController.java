package com.foodtech.infrastructure.web.controller;

import com.foodtech.application.service.AsignacionApplicationService;
import com.foodtech.infrastructure.web.dto.RepartidorListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/delivers")
@RequiredArgsConstructor
public class DeliversController {

    private final AsignacionApplicationService asignacionApplicationService;

    @GetMapping
    public ResponseEntity<List<RepartidorListResponseDto>> getAllDelivers() {
        List<RepartidorListResponseDto> repartidores = asignacionApplicationService.getAllRepartidores();
        return ResponseEntity.ok(repartidores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorListResponseDto> getDeliverById(@PathVariable Long id) {
        return asignacionApplicationService.getRepartidorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
