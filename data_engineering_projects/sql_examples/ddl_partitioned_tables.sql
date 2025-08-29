-- ===================================================================
-- File: ddl_partitioned_tables.sql
-- Purpose: Example DDL for partitioned tables (dummy/demo version)
-- ===================================================================

-- =========================
-- 1) Staging: raw payments
-- =========================
CREATE TABLE demo_staging_payments_raw (
    ingested_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    period_yyyymm     NUMBER(6)      NOT NULL,   -- e.g., 202501
    user_id           VARCHAR2(32)   NOT NULL,
    account_id        VARCHAR2(32)   NOT NULL,
    doc_num           VARCHAR2(32),
    event_ts          TIMESTAMP      NOT NULL,
    amount_value      NUMBER(12,0)   NOT NULL,
    channel_code      VARCHAR2(16),
    source_file       VARCHAR2(128)
)
PARTITION BY RANGE (period_yyyymm) (
    PARTITION p_202401 VALUES LESS THAN (202402),
    PARTITION p_202402 VALUES LESS THAN (202403)
);

-- ============================
-- 2) Curated payments fact
-- ============================
CREATE TABLE demo_dw_fact_payments (
    period_yyyymm     NUMBER(6)      NOT NULL,
    user_id           VARCHAR2(32)   NOT NULL,
    account_id        VARCHAR2(32)   NOT NULL,
    txn_date          DATE           NOT NULL,
    amount_value      NUMBER(12,0)   NOT NULL,
    channel_code      VARCHAR2(16),
    last_update_ts    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
)
PARTITION BY RANGE (period_yyyymm) (
    PARTITION p_202401 VALUES LESS THAN (202402),
    PARTITION p_202402 VALUES LESS THAN (202403)
);
