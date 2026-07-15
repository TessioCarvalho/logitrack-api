package com.logitrack.api.domain.shippingorder.dto;

public record ShippingOrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        String customerName
) {}
