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
    created_at TIMESTAMP,
    -- New fields from fixed-width format
    originator_type VARCHAR(1),
    firm_number VARCHAR(10),
    fund_number VARCHAR(10),
    transaction_type VARCHAR(10),
    transaction_id VARCHAR(50),
    trade_date DATE,
    dollar_amount NUMERIC(15,2),
    client_account_no VARCHAR(50),
    client_name VARCHAR(100),
    ssn VARCHAR(20),
    dob DATE,
    kyc VARCHAR(1),
    share_quantity NUMERIC(15,0)
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
