ALTER TABLE PASSWORDRESETTOKENS MODIFY (CODE NVARCHAR2(2000));

CREATE OR REPLACE PACKAGE SHOE_ASYM_CRYPTO AS

    FUNCTION RSA_GENERATE_KEYS(p_key_size IN NUMBER DEFAULT 1024) RETURN VARCHAR2;

    FUNCTION RSA_ENCRYPT(
        p_plain_text IN VARCHAR2,
        p_public_key IN VARCHAR2
    ) RETURN VARCHAR2;

    FUNCTION RSA_DECRYPT(
        p_encrypted_text IN VARCHAR2,
        p_private_key IN VARCHAR2
    ) RETURN VARCHAR2;

    FUNCTION GET_PUBLIC_KEY RETURN VARCHAR2;

    FUNCTION GET_PRIVATE_KEY RETURN VARCHAR2;

END SHOE_ASYM_CRYPTO;
/

CREATE OR REPLACE PACKAGE BODY SHOE_ASYM_CRYPTO AS

    g_public_key VARCHAR2(4000);
    g_private_key VARCHAR2(4000);
    g_modulus RAW(256);
    g_exponent RAW(256);
    g_private_exponent RAW(256);

    FUNCTION GENERATE_PRIME(p_bit_size IN NUMBER) RETURN NUMBER IS
        v_candidate NUMBER;
        v_is_prime BOOLEAN;
        v_test_count NUMBER;
    BEGIN
        LOOP
            v_candidate := TRUNC(DBMS_RANDOM.VALUE(POWER(2, p_bit_size - 1), POWER(2, p_bit_size)));

            IF MOD(v_candidate, 2) = 0 THEN
                v_candidate := v_candidate + 1;
            END IF;

            v_is_prime := TRUE;
            v_test_count := 0;

            FOR i IN 2..LEAST(1000, SQRT(v_candidate)) LOOP
                IF MOD(v_candidate, i) = 0 THEN
                    v_is_prime := FALSE;
                    EXIT;
                END IF;
                v_test_count := v_test_count + 1;
                IF v_test_count > 100 THEN
                    EXIT;
                END IF;
            END LOOP;

            EXIT WHEN v_is_prime;
        END LOOP;

        RETURN v_candidate;
    END GENERATE_PRIME;

    FUNCTION GCD(p_a IN NUMBER, p_b IN NUMBER) RETURN NUMBER IS
        v_a NUMBER := p_a;
        v_b NUMBER := p_b;
        v_temp NUMBER;
    BEGIN
        WHILE v_b != 0 LOOP
            v_temp := v_b;
            v_b := MOD(v_a, v_b);
            v_a := v_temp;
        END LOOP;
        RETURN v_a;
    END GCD;

    FUNCTION MOD_INVERSE(p_a IN NUMBER, p_m IN NUMBER) RETURN NUMBER IS
        v_m0 NUMBER := p_m;
        v_x0 NUMBER := 0;
        v_x1 NUMBER := 1;
        v_q NUMBER;
        v_t NUMBER;
    BEGIN
        IF p_m = 1 THEN
            RETURN 0;
        END IF;

        WHILE p_a > 1 LOOP
            v_q := TRUNC(p_a / p_m);
            v_t := p_m;
            p_m := MOD(p_a, p_m);
            p_a := v_t;
            v_t := v_x0;
            v_x0 := v_x1 - v_q * v_x0;
            v_x1 := v_t;
        END LOOP;

        IF v_x1 < 0 THEN
            v_x1 := v_x1 + v_m0;
        END IF;

        RETURN v_x1;
    END MOD_INVERSE;

    FUNCTION POWER_MOD(p_base IN NUMBER, p_exp IN NUMBER, p_mod IN NUMBER) RETURN NUMBER IS
        v_result NUMBER := 1;
        v_base NUMBER := MOD(p_base, p_mod);
        v_exp NUMBER := p_exp;
    BEGIN
        WHILE v_exp > 0 LOOP
            IF MOD(v_exp, 2) = 1 THEN
                v_result := MOD(v_result * v_base, p_mod);
            END IF;
            v_exp := TRUNC(v_exp / 2);
            v_base := MOD(v_base * v_base, p_mod);
        END LOOP;
        RETURN v_result;
    END POWER_MOD;

    FUNCTION RSA_GENERATE_KEYS(p_key_size IN NUMBER DEFAULT 1024) RETURN VARCHAR2 IS
        v_p NUMBER;
        v_q NUMBER;
        v_n NUMBER;
        v_phi NUMBER;
        v_e NUMBER := 65537;
        v_d NUMBER;
        v_result VARCHAR2(4000);
    BEGIN
        v_p := GENERATE_PRIME(p_key_size / 2);
        v_q := GENERATE_PRIME(p_key_size / 2);

        v_n := v_p * v_q;
        v_phi := (v_p - 1) * (v_q - 1);

        WHILE GCD(v_e, v_phi) != 1 LOOP
            v_e := v_e + 2;
        END LOOP;

        v_d := MOD_INVERSE(v_e, v_phi);

        g_public_key := TO_CHAR(v_e) || ',' || TO_CHAR(v_n);
        g_private_key := TO_CHAR(v_d) || ',' || TO_CHAR(v_n);

        v_result := '****publicKey start****,' ||
                    '****publicKey end**** ****privateKey start****,' ||
                    '****privateKey end**** do not copy asteric(*) ****';

        RETURN v_result;
    END RSA_GENERATE_KEYS;

    FUNCTION STRING_TO_NUMBER_ARRAY(p_text IN VARCHAR2) RETURN DBMS_SQL.NUMBER_TABLE IS
        v_result DBMS_SQL.NUMBER_TABLE;
    BEGIN
        FOR i IN 1..LENGTH(p_text) LOOP
            v_result(i) := ASCII(SUBSTR(p_text, i, 1));
        END LOOP;
        RETURN v_result;
    END STRING_TO_NUMBER_ARRAY;

    FUNCTION NUMBER_ARRAY_TO_STRING(p_array IN DBMS_SQL.NUMBER_TABLE) RETURN VARCHAR2 IS
        v_result VARCHAR2(32767) := '';
    BEGIN
        FOR i IN 1..p_array.COUNT LOOP
            v_result := v_result || CHR(p_array(i));
        END LOOP;
        RETURN v_result;
    END NUMBER_ARRAY_TO_STRING;

    FUNCTION RSA_ENCRYPT(
        p_plain_text IN VARCHAR2,
        p_public_key IN VARCHAR2
    ) RETURN VARCHAR2 IS
        v_e NUMBER;
        v_n NUMBER;
        v_comma_pos NUMBER;
        v_plain_numbers DBMS_SQL.NUMBER_TABLE;
        v_encrypted_numbers DBMS_SQL.NUMBER_TABLE;
        v_result VARCHAR2(4000) := '';
    BEGIN
        v_comma_pos := INSTR(p_public_key, ',');
        v_e := TO_NUMBER(SUBSTR(p_public_key, 1, v_comma_pos - 1));
        v_n := TO_NUMBER(SUBSTR(p_public_key, v_comma_pos + 1));

        v_plain_numbers := STRING_TO_NUMBER_ARRAY(p_plain_text);

        FOR i IN 1..v_plain_numbers.COUNT LOOP
            v_encrypted_numbers(i) := POWER_MOD(v_plain_numbers(i), v_e, v_n);
            IF i > 1 THEN
                v_result := v_result || ' ';
            END IF;
            v_result := v_result || TO_CHAR(v_encrypted_numbers(i));
        END LOOP;

        RETURN v_result;
    END RSA_ENCRYPT;

    FUNCTION RSA_DECRYPT(
        p_encrypted_text IN VARCHAR2,
        p_private_key IN VARCHAR2
    ) RETURN VARCHAR2 IS
        v_d NUMBER;
        v_n NUMBER;
        v_comma_pos NUMBER;
        v_encrypted_numbers DBMS_SQL.NUMBER_TABLE;
        v_decrypted_numbers DBMS_SQL.NUMBER_TABLE;
        v_start_pos NUMBER := 1;
        v_space_pos NUMBER;
        v_index NUMBER := 0;
        v_result VARCHAR2(4000) := '';
    BEGIN
        v_comma_pos := INSTR(p_private_key, ',');
        v_d := TO_NUMBER(SUBSTR(p_private_key, 1, v_comma_pos - 1));
        v_n := TO_NUMBER(SUBSTR(p_private_key, v_comma_pos + 1));

        LOOP
            v_space_pos := INSTR(p_encrypted_text, ' ', v_start_pos);
            v_index := v_index + 1;

            IF v_space_pos = 0 THEN
                v_encrypted_numbers(v_index) := TO_NUMBER(SUBSTR(p_encrypted_text, v_start_pos));
                EXIT;
            ELSE
                v_encrypted_numbers(v_index) := TO_NUMBER(SUBSTR(p_encrypted_text, v_start_pos, v_space_pos - v_start_pos));
                v_start_pos := v_space_pos + 1;
            END IF;
        END LOOP;

        FOR i IN 1..v_encrypted_numbers.COUNT LOOP
            v_decrypted_numbers(i) := POWER_MOD(v_encrypted_numbers(i), v_d, v_n);
            v_result := v_result || CHR(v_decrypted_numbers(i));
        END LOOP;

        RETURN v_result;
    END RSA_DECRYPT;

    FUNCTION GET_PUBLIC_KEY RETURN VARCHAR2 IS
    BEGIN
        RETURN g_public_key;
    END GET_PUBLIC_KEY;

    FUNCTION GET_PRIVATE_KEY RETURN VARCHAR2 IS
    BEGIN
        RETURN g_private_key;
    END GET_PRIVATE_KEY;

BEGIN
    DECLARE
        v_keys VARCHAR2(4000);
    BEGIN
        v_keys := RSA_GENERATE_KEYS(1024);
    END;
END SHOE_ASYM_CRYPTO;
/

CREATE OR REPLACE TRIGGER TRG_PASSWORDRESET_RSA_ENCRYPT
BEFORE INSERT OR UPDATE ON PASSWORDRESETTOKENS
FOR EACH ROW
DECLARE
    v_public_key VARCHAR2(4000);
    v_encrypted_code CLOB;
    v_original_code VARCHAR2(2000);
    v_is_already_encrypted BOOLEAN := FALSE;
BEGIN
    IF :NEW.CODE IS NOT NULL AND (:OLD.CODE IS NULL OR :NEW.CODE != :OLD.CODE) THEN
        v_original_code := :NEW.CODE;

        v_is_already_encrypted := LENGTH(v_original_code) > 100 OR
                                  REGEXP_LIKE(v_original_code, '^[A-Za-z0-9+/=]+$');

        IF NOT v_is_already_encrypted THEN
            v_public_key := SHOE_ASYM_CRYPTO.GET_PUBLIC_KEY();

            IF v_public_key IS NULL THEN
                DECLARE
                    v_keys VARCHAR2(4000);
                BEGIN
                    v_keys := SHOE_ASYM_CRYPTO.RSA_GENERATE_KEYS(1024);
                    v_public_key := SHOE_ASYM_CRYPTO.GET_PUBLIC_KEY();
                END;
            END IF;

            v_encrypted_code := SHOE_ASYM_CRYPTO.RSA_ENCRYPT(v_original_code, v_public_key);

            IF LENGTH(v_encrypted_code) <= 2000 THEN
                :NEW.CODE := v_encrypted_code;
            END IF;
        END IF;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        NULL;
END;
/
