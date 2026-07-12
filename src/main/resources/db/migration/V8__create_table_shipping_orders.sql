-- Tabela Mãe: Ordem de Frete vinculada a um Veículo
CREATE TABLE shipping_orders (
                                 id BIGSERIAL PRIMARY KEY,
                                 vehicle_id BIGINT NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 total_weight DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 total_cubic_volume DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_shipping_orders_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Tabela Filha: Itens da Ordem (Produtos, Quantidades e Clientes diferentes)
CREATE TABLE shipping_order_items (
                                      id BIGSERIAL PRIMARY KEY,
                                      shipping_order_id BIGINT NOT NULL,
                                      product_id BIGINT NOT NULL,
                                      quantity INTEGER NOT NULL,
                                      customer_name VARCHAR(150) NOT NULL, -- Identifica o cliente desta entrega
                                      CONSTRAINT fk_items_shipping_order FOREIGN KEY (shipping_order_id) REFERENCES shipping_orders(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);