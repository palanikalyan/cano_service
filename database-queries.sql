-- ========================================
-- DATABASE SETUP FOR CANONICAL SERVICE
-- ========================================

-- Create Database
CREATE DATABASE canonical_db;

-- Connect to the database
\c canonical_db

-- ========================================
-- CREATE TABLES
-- ========================================

-- Table: canonical_trades
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

-- Table: outbox_events
CREATE TABLE IF NOT EXISTS outbox_events
(
    id UUID PRIMARY KEY,
    aggregate_id UUID,
    event_type VARCHAR(100),
    payload JSONB,
    status VARCHAR(20),
    created_at TIMESTAMP
);

-- ========================================
-- QUERY TO CHECK DATA
-- ========================================

-- Check all canonical trades
SELECT * FROM canonical_trades ORDER BY created_at DESC;

-- Check all outbox events
SELECT * FROM outbox_events ORDER BY created_at DESC;

-- Count records
SELECT 
    (SELECT COUNT(*) FROM canonical_trades) as total_trades,
    (SELECT COUNT(*) FROM outbox_events) as total_events;

-- Check latest 5 trades with their outbox events
SELECT 
    ct.id,
    ct.order_id,
    ct.fund_code,
    ct.investor_name,
    ct.txn_type,
    ct.amount,
    ct.status as trade_status,
    ct.created_at as trade_created,
    oe.event_type,
    oe.status as event_status,
    oe.created_at as event_created
FROM canonical_trades ct
LEFT JOIN outbox_events oe ON ct.id = oe.aggregate_id
ORDER BY ct.created_at DESC
LIMIT 5;

-- Check by transaction type
SELECT txn_type, COUNT(*) as count, SUM(amount) as total_amount
FROM canonical_trades
GROUP BY txn_type;

-- Check by fund code
SELECT fund_code, COUNT(*) as count
FROM canonical_trades
GROUP BY fund_code
ORDER BY count DESC;

-- Check pending outbox events
SELECT * FROM outbox_events 
WHERE status = 'PENDING' 
ORDER BY created_at DESC;

-- ========================================
-- CLEANUP QUERIES (Use with caution)
-- ========================================

-- Delete all data (for testing)
-- TRUNCATE TABLE outbox_events;
-- TRUNCATE TABLE canonical_trades;

-- Drop tables (if needed)
-- DROP TABLE IF EXISTS outbox_events;
-- DROP TABLE IF EXISTS canonical_trades;
