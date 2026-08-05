package com.logitrack.api.domain.shippingorder;

public enum ShippingOrderStatus {
    PENDING,
    ROUTED,
    DISPATCHED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED;

    /**
     * Valida as transições permitidas no ciclo de vida de uma ordem de frete no TMS.
     */
    public boolean canTransitionTo(ShippingOrderStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        return switch (this) {
            case PENDING -> nextStatus == ROUTED || nextStatus == CANCELLED;
            case ROUTED -> nextStatus == DISPATCHED || nextStatus == CANCELLED;
            case DISPATCHED -> nextStatus == IN_TRANSIT || nextStatus == CANCELLED;
            case IN_TRANSIT -> nextStatus == DELIVERED || nextStatus == CANCELLED;
            case DELIVERED, CANCELLED -> false; // Estados terminais
        };
    }
}