-- =====================================================
-- V10: Fine Grained Auditing for Sensitive Data Access
-- =====================================================

BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema   => 'SHOE_APP',
        object_name     => 'USERS',
        policy_name     => 'FGA_USERS_SENSITIVE',
        audit_column    => 'EMAIL, PHONE',
        audit_condition => 'SYS_CONTEXT(''USERENV'', ''CLIENT_IDENTIFIER'') <> ''ADMIN''',
        statement_types => 'SELECT',
        audit_trail     => DBMS_FGA.DB_EXTENDED,
        audit_column_opts => DBMS_FGA.ANY_COLUMNS
    );
END;
/

BEGIN
    DBMS_FGA.ADD_POLICY(
        object_schema   => 'SHOE_APP',
        object_name     => 'ADDRESSES',
        policy_name     => 'FGA_ADDRESSES_PHONE',
        audit_column    => 'PHONE',
        audit_condition => 'SYS_CONTEXT(''USERENV'', ''CLIENT_IDENTIFIER'') <> ''ADMIN''',
        statement_types => 'SELECT',
        audit_trail     => DBMS_FGA.DB_EXTENDED,
        audit_column_opts => DBMS_FGA.ANY_COLUMNS
    );
END;
/
