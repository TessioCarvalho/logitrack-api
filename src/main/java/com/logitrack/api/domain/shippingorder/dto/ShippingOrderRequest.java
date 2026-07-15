package com.logitrack.api.domain.shippingorder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ShippingOrderRequest(
        @NotNull(message = "O ID do veículo é obrigatório")
        Long vehicleId,

        @NotEmpty(message = "A ordem de frete deve conter pelo menos um item")
        @Valid
        List<ShippingOrderItemRequest> items
) {}