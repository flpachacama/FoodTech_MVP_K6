package com.foodtech.infrastructure.web.controller;

import com.foodtech.application.service.RestauranteService;
import com.foodtech.infrastructure.web.dto.RestauranteResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;

    @GetMapping
    public ResponseEntity<List<RestauranteResponseDto>> getAllRestaurantes() {
        List<RestauranteResponseDto> restaurantes = restauranteService.getAllRestaurantes();
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDto> getRestauranteById(@PathVariable Long id) {
        RestauranteResponseDto restaurante = restauranteService.getRestauranteById(id);
        return ResponseEntity.ok(restaurante);
    }
}
