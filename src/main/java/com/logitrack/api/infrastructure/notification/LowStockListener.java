package com.logitrack.api.infrastructure.notification;

import com.logitrack.api.domain.product.event.LowStockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LowStockListener {

    private static final Logger log = LoggerFactory.getLogger(LowStockListener.class);

    @EventListener
    public void handleLowStock(LowStockEvent event) {
        log.warn("🚨 ALERT - STOCK BELOW MINIMUM LEVEL | Product: {} (SKU: {}) | Current: {} | Min Required: {}",
                event.name(), event.sku(), event.currentStock(), event.minimumStock());

        // Aqui no futuro você injetaria um EmailService, SlackService, etc.
    }
}