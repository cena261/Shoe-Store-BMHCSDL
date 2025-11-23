package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.FgaAuditResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FgaAuditService {

    EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<FgaAuditResponse> getRecentUserFgaEvents(int limit) {
        log.info("Retrieving {} recent FGA audit events for USERS table", limit);

        String sql = "SELECT " +
                "EXTENDED_TIMESTAMP, " +
                "DB_USER, " +
                "OS_USER, " +
                "USERHOST, " +
                "OBJECT_SCHEMA, " +
                "OBJECT_NAME, " +
                "POLICY_NAME, " +
                "CLIENT_ID, " +
                "SQL_TEXT " +
                "FROM DBA_FGA_AUDIT_TRAIL " +
                "WHERE OBJECT_NAME = 'USERS' " +
                "ORDER BY EXTENDED_TIMESTAMP DESC " +
                "FETCH FIRST :limit ROWS ONLY";

        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("limit", limit);

            List<Object[]> results = query.getResultList();
            List<FgaAuditResponse> responses = new ArrayList<>();

            for (Object[] row : results) {
                FgaAuditResponse response = FgaAuditResponse.builder()
                        .eventTime(convertToInstant(row[0]))
                        .dbUser(row[1] != null ? row[1].toString() : null)
                        .osUser(row[2] != null ? row[2].toString() : null)
                        .host(row[3] != null ? row[3].toString() : null)
                        .objectSchema(row[4] != null ? row[4].toString() : null)
                        .objectName(row[5] != null ? row[5].toString() : null)
                        .policyName(row[6] != null ? row[6].toString() : null)
                        .clientIdentifier(row[7] != null ? row[7].toString() : null)
                        .sqlText(row[8] != null ? row[8].toString() : null)
                        .build();
                responses.add(response);
            }

            log.info("Retrieved {} FGA audit events for USERS table", responses.size());
            return responses;

        } catch (Exception e) {
            log.warn("Failed to retrieve FGA audit events from DBA_FGA_AUDIT_TRAIL, trying USER_FGA_AUDIT_TRAIL: {}", e.getMessage());
            return getRecentUserFgaEventsFromUserView(limit);
        }
    }

    @Transactional(readOnly = true)
    public List<FgaAuditResponse> getRecentUserFgaEventsFromUserView(int limit) {
        log.info("Retrieving {} recent FGA audit events from DBA_FGA_AUDIT_TRAIL (fallback)", limit);

        String sql = "SELECT " +
                "EXTENDED_TIMESTAMP, " +
                "DB_USER, " +
                "OS_USER, " +
                "USERHOST, " +
                "OBJECT_SCHEMA, " +
                "OBJECT_NAME, " +
                "POLICY_NAME, " +
                "CLIENT_ID, " +
                "SQL_TEXT " +
                "FROM DBA_FGA_AUDIT_TRAIL " +
                "WHERE OBJECT_NAME = 'USERS' AND OBJECT_SCHEMA = 'SHOE_APP' " +
                "ORDER BY EXTENDED_TIMESTAMP DESC " +
                "FETCH FIRST :limit ROWS ONLY";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);

        List<Object[]> results = query.getResultList();
        List<FgaAuditResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            FgaAuditResponse response = FgaAuditResponse.builder()
                    .eventTime(convertToInstant(row[0]))
                    .dbUser(row[1] != null ? row[1].toString() : null)
                    .osUser(row[2] != null ? row[2].toString() : null)
                    .host(row[3] != null ? row[3].toString() : null)
                    .objectSchema(row[4] != null ? row[4].toString() : null)
                    .objectName(row[5] != null ? row[5].toString() : null)
                    .policyName(row[6] != null ? row[6].toString() : null)
                    .clientIdentifier(row[7] != null ? row[7].toString() : null)
                    .sqlText(row[8] != null ? row[8].toString() : null)
                    .build();
            responses.add(response);
        }

        log.info("Retrieved {} FGA audit events from DBA_FGA_AUDIT_TRAIL", responses.size());
        return responses;
    }

    @Transactional(readOnly = true)
    public List<FgaAuditResponse> getFgaEventsByPolicy(String policyName, int limit) {
        log.info("Retrieving {} FGA audit events for policy: {}", limit, policyName);

        String sql = "SELECT " +
                "EXTENDED_TIMESTAMP, " +
                "DB_USER, " +
                "OS_USER, " +
                "USERHOST, " +
                "OBJECT_SCHEMA, " +
                "OBJECT_NAME, " +
                "POLICY_NAME, " +
                "CLIENT_ID, " +
                "SQL_TEXT " +
                "FROM DBA_FGA_AUDIT_TRAIL " +
                "WHERE POLICY_NAME = :policyName AND OBJECT_SCHEMA = 'SHOE_APP' " +
                "ORDER BY EXTENDED_TIMESTAMP DESC " +
                "FETCH FIRST :limit ROWS ONLY";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("policyName", policyName);
        query.setParameter("limit", limit);

        List<Object[]> results = query.getResultList();
        List<FgaAuditResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            FgaAuditResponse response = FgaAuditResponse.builder()
                    .eventTime(convertToInstant(row[0]))
                    .dbUser(row[1] != null ? row[1].toString() : null)
                    .osUser(row[2] != null ? row[2].toString() : null)
                    .host(row[3] != null ? row[3].toString() : null)
                    .objectSchema(row[4] != null ? row[4].toString() : null)
                    .objectName(row[5] != null ? row[5].toString() : null)
                    .policyName(row[6] != null ? row[6].toString() : null)
                    .clientIdentifier(row[7] != null ? row[7].toString() : null)
                    .sqlText(row[8] != null ? row[8].toString() : null)
                    .build();
            responses.add(response);
        }

        log.info("Retrieved {} FGA audit events for policy: {}", responses.size(), policyName);
        return responses;
    }

    @Transactional(readOnly = true)
    public List<FgaAuditResponse> getAllRecentFgaEvents(int limit) {
        log.info("Retrieving {} recent FGA audit events (all tables)", limit);

        String sql = "SELECT " +
                "EXTENDED_TIMESTAMP, " +
                "DB_USER, " +
                "OS_USER, " +
                "USERHOST, " +
                "OBJECT_SCHEMA, " +
                "OBJECT_NAME, " +
                "POLICY_NAME, " +
                "CLIENT_ID, " +
                "SQL_TEXT " +
                "FROM DBA_FGA_AUDIT_TRAIL " +
                "WHERE OBJECT_SCHEMA = 'SHOE_APP' " +
                "ORDER BY EXTENDED_TIMESTAMP DESC " +
                "FETCH FIRST :limit ROWS ONLY";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("limit", limit);

        List<Object[]> results = query.getResultList();
        List<FgaAuditResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            FgaAuditResponse response = FgaAuditResponse.builder()
                    .eventTime(convertToInstant(row[0]))
                    .dbUser(row[1] != null ? row[1].toString() : null)
                    .osUser(row[2] != null ? row[2].toString() : null)
                    .host(row[3] != null ? row[3].toString() : null)
                    .objectSchema(row[4] != null ? row[4].toString() : null)
                    .objectName(row[5] != null ? row[5].toString() : null)
                    .policyName(row[6] != null ? row[6].toString() : null)
                    .clientIdentifier(row[7] != null ? row[7].toString() : null)
                    .sqlText(row[8] != null ? row[8].toString() : null)
                    .build();
            responses.add(response);
        }

        log.info("Retrieved {} FGA audit events (all tables)", responses.size());
        return responses;
    }

    private Instant convertToInstant(Object timestampObj) {
        if (timestampObj == null) {
            return null;
        }
        if (timestampObj instanceof Timestamp) {
            return ((Timestamp) timestampObj).toInstant();
        }
        if (timestampObj instanceof oracle.sql.TIMESTAMP) {
            try {
                return ((oracle.sql.TIMESTAMP) timestampObj).timestampValue().toInstant();
            } catch (Exception e) {
                log.warn("Failed to convert Oracle TIMESTAMP to Instant: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }
}
