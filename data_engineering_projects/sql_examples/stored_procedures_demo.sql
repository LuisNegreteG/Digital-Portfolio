-- ===================================================================
-- File: stored_procedures_demo.sql
-- Purpose: Example stored procedure for balance snapshots (dummy/demo)
-- ===================================================================

CREATE OR REPLACE PACKAGE demo_pkg_account_balance AS
  PROCEDURE calc_daily_balance (
    p_account_id   IN  VARCHAR2,
    p_as_of_date   IN  DATE,
    p_out_balance  OUT NUMBER
  );

  FUNCTION get_month_balance (
    p_account_id IN VARCHAR2,
    p_period     IN NUMBER
  ) RETURN NUMBER;
END demo_pkg_account_balance;
/

CREATE OR REPLACE PACKAGE BODY demo_pkg_account_balance AS

  PROCEDURE calc_daily_balance (
    p_account_id   IN  VARCHAR2,
    p_as_of_date   IN  DATE,
    p_out_balance  OUT NUMBER
  ) IS
  BEGIN
    SELECT NVL(SUM(amount_value),0)
      INTO p_out_balance
      FROM demo_dw_fact_payments
     WHERE account_id = p_account_id
       AND txn_date   = TRUNC(p_as_of_date);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      p_out_balance := 0;
  END calc_daily_balance;

  FUNCTION get_month_balance (
    p_account_id IN VARCHAR2,
    p_period     IN NUMBER
  ) RETURN NUMBER IS
    v_sum NUMBER := 0;
  BEGIN
    SELECT NVL(SUM(amount_value),0)
      INTO v_sum
      FROM demo_dw_fact_payments
     WHERE account_id    = p_account_id
       AND period_yyyymm = p_period;

    RETURN v_sum;
  END get_month_balance;

END demo_pkg_account_balance;
/
