-- Drop existing tables
DROP TABLE IF EXISTS canonical_trades CASCADE;

CREATE TABLE IF NOT EXISTS canonical_trades
(
    id UUID PRIMARY KEY,
    status VARCHAR(20),
    created_at TIMESTAMP,
    -- Trade fields
    originator_type INTEGER,
    firm_number INTEGER,
    fund_number INTEGER,
    transaction_type VARCHAR(10),
    transaction_id VARCHAR(50),
    trade_datetime TIMESTAMP,
    dollar_amount NUMERIC(15,2),
    client_account_no INTEGER,
    client_name VARCHAR(100),
    ssn VARCHAR(20),
    dob DATE,
    share_quantity NUMERIC(15,0)
);

CREATE INDEX IF NOT EXISTS idx_transaction_id ON canonical_trades(transaction_id);
CREATE INDEX IF NOT EXISTS idx_trade_datetime ON canonical_trades(trade_datetime);
CREATE INDEX IF NOT EXISTS idx_client_account ON canonical_trades(client_account_no);
