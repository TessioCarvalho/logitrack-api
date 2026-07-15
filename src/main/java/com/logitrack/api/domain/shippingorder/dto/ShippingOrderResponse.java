package com.logitrack.api.domain.shippingorder.dto;

import com.logitrack.api.domain.shippingorder.ShippingOrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ShippingOrderResponse(
        Long id,
        Long vehicleId,
        String vehicleModel,
        ShippingOrderStatus status,
        Double totalWeight,
        Double totalCubicVolume,
        LocalDateTime createdAt,
        List<ShippingOrderItemResponse> items
) {}