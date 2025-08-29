-- ===================================================================
-- File: plsql_dynamic_etl.sql
-- Purpose: Example PL/SQL dynamic ETL (dummy/demo version)
-- ===================================================================

DECLARE
  v_period       NUMBER(6) := :p_period;  -- e.g., 202501
  v_target_tbl   VARCHAR2(128);
  v_sql          CLOB;
BEGIN
  v_target_tbl := 'demo_dw_fact_payments_' || v_period;

  -- Drop table if exists
  BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE ' || v_target_tbl;
  EXCEPTION WHEN OTHERS THEN NULL;
  END;

  v_sql := '
    CREATE TABLE ' || v_target_tbl || ' AS
    WITH base AS (
      SELECT
        period_yyyymm,
        user_id,
        account_id,
        CAST(event_ts AS DATE) AS txn_date,
        amount_value,
        ROW_NUMBER() OVER (
          PARTITION BY user_id, account_id, CAST(event_ts AS DATE)
          ORDER BY event_ts DESC
        ) AS rn
      FROM demo_staging_payments_raw
      WHERE period_yyyymm = :1
    )
    SELECT
      period_yyyymm,
      user_id,
      account_id,
      txn_date,
      amount_value,
      CURRENT_TIMESTAMP AS last_update_ts
    FROM base
    WHERE rn = 1';

  EXECUTE IMMEDIATE v_sql USING v_period;
  COMMIT;
END;
/
