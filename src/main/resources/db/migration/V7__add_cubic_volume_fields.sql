-- 1. Corrige o tipo da coluna da V6 para bater com o Double do Java
ALTER TABLE vehicles
ALTER COLUMN max_capacity_kg TYPE DOUBLE PRECISION;

-- 2. Atualização no WMS: Volume ocupado por um único produto
ALTER TABLE products
    ADD COLUMN cubic_volume DOUBLE PRECISION NOT NULL DEFAULT 0.0;

-- 3. Atualização no TMS: Capacidade volumétrica total do baú do veículo
ALTER TABLE vehicles
    ADD COLUMN max_cubic_volume DOUBLE PRECISION NOT NULL DEFAULT 0.0;