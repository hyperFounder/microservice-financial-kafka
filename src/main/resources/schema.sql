CREATE TABLE IF NOT EXISTS transactions(
    transaction_id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS outbox_events(
    event_id VARCHAR(36) PRIMARY KEY,
    aggregate_id VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
)

-- Avoid full outbox_events TABLE scan
CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox_events (status)