package com.logitrack.api.domain.shippingorder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ShippingOrderItemRequest(
        @NotNull(message = "O ID do produto é obrigatório")
        Long productId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantity,

        @NotBlank(message = "O nome do cliente é obrigatório")
        String customerName
) {}