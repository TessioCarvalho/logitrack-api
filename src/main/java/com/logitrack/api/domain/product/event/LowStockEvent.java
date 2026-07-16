package com.logitrack.api.domain.product.event;

public record LowStockEvent(
        Long productId,
        String sku,
        String name,
        Integer currentStock,
        Integer minimumStock
) {}