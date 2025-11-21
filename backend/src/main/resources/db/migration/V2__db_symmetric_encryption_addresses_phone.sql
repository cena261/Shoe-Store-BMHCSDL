/* ============================================
   ShoeStore – Database-Level Symmetric Encryption
   V2__db_symmetric_encryption_addresses_phone.sql

   ============================================ */

ALTER TABLE ADDRESSES MODIFY (PHONE NVARCHAR2(255));


CREATE OR REPLACE PACKAGE SHOE_CRYPTO
AS
  FUNCTION encrypt (p_plainText VARCHAR2, priKey VARCHAR2) RETURN RAW DETERMINISTIC;
  FUNCTION decrypt (p_encryptedText RAW, priKey VARCHAR2) RETURN VARCHAR2 DETERMINISTIC;
END;
/

CREATE OR REPLACE PACKAGE BODY SHOE_CRYPTO
AS
  -- Define encryption type: DES + CBC chaining + PKCS5 padding
  encryption_type    PLS_INTEGER := DBMS_CRYPTO.ENCRYPT_DES
                                    + DBMS_CRYPTO.CHAIN_CBC
                                    + DBMS_CRYPTO.PAD_PKCS5;

  FUNCTION encrypt (p_plainText VARCHAR2, priKey VARCHAR2) RETURN RAW DETERMINISTIC
  IS
    encryption_key RAW (32) := UTL_RAW.cast_to_raw(priKey); -- Private Key DES
    encrypted_raw RAW (2000);
  BEGIN
    IF p_plainText IS NULL OR LENGTH(p_plainText) = 0 THEN
      RETURN NULL;
    END IF;

    -- Perform encryption using DBMS_CRYPTO
    encrypted_raw := DBMS_CRYPTO.ENCRYPT
    (
      src => UTL_RAW.CAST_TO_RAW (p_plainText),
      typ => encryption_type,
      key => encryption_key
    );
    RETURN encrypted_raw;
  END encrypt;

  FUNCTION decrypt (p_encryptedText RAW, priKey VARCHAR2) RETURN VARCHAR2 DETERMINISTIC
  IS
    encryption_key RAW (32) := UTL_RAW.cast_to_raw(priKey); -- Private Key DES
    decrypted_raw      RAW (2000);
  BEGIN
    IF p_encryptedText IS NULL THEN
      RETURN NULL;
    END IF;

    -- Perform decryption using DBMS_CRYPTO
    decrypted_raw := DBMS_CRYPTO.DECRYPT
    (
      src => p_encryptedText,
      typ => encryption_type,
      key => encryption_key
    );
    RETURN (UTL_RAW.CAST_TO_VARCHAR2 (decrypted_raw));
  END decrypt;

END SHOE_CRYPTO;
/

-- ================================================
-- Create Trigger for Automatic Encryption
-- ================================================
-- This trigger automatically encrypts PHONE before INSERT or UPDATE
-- Even if developers bypass the application layer, encryption still occurs
CREATE OR REPLACE TRIGGER TRG_ADDRESSES_ENCRYPT_PHONE
BEFORE INSERT OR UPDATE OF PHONE ON ADDRESSES
FOR EACH ROW
DECLARE
  v_encryption_key VARCHAR2(32) := 'ShoeStoreAES128'; -- Same key as application layer
  v_encrypted_raw RAW(2000);
BEGIN
  -- Only encrypt if PHONE is not NULL and not already encrypted
  IF :NEW.PHONE IS NOT NULL THEN
    -- Check if the value appears to be plaintext (not encrypted)
    -- Encrypted values will be much longer and contain non-printable characters
    -- Simple heuristic: if length is reasonable for a phone number, encrypt it
    IF LENGTH(:NEW.PHONE) <= 20 THEN
      -- Encrypt the phone number using the package function
      v_encrypted_raw := SHOE_CRYPTO.encrypt(:NEW.PHONE, v_encryption_key);

      -- Store as Base64-encoded string for database storage
      -- UTL_ENCODE.BASE64_ENCODE converts RAW to Base64 VARCHAR2
      :NEW.PHONE := UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_ENCODE(v_encrypted_raw));
    END IF;
    -- If already encrypted (length > 20), leave it as is
  END IF;
END;
/

-- ================================================
-- Verification Comments
-- ================================================
-- To test encryption manually:
-- SELECT SHOE_CRYPTO.encrypt('0123456789', 'ShoeStoreAES128') FROM DUAL;
--
-- To test decryption manually:
-- SELECT SHOE_CRYPTO.decrypt(SHOE_CRYPTO.encrypt('0123456789', 'ShoeStoreAES128'), 'ShoeStoreAES128') FROM DUAL;
--
-- To verify trigger works:
-- INSERT INTO ADDRESSES (USER_ID, FULL_NAME, PHONE, TEN_DUONG, XA_QUAN, TINH_THANH, IS_DEFAULT)
-- VALUES (1, 'Test User', '0987654321', '123 Test St', 'District 1', 'Ho Chi Minh', 0);
--
-- Then check: SELECT PHONE, LENGTH(PHONE) FROM ADDRESSES WHERE FULL_NAME = 'Test User';
-- The PHONE should be encrypted (long Base64 string)


