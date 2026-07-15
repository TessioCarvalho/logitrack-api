package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.shippingorder.dto.ShippingOrderRequest;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderResponse;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderItemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping-orders")
public class ShippingOrderController {

    private final ShippingOrderService shippingOrderService;

    public ShippingOrderController(ShippingOrderService shippingOrderService) {
        this.shippingOrderService = shippingOrderService;
    }

    @PostMapping
    public ResponseEntity<ShippingOrderResponse> create(@RequestBody @Valid ShippingOrderRequest request) {
        ShippingOrder order = shippingOrderService.createShippingOrder(request);

        // Mapeia a lista de itens internos para DTOs expostos com segurança
        List<ShippingOrderItemResponse> itemDtos = order.getItems().stream()
                .map(item -> new ShippingOrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getCustomerName()
                )).toList();

        // Constrói o DTO final de resposta sem acoplamento cíclico
        ShippingOrderResponse response = new ShippingOrderResponse(
                order.getId(),
                order.getVehicle().getId(),
                order.getVehicle().getModel(),
                order.getStatus(),
                order.getTotalWeight(),
                order.getTotalCubicVolume(),
                order.getCreatedAt(),
                itemDtos
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}