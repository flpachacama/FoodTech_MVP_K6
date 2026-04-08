package com.foodtech.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.domain.exception.RestauranteNotFoundException;
import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.infrastructure.persistence.entity.RestauranteEntity;
import com.foodtech.infrastructure.web.dto.ProductoMenuDto;
import com.foodtech.infrastructure.web.dto.RestauranteResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestauranteService {

    private final RestauranteJpaRepository restauranteRepository;
    private final ObjectMapper objectMapper;

    public List<RestauranteResponseDto> getAllRestaurantes() {
        log.info("[getAllRestaurantes] Consultando todos los restaurantes");
        
        List<RestauranteEntity> entities = restauranteRepository.findAll();
        
        List<RestauranteResponseDto> restaurantes = entities.stream()
                .map(this::toDto)
                .toList();
        
        log.info("[getAllRestaurantes] Se encontraron {} restaurantes", restaurantes.size());
        return restaurantes;
    }

    public RestauranteResponseDto getRestauranteById(Long id) {
        log.info("[getRestauranteById] Buscando restaurante con id={}", id);
        
        RestauranteEntity entity = restauranteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[getRestauranteById] Restaurante no encontrado id={}", id);
                    return new RestauranteNotFoundException(id);
                });
        
        return toDto(entity);
    }

    private RestauranteResponseDto toDto(RestauranteEntity entity) {
        return RestauranteResponseDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .coordenadaX(entity.getCoordenadaX())
                .coordenadaY(entity.getCoordenadaY())
                .menu(parseMenu(entity.getMenu()))
                .build();
    }

    private List<ProductoMenuDto> parseMenu(String menuJson) {
        if (menuJson == null || menuJson.isBlank()) {
            return Collections.emptyList();
        }
        
        try {
            return objectMapper.readValue(menuJson, new TypeReference<List<ProductoMenuDto>>() {});
        } catch (Exception e) {
            log.error("Error al parsear menú JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
