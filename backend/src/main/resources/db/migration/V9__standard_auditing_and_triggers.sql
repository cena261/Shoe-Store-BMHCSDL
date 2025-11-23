-- =====================================================
-- V9: Standard Auditing and Trigger-Based Auditing
-- =====================================================

AUDIT SELECT, INSERT, UPDATE, DELETE ON SHOE_APP.USERS BY ACCESS;
AUDIT SELECT, INSERT, UPDATE, DELETE ON SHOE_APP.ORDERS BY ACCESS;
AUDIT SELECT, INSERT, UPDATE, DELETE ON SHOE_APP.ORDERITEMS BY ACCESS;
AUDIT SELECT, INSERT, UPDATE, DELETE ON SHOE_APP.PRODUCTVARIANTS BY ACCESS;
AUDIT SELECT, INSERT, UPDATE, DELETE ON SHOE_APP.ADDRESSES BY ACCESS;

CREATE TABLE SHOE_APP.AUDIT_LOGS (
    AUDIT_ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    EVENT_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    USERNAME NVARCHAR2(128),
    IP_ADDRESS NVARCHAR2(128),
    HOST NVARCHAR2(128),
    OBJECT_NAME NVARCHAR2(128) NOT NULL,
    ACTION NVARCHAR2(20) NOT NULL,
    ORDER_ID NUMBER,
    DETAILS NVARCHAR2(2000)
);

CREATE INDEX IDX_AUDIT_LOGS_EVENT_TIME ON SHOE_APP.AUDIT_LOGS(EVENT_TIME DESC);
CREATE INDEX IDX_AUDIT_LOGS_OBJECT_NAME ON SHOE_APP.AUDIT_LOGS(OBJECT_NAME);
CREATE INDEX IDX_AUDIT_LOGS_USERNAME ON SHOE_APP.AUDIT_LOGS(USERNAME);
CREATE INDEX IDX_AUDIT_LOGS_ORDER_ID ON SHOE_APP.AUDIT_LOGS(ORDER_ID);


CREATE OR REPLACE TRIGGER SHOE_APP.TRG_ORDERS_AUDIT
AFTER INSERT OR UPDATE OR DELETE ON SHOE_APP.ORDERS
FOR EACH ROW
DECLARE
    v_action NVARCHAR2(20);
    v_username NVARCHAR2(128);
    v_ip_address NVARCHAR2(128);
    v_host NVARCHAR2(128);
    v_order_id NUMBER;
    v_details NVARCHAR2(2000);
BEGIN
    v_username := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_ip_address := SYS_CONTEXT('USERENV', 'IP_ADDRESS');
    v_host := SYS_CONTEXT('USERENV', 'HOST');

    IF INSERTING THEN
        v_action := 'INSERT';
        v_order_id := :NEW.ORDERID;
        v_details := 'New order created with status: ' || :NEW.ORDERSTATUS ||
                     ', Total: ' || TO_CHAR(:NEW.TOTALAMOUNT);
    ELSIF UPDATING THEN
        v_action := 'UPDATE';
        v_order_id := :NEW.ORDERID;

        v_details := 'Order updated.';

        IF :OLD.ORDERSTATUS != :NEW.ORDERSTATUS OR
           (:OLD.ORDERSTATUS IS NULL AND :NEW.ORDERSTATUS IS NOT NULL) OR
           (:OLD.ORDERSTATUS IS NOT NULL AND :NEW.ORDERSTATUS IS NULL) THEN
            v_details := v_details || ' Status changed from ' ||
                        NVL(:OLD.ORDERSTATUS, 'NULL') || ' to ' ||
                        NVL(:NEW.ORDERSTATUS, 'NULL') || '.';
        END IF;

        IF :OLD.TOTALAMOUNT != :NEW.TOTALAMOUNT THEN
            v_details := v_details || ' Total amount changed from ' ||
                        TO_CHAR(:OLD.TOTALAMOUNT) || ' to ' ||
                        TO_CHAR(:NEW.TOTALAMOUNT) || '.';
        END IF;

    ELSIF DELETING THEN
        v_action := 'DELETE';
        v_order_id := :OLD.ORDERID;
        v_details := 'Order deleted. Status was: ' || :OLD.ORDERSTATUS ||
                     ', Total was: ' || TO_CHAR(:OLD.TOTALAMOUNT);
    END IF;

    INSERT INTO SHOE_APP.AUDIT_LOGS (
        EVENT_TIME,
        USERNAME,
        IP_ADDRESS,
        HOST,
        OBJECT_NAME,
        ACTION,
        ORDER_ID,
        DETAILS
    ) VALUES (
        CURRENT_TIMESTAMP,
        v_username,
        v_ip_address,
        v_host,
        'ORDERS',
        v_action,
        v_order_id,
        v_details
    );

EXCEPTION
    WHEN OTHERS THEN
        -- log error khong anh huong toi DML, nen log vao bang rieng, build sau
        NULL;
END;
/

-- optional

CREATE OR REPLACE TRIGGER SHOE_APP.TRG_USERS_AUDIT
AFTER INSERT OR UPDATE OR DELETE ON SHOE_APP.USERS
FOR EACH ROW
DECLARE
    v_action NVARCHAR2(20);
    v_username NVARCHAR2(128);
    v_ip_address NVARCHAR2(128);
    v_host NVARCHAR2(128);
    v_details NVARCHAR2(2000);
BEGIN
    v_username := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_ip_address := SYS_CONTEXT('USERENV', 'IP_ADDRESS');
    v_host := SYS_CONTEXT('USERENV', 'HOST');

    IF INSERTING THEN
        v_action := 'INSERT';
        v_details := 'New user registered: ' || :NEW.EMAIL;
    ELSIF UPDATING THEN
        v_action := 'UPDATE';
        v_details := 'User updated: ' || :NEW.EMAIL;

        IF :OLD.ISACTIVE != :NEW.ISACTIVE THEN
            v_details := v_details || '. Active status changed from ' ||
                        TO_CHAR(:OLD.ISACTIVE) || ' to ' ||
                        TO_CHAR(:NEW.ISACTIVE);
        END IF;
    ELSIF DELETING THEN
        v_action := 'DELETE';
        v_details := 'User deleted: ' || :OLD.EMAIL;
    END IF;

    INSERT INTO SHOE_APP.AUDIT_LOGS (
        EVENT_TIME,
        USERNAME,
        IP_ADDRESS,
        HOST,
        OBJECT_NAME,
        ACTION,
        ORDER_ID,
        DETAILS
    ) VALUES (
        CURRENT_TIMESTAMP,
        v_username,
        v_ip_address,
        v_host,
        'USERS',
        v_action,
        NULL,
        v_details
    );

EXCEPTION
    WHEN OTHERS THEN
        NULL;
END;
/

CREATE OR REPLACE TRIGGER SHOE_APP.TRG_ORDERITEMS_AUDIT
AFTER INSERT OR UPDATE OR DELETE ON SHOE_APP.ORDERITEMS
FOR EACH ROW
DECLARE
    v_action NVARCHAR2(20);
    v_username NVARCHAR2(128);
    v_ip_address NVARCHAR2(128);
    v_host NVARCHAR2(128);
    v_order_id NUMBER;
    v_details NVARCHAR2(2000);
BEGIN
    v_username := SYS_CONTEXT('USERENV', 'SESSION_USER');
    v_ip_address := SYS_CONTEXT('USERENV', 'IP_ADDRESS');
    v_host := SYS_CONTEXT('USERENV', 'HOST');

    IF INSERTING THEN
        v_action := 'INSERT';
        v_order_id := :NEW.ORDERID;
        v_details := 'Order item added. Quantity: ' || TO_CHAR(:NEW.QUANTITY) ||
                     ', Unit Price: ' || TO_CHAR(:NEW.UNITPRICE);
    ELSIF UPDATING THEN
        v_action := 'UPDATE';
        v_order_id := :NEW.ORDERID;
        v_details := 'Order item updated.';

        IF :OLD.QUANTITY != :NEW.QUANTITY THEN
            v_details := v_details || ' Quantity changed from ' ||
                        TO_CHAR(:OLD.QUANTITY) || ' to ' ||
                        TO_CHAR(:NEW.QUANTITY) || '.';
        END IF;

        IF :OLD.UNITPRICE != :NEW.UNITPRICE THEN
            v_details := v_details || ' Unit price changed from ' ||
                        TO_CHAR(:OLD.UNITPRICE) || ' to ' ||
                        TO_CHAR(:NEW.UNITPRICE) || '.';
        END IF;
    ELSIF DELETING THEN
        v_action := 'DELETE';
        v_order_id := :OLD.ORDERID;
        v_details := 'Order item removed. Quantity was: ' || TO_CHAR(:OLD.QUANTITY);
    END IF;

    INSERT INTO SHOE_APP.AUDIT_LOGS (
        EVENT_TIME,
        USERNAME,
        IP_ADDRESS,
        HOST,
        OBJECT_NAME,
        ACTION,
        ORDER_ID,
        DETAILS
    ) VALUES (
        CURRENT_TIMESTAMP,
        v_username,
        v_ip_address,
        v_host,
        'ORDERITEMS',
        v_action,
        v_order_id,
        v_details
    );

EXCEPTION
    WHEN OTHERS THEN
        NULL;
END;
/

COMMENT ON TABLE SHOE_APP.AUDIT_LOGS IS 'Custom audit log table for tracking business operations with detailed context';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.AUDIT_ID IS 'Primary key, auto-generated';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.EVENT_TIME IS 'Timestamp when the event occurred';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.USERNAME IS 'Database username from SYS_CONTEXT';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.IP_ADDRESS IS 'Client IP address from SYS_CONTEXT';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.HOST IS 'Client hostname from SYS_CONTEXT';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.OBJECT_NAME IS 'Name of the audited table (ORDERS, USERS, ORDERITEMS)';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.ACTION IS 'DML operation type (INSERT, UPDATE, DELETE)';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.ORDER_ID IS 'Order ID if applicable (for ORDERS and ORDERITEMS)';
COMMENT ON COLUMN SHOE_APP.AUDIT_LOGS.DETAILS IS 'Detailed description of what changed';
