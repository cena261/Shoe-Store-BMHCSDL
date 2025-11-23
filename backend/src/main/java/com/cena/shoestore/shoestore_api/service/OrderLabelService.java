package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderLabelService {

    EntityManager entityManager;

    private static final List<String> VALID_LABELS = Arrays.asList("PUBLIC", "CONFIDENTIAL", "ADMIN_ONLY");

    @Transactional
    public void setOrderLabel(Long orderId, String label) {
        log.info("Setting label for order {} to {}", orderId, label);

        if (!VALID_LABELS.contains(label)) {
            log.error("Invalid security label: {}", label);
            throw new AppException(ErrorCode.INVALID_SECURITY_LABEL);
        }

        String checkSql = "SELECT COUNT(*) FROM ORDERS WHERE ORDERID = :orderId";
        Query checkQuery = entityManager.createNativeQuery(checkSql);
        checkQuery.setParameter("orderId", orderId);
        Number count = (Number) checkQuery.getSingleResult();

        if (count.intValue() == 0) {
            log.error("Order not found: {}", orderId);
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        String updateSql = "UPDATE ORDERS SET SECURITY_LABEL = :label WHERE ORDERID = :orderId";
        Query updateQuery = entityManager.createNativeQuery(updateSql);
        updateQuery.setParameter("label", label);
        updateQuery.setParameter("orderId", orderId);

        int rowsUpdated = updateQuery.executeUpdate();

        if (rowsUpdated == 0) {
            log.error("Failed to update order label - order may not be visible due to VPD policy");
            throw new AppException(ErrorCode.ORDER_LABEL_UPDATE_FAILED);
        }

        log.info("Successfully updated order {} label to {}", orderId, label);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getLabelSummary() {
        log.info("Retrieving order label summary");

        String sql = "SELECT SECURITY_LABEL, COUNT(*) FROM ORDERS GROUP BY SECURITY_LABEL";
        Query query = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        Map<String, Long> summary = new HashMap<>();
        for (String label : VALID_LABELS) {
            summary.put(label, 0L);
        }

        for (Object[] row : results) {
            String label = row[0] != null ? row[0].toString() : "UNKNOWN";
            Long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            summary.put(label, count);
        }

        log.info("Label summary retrieved: {}", summary);
        return summary;
    }

    @Transactional(readOnly = true)
    public String getOrderLabel(Long orderId) {
        log.debug("Retrieving label for order {}", orderId);

        String sql = "SELECT SECURITY_LABEL FROM ORDERS WHERE ORDERID = :orderId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("orderId", orderId);

        try {
            Object result = query.getSingleResult();
            return result != null ? result.toString() : "PUBLIC";
        } catch (Exception e) {
            log.error("Failed to retrieve order label for order {}", orderId, e);
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
    }

    public List<String> getValidLabels() {
        return VALID_LABELS;
    }
}
