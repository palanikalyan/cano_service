CREATE TABLE IF NOT EXISTS canonical_trades
(
    id UUID PRIMARY KEY,
    order_id VARCHAR(100),
    fund_code VARCHAR(50),
    investor_name VARCHAR(100),
    txn_type VARCHAR(20),
    amount NUMERIC(15,2),
    units NUMERIC(10,4),
    status VARCHAR(20),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS outbox_events
(
    id UUID PRIMARY KEY,
    aggregate_id UUID,
    event_type VARCHAR(100),
    payload JSONB,
    status VARCHAR(20),
    created_at TIMESTAMP
);
