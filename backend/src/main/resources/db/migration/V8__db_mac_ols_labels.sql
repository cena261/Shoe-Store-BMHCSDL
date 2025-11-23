/* ============================================
   ShoeStore – MAC with Oracle Label Security (OLS)
   V8__db_mac_ols_labels.sql
   ============================================ */

ALTER TABLE ORDERS ADD SECURITY_LABEL NVARCHAR2(20) DEFAULT 'PUBLIC' NOT NULL;

UPDATE ORDERS SET SECURITY_LABEL = 'PUBLIC' WHERE SECURITY_LABEL IS NULL;

COMMIT;

BEGIN
    EXECUTE IMMEDIATE 'DROP PACKAGE SHOE_OLS_PKG';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -4043 THEN RAISE; END IF;
END;
/

BEGIN
    DBMS_RLS.DROP_POLICY(
        object_schema => 'SHOE_APP',
        object_name   => 'ORDERS',
        policy_name   => 'ORDERS_OLS_POLICY'
    );
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -28102 THEN RAISE; END IF;
END;
/

CREATE OR REPLACE PACKAGE SHOE_OLS_PKG AS
    FUNCTION GET_OLS_PREDICATE(
        p_schema VARCHAR2,
        p_object VARCHAR2
    ) RETURN VARCHAR2;
END SHOE_OLS_PKG;
/

CREATE OR REPLACE PACKAGE BODY SHOE_OLS_PKG AS
    FUNCTION GET_OLS_PREDICATE(
        p_schema VARCHAR2,
        p_object VARCHAR2
    ) RETURN VARCHAR2 IS
        v_client_id VARCHAR2(128);
        v_predicate VARCHAR2(4000);
    BEGIN
        v_client_id := SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER');

        IF v_client_id IS NULL THEN
            v_predicate := '1=0';
        ELSIF v_client_id = 'ADMIN' THEN
            v_predicate := '1=1';
        ELSE
            v_predicate := 'SECURITY_LABEL = ''PUBLIC''';
        END IF;

        RETURN v_predicate;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN '1=0';
    END GET_OLS_PREDICATE;
END SHOE_OLS_PKG;
/

BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'SHOE_APP',
        object_name     => 'ORDERS',
        policy_name     => 'ORDERS_OLS_POLICY',
        function_schema => 'SHOE_APP',
        policy_function => 'SHOE_OLS_PKG.GET_OLS_PREDICATE',
        statement_types => 'SELECT'
    );
END;
/

COMMENT ON COLUMN ORDERS.SECURITY_LABEL IS 'Label-based security classification: PUBLIC, CONFIDENTIAL, ADMIN_ONLY';

CREATE INDEX IDX_ORDERS_SECURITY_LABEL ON ORDERS(SECURITY_LABEL);
