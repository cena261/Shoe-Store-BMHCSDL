/* ============================================
   ShoeStore – Database-Level Hybrid Encryption
   V4__db_hybrid_encryption_order_exports.sql

   Purpose: Create ORDEREXPORTS table to store AES-encrypted
            order data with RSA-encrypted AES keys.
            Demonstrates hybrid encryption at database level.
   ============================================ */

CREATE TABLE ORDEREXPORTS (
    EXPORT_ID NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    CREATED_AT TIMESTAMP NOT NULL,
    CREATED_BY NUMBER(10) NOT NULL,
    ENCRYPTED_KEY NVARCHAR2(2000) NOT NULL,
    ENCRYPTED_DATA BLOB NOT NULL,
    CONSTRAINT FK_ORDEREXPORTS_USER FOREIGN KEY (CREATED_BY) REFERENCES USERS(USERID)
);

COMMENT ON TABLE ORDEREXPORTS IS 'Stores hybrid-encrypted order exports (AES for data, RSA for key)';
COMMENT ON COLUMN ORDEREXPORTS.EXPORT_ID IS 'Primary key, auto-generated';
COMMENT ON COLUMN ORDEREXPORTS.CREATED_AT IS 'Timestamp when export was created';
COMMENT ON COLUMN ORDEREXPORTS.CREATED_BY IS 'User ID of admin who created the export';
COMMENT ON COLUMN ORDEREXPORTS.ENCRYPTED_KEY IS 'AES key encrypted with RSA public key (Base64)';
COMMENT ON COLUMN ORDEREXPORTS.ENCRYPTED_DATA IS 'Order data JSON encrypted with AES';

CREATE INDEX IDX_ORDEREXPORTS_CREATED_BY ON ORDEREXPORTS(CREATED_BY);
CREATE INDEX IDX_ORDEREXPORTS_CREATED_AT ON ORDEREXPORTS(CREATED_AT DESC);
