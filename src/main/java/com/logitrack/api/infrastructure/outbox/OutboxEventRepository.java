package com.logitrack.api.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    // Busca eventos que ainda não foram disparados para processamento
    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
}