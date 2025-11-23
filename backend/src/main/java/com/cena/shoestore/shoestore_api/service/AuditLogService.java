package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.AuditLogResponse;
import com.cena.shoestore.shoestore_api.entity.AuditLog;
import com.cena.shoestore.shoestore_api.repository.AuditLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditLogService {

    AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getRecentAuditLogs(int limit) {
        log.info("Retrieving {} recent audit logs", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<AuditLog> logs = auditLogRepository.findRecentLogs(pageable);

        log.info("Retrieved {} audit logs", logs.size());

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByObjectName(String objectName, int limit) {
        log.info("Retrieving {} audit logs for object: {}", limit, objectName);

        Pageable pageable = PageRequest.of(0, limit);
        List<AuditLog> logs = auditLogRepository.findByObjectName(objectName, pageable);

        log.info("Retrieved {} audit logs for object: {}", logs.size(), objectName);

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByUser(String username, int limit) {
        log.info("Retrieving {} audit logs for user: {}", limit, username);

        Pageable pageable = PageRequest.of(0, limit);
        List<AuditLog> logs = auditLogRepository.findByUsername(username, pageable);

        log.info("Retrieved {} audit logs for user: {}", logs.size(), username);

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByOrderId(Long orderId, int limit) {
        log.info("Retrieving {} audit logs for order ID: {}", limit, orderId);

        Pageable pageable = PageRequest.of(0, limit);
        List<AuditLog> logs = auditLogRepository.findByOrderId(orderId, pageable);

        log.info("Retrieved {} audit logs for order ID: {}", logs.size(), orderId);

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByFilters(
            String objectName,
            String username,
            String action,
            int limit) {
        log.info("Retrieving {} audit logs with filters - Object: {}, User: {}, Action: {}",
                limit, objectName, username, action);

        Pageable pageable = PageRequest.of(0, limit);
        List<AuditLog> logs = auditLogRepository.findByFilters(objectName, username, action, pageable);

        log.info("Retrieved {} audit logs with filters", logs.size());

        return logs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .auditId(auditLog.getAuditId())
                .eventTime(auditLog.getEventTime())
                .username(auditLog.getUsername())
                .ipAddress(auditLog.getIpAddress())
                .host(auditLog.getHost())
                .objectName(auditLog.getObjectName())
                .action(auditLog.getAction())
                .orderId(auditLog.getOrderId())
                .details(auditLog.getDetails())
                .build();
    }
}
