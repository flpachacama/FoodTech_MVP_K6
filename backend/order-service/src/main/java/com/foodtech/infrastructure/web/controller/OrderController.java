package com.foodtech.infrastructure.web.controller;

import com.foodtech.domain.port.input.OrderUseCase;
import com.foodtech.infrastructure.web.dto.CancelOrderResponseDto;
import com.foodtech.infrastructure.web.dto.DeliverOrderResponseDto;
import com.foodtech.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.infrastructure.web.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto request) {
        OrderResponseDto response = orderUseCase.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<CancelOrderResponseDto> cancelOrder(@PathVariable Long id) {
        CancelOrderResponseDto response = orderUseCase.cancelOrder(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<DeliverOrderResponseDto> deliverOrder(@PathVariable Long id) {
        DeliverOrderResponseDto response = orderUseCase.deliverOrder(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/repartidor/{repartidorId}")
    public ResponseEntity<OrderResponseDto> getOrderByRepartidorId(@PathVariable Long repartidorId) {
        return ResponseEntity.ok(orderUseCase.getOrderByRepartidorId(repartidorId));
    }
}
