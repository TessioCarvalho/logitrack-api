CREATE TABLE vehicles (
                          id BIGSERIAL PRIMARY KEY,
                          plate VARCHAR(20) NOT NULL UNIQUE,
                          model VARCHAR(100) NOT NULL,
                          max_capacity_kg DECIMAL(10, 2) NOT NULL
);