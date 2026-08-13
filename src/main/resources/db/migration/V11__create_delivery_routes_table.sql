-- 1. Adiciona a coluna de status na tabela de veículos (padrão AVAILABLE)
ALTER TABLE vehicles
    ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE';

-- 2. Cria a tabela de rotas de entrega
CREATE TABLE delivery_routes (
                                 id BIGSERIAL PRIMARY KEY,
                                 vehicle_id BIGINT NOT NULL,
                                 total_weight_kg DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 total_volume_m3 DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 CONSTRAINT fk_delivery_route_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- 3. Atualiza a tabela de ordens de frete para vincular à rota e registrar a sequência
ALTER TABLE shipping_orders
    ADD COLUMN IF NOT EXISTS delivery_route_id BIGINT,
    ADD COLUMN IF NOT EXISTS delivery_sequence INT,
    ADD CONSTRAINT fk_shipping_order_delivery_route FOREIGN KEY (delivery_route_id) REFERENCES delivery_routes(id);