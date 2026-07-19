package com.logitrack.api.domain.shippingorder;

public enum ShippingOrderStatus {
    PENDING,     // Aguardando carregamento / validação
    APPROVED,    // Carga validada e liberada para viagem
    DELIVERED,   // Entregue aos clientes
    CANCELLED     // Cancelada por motivos operacionais
}