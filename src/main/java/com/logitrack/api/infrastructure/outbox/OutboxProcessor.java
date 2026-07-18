package com.logitrack.api.infrastructure.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    private final OutboxEventRepository outboxEventRepository;

    public OutboxProcessor(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    // Executa a cada 5000 milissegundos (5 segundos)
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processPendingEvents() {
        // Busca apenas o que não foi processado usando o índice parcial performático que criamos
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Encontrados {} eventos pendentes na tabela Outbox para processamento.", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                // 💡 AQUI É O DIFERENCIAL SÊNIOR:
                // Simulamos o disparo para um Message Broker (RabbitMQ / Kafka) ou serviço de e-mail externo.
                log.info("Disparando evento [{}] ID: {} para o sistema de notificações externas. Payload: {}",
                        event.getEventType(), event.getId(), event.getPayload());

                // Se o disparo funcionou sem lançar exceção, marcamos como processado com segurança
                event.markAsProcessed();
                outboxEventRepository.save(event);

                log.info("Evento ID: {} processado e consolidado com sucesso no Outbox.", event.getId());

            } catch (Exception e) {
                log.error("Falha crônica ao processar evento ID: {}. Ele será tentado novamente no próximo ciclo.", event.getId(), e);
                // Não relançamos a exceção aqui para permitir que os próximos eventos da lista tentem ser processados
            }
        }
    }
}