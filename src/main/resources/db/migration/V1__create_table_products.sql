CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          quantity INT NOT NULL DEFAULT 0
);