CREATE TABLE IF NOT EXISTS orders (
 id UUID PRIMARY KEY,
 product_id TEXT NOT NULL,
 quantity INTEGER NOT NULL CHECK (quantity > 0),
 amount NUMERIC(18,2) NOT NULL CHECK (amount > 0),
 status TEXT NOT NULL CHECK (status IN ('PENDING','COMPLETED','CANCELLED','REQUIRES_ATTENTION'))
);
CREATE TABLE IF NOT EXISTS inventory_lab (
 product_id TEXT PRIMARY KEY,
 available_quantity INTEGER NOT NULL CHECK (available_quantity >= 0)
);
INSERT INTO inventory_lab(product_id,available_quantity) VALUES ('laptop',1)
 ON CONFLICT(product_id) DO NOTHING;
