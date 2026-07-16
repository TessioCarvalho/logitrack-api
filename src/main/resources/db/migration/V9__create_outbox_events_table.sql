CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_type VARCHAR(255) NOT NULL,
                               aggregate_id VARCHAR(255) NOT NULL,
                               event_type VARCHAR(255) NOT NULL,
                               payload TEXT NOT NULL,
                               created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                               processed BOOLEAN NOT NULL DEFAULT FALSE,
                               processed_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_outbox_unprocessed ON outbox_events(processed) WHERE processed = FALSE;