CREATE TABLE products (
                          id                BIGSERIAL PRIMARY KEY,
                          sku               VARCHAR(64)  NOT NULL UNIQUE,
                          name              VARCHAR(255) NOT NULL,
                          quantity_in_stock INTEGER      NOT NULL CHECK (quantity_in_stock >= 0),
                          unit_price        NUMERIC(19,2) NOT NULL CHECK (unit_price >= 0),
                          version           BIGINT       NOT NULL DEFAULT 0
);


CREATE TABLE sale_records (
                              id             BIGSERIAL PRIMARY KEY,
                              product_id     BIGINT       NOT NULL REFERENCES products(id),
                              product_name   VARCHAR(255) NOT NULL,
                              quantity       INTEGER      NOT NULL CHECK (quantity > 0),
                              unit_price     NUMERIC(19,2) NOT NULL,
                              sale_date      DATE         NOT NULL,
                              sale_timestamp TIMESTAMP    NOT NULL
);

CREATE INDEX idx_sale_records_sale_date ON sale_records (sale_date);
CREATE INDEX idx_sale_records_product_id ON sale_records (product_id);


INSERT INTO products (sku, name, quantity_in_stock, unit_price) VALUES
                                                                    ('BRV-0001', 'Coca-Cola 0.5L', 500, 1.20),
                                                                    ('BRV-0002', 'Chocolate Bar 100g', 300, 2.50),
                                                                    ('BRV-0003', 'White Bread', 200, 0.90),
                                                                    ('BRV-0004', 'Milk 1L', 150, 1.80),
                                                                    ('BRV-0005', 'Instant Coffee 200g', 100, 6.75);