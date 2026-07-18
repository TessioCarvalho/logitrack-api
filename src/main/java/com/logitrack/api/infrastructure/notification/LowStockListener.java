package com.logitrack.api.infrastructure.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.api.domain.product.event.LowStockEvent;
import com.logitrack.api.infrastructure.outbox.OutboxEvent;
import com.logitrack.api.infrastructure.outbox.OutboxEventRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LowStockListener {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public LowStockListener(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void handleLowStockEvent(LowStockEvent event) {
        try {
            // Converter o payload do record de evento para uma String JSON
            String jsonPayload = objectMapper.writeValueAsString(event);

            // Criar a entidade Outbox com os metadados corretos
            OutboxEvent outboxEvent = new OutboxEvent(
                    "Product",
                    event.productId().toString(),
                    event.getClass().getSimpleName(),
                    jsonPayload
            );

            // Salvar no banco. Como estamos na mesma transação do Service,
            // se o fluxo principal falhar após este ponto, este registro sofre ROLLBACK junto!
            outboxEventRepository.save(outboxEvent);

        } catch (Exception e) {
            // Em cenários de produção, lance uma exceção para travar a transação se o outbox falhar
            throw new RuntimeException("Falha ao serializar evento para a tabela Outbox", e);
        }
    }
}