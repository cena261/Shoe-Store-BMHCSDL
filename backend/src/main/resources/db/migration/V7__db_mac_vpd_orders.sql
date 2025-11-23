/* ============================================
   ShoeStore – Mandatory Access Control (MAC) with VPD
   V7__db_mac_vpd_orders.sql
   ============================================ */

BEGIN
    EXECUTE IMMEDIATE 'DROP PACKAGE SHOE_VPD_PKG';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -4043 THEN RAISE; END IF;
END;
/

BEGIN
    DBMS_RLS.DROP_POLICY(
        object_schema => 'SHOE_APP',
        object_name   => 'ORDERS',
        policy_name   => 'ORDERS_VPD_POLICY'
    );
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -28102 THEN RAISE; END IF;
END;
/

BEGIN
    DBMS_RLS.DROP_POLICY(
        object_schema => 'SHOE_APP',
        object_name   => 'ORDERITEMS',
        policy_name   => 'ORDERITEMS_VPD_POLICY'
    );
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -28102 THEN RAISE; END IF;
END;
/

CREATE OR REPLACE PACKAGE SHOE_VPD_PKG AS
    FUNCTION GET_PREDICATE(
        p_schema VARCHAR2,
        p_object VARCHAR2
    ) RETURN VARCHAR2;

    FUNCTION GET_ORDERITEMS_PREDICATE(
        p_schema VARCHAR2,
        p_object VARCHAR2
    ) RETURN VARCHAR2;
END SHOE_VPD_PKG;
/

CREATE OR REPLACE PACKAGE BODY SHOE_VPD_PKG AS
    FUNCTION GET_PREDICATE(
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
            v_predicate := 'USERID = TO_NUMBER(SYS_CONTEXT(''USERENV'', ''CLIENT_IDENTIFIER''))';
        END IF;

        RETURN v_predicate;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN '1=0';
    END GET_PREDICATE;

    FUNCTION GET_ORDERITEMS_PREDICATE(
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
            v_predicate := 'ORDERID IN (SELECT ORDERID FROM ORDERS WHERE USERID = TO_NUMBER(SYS_CONTEXT(''USERENV'', ''CLIENT_IDENTIFIER'')))';
        END IF;

        RETURN v_predicate;
    EXCEPTION
        WHEN OTHERS THEN
            RETURN '1=0';
    END GET_ORDERITEMS_PREDICATE;
END SHOE_VPD_PKG;
/

BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'SHOE_APP',
        object_name     => 'ORDERS',
        policy_name     => 'ORDERS_VPD_POLICY',
        function_schema => 'SHOE_APP',
        policy_function => 'SHOE_VPD_PKG.GET_PREDICATE',
        statement_types => 'SELECT'
    );
END;
/

BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'SHOE_APP',
        object_name     => 'ORDERITEMS',
        policy_name     => 'ORDERITEMS_VPD_POLICY',
        function_schema => 'SHOE_APP',
        policy_function => 'SHOE_VPD_PKG.GET_ORDERITEMS_PREDICATE',
        statement_types => 'SELECT'
    );
END;
/
