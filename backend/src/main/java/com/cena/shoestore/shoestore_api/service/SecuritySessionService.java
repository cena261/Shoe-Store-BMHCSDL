package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.SecurityContextResponse;
import com.cena.shoestore.shoestore_api.dto.response.SessionAuditResponse;
import com.cena.shoestore.shoestore_api.entity.SessionAudit;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.SessionAuditRepository;
import com.cena.shoestore.shoestore_api.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecuritySessionService {

    SessionAuditRepository sessionAuditRepository;
    UserRepository userRepository;
    EntityManager entityManager;

    @Transactional
    public void logLogin(User user, HttpServletRequest request) {
        try {
            log.info("Logging session for user: {}", user.getEmail());

            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            Map<String, String> dbContext = getDbContextInfo();

            SessionAudit sessionAudit = SessionAudit.builder()
                    .user(user)
                    .username(user.getEmail())
                    .loginAt(Instant.now())
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .dbUser(dbContext.get("DB_USER"))
                    .clientIdentifier(dbContext.get("CLIENT_IDENTIFIER"))
                    .build();

            sessionAuditRepository.save(sessionAudit);

            log.info("Session logged successfully for user: {}, IP: {}, DB User: {}",
                    user.getEmail(), ipAddress, dbContext.get("DB_USER"));

        } catch (Exception e) {
            log.error("Failed to log session for user: {}", user.getEmail(), e);
        }
    }

    @Transactional(readOnly = true)
    public SecurityContextResponse getCurrentSecurityContext() {
        log.info("Retrieving current security context");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SessionAudit latestSession = sessionAuditRepository.findLatestByUserId(currentUser.getUserId())
                .orElse(null);

        Map<String, String> dbContext = getDbContextInfo();
        Map<String, String> tablespaceContext = getTablespaceAndProfileInfo();

        SecurityContextResponse.SecurityContextResponseBuilder responseBuilder = SecurityContextResponse.builder()
                .applicationUsername(currentUser.getEmail())
                .userId(currentUser.getUserId())
                .dbUser(dbContext.get("CURRENT_USER"))
                .sessionUser(dbContext.get("SESSION_USER"))
                .dbName(dbContext.get("DB_NAME"))
                .dbHost(dbContext.get("HOST"))
                .dbSessionId(dbContext.get("SESSION_ID"))
                .clientIdentifier(dbContext.get("CLIENT_IDENTIFIER"))
                .defaultTablespace(tablespaceContext.get("DEFAULT_TABLESPACE"))
                .temporaryTablespace(tablespaceContext.get("TEMPORARY_TABLESPACE"))
                .profileName(tablespaceContext.get("PROFILE"));

        if (latestSession != null) {
            responseBuilder
                    .lastLoginAt(latestSession.getLoginAt())
                    .lastLoginIp(latestSession.getIpAddress())
                    .lastLoginUserAgent(latestSession.getUserAgent());
        }

        SecurityContextResponse response = responseBuilder.build();

        log.info("Security context retrieved successfully for user: {}, DB User: {}, Tablespace: {}",
                currentUser.getEmail(), response.getDbUser(), response.getDefaultTablespace());

        return response;
    }

    private Map<String, String> getDbContextInfo() {
        try {
            String sql = "SELECT " +
                    "SYS_CONTEXT('USERENV', 'CURRENT_USER') AS CURRENT_USER, " +
                    "SYS_CONTEXT('USERENV', 'SESSION_USER') AS SESSION_USER, " +
                    "SYS_CONTEXT('USERENV', 'DB_NAME') AS DB_NAME, " +
                    "SYS_CONTEXT('USERENV', 'HOST') AS HOST, " +
                    "SYS_CONTEXT('USERENV', 'SESSIONID') AS SESSION_ID, " +
                    "SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER') AS CLIENT_IDENTIFIER " +
                    "FROM DUAL";

            Query query = entityManager.createNativeQuery(sql);
            Object[] result = (Object[]) query.getSingleResult();

            return Map.of(
                    "CURRENT_USER", result[0] != null ? result[0].toString() : "",
                    "SESSION_USER", result[1] != null ? result[1].toString() : "",
                    "DB_NAME", result[2] != null ? result[2].toString() : "",
                    "HOST", result[3] != null ? result[3].toString() : "",
                    "SESSION_ID", result[4] != null ? result[4].toString() : "",
                    "CLIENT_IDENTIFIER", result[5] != null ? result[5].toString() : "",
                    "DB_USER", result[0] != null ? result[0].toString() : ""
            );
        } catch (Exception e) {
            log.error("Failed to retrieve database context information", e);
            return Map.of(
                    "CURRENT_USER", "ERROR",
                    "SESSION_USER", "ERROR",
                    "DB_NAME", "ERROR",
                    "HOST", "ERROR",
                    "SESSION_ID", "ERROR",
                    "CLIENT_IDENTIFIER", "ERROR",
                    "DB_USER", "ERROR"
            );
        }
    }

    private Map<String, String> getTablespaceAndProfileInfo() {
        try {
            String sql = "SELECT DEFAULT_TABLESPACE, TEMPORARY_TABLESPACE " +
                    "FROM USER_USERS WHERE ROWNUM = 1";

            Query query = entityManager.createNativeQuery(sql);
            Object[] result = (Object[]) query.getSingleResult();

            return Map.of(
                    "DEFAULT_TABLESPACE", result[0] != null ? result[0].toString() : "",
                    "TEMPORARY_TABLESPACE", result[1] != null ? result[1].toString() : "",
                    "PROFILE", "DEFAULT"
            );
        } catch (Exception e) {
            log.error("Failed to retrieve tablespace and profile information", e);
            return Map.of(
                    "DEFAULT_TABLESPACE", "ERROR",
                    "TEMPORARY_TABLESPACE", "ERROR",
                    "PROFILE", "DEFAULT"
            );
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    @Transactional(readOnly = true)
    public List<SessionAuditResponse> getSessionAuditLogs(Integer limit, Long userId, String username) {
        log.info("Retrieving session audit logs - limit: {}, userId: {}, username: {}",
                limit, userId, username);

        List<SessionAudit> sessions;

        if (userId != null) {
            sessions = sessionAuditRepository.findRecentSessionsByUserId(userId, limit);
            log.info("Found {} sessions for userId: {}", sessions.size(), userId);
        } else if (username != null) {
            sessions = sessionAuditRepository.findRecentSessionsByUsername(username, limit);
            log.info("Found {} sessions for username: {}", sessions.size(), username);
        } else {
            sessions = sessionAuditRepository.findRecentSessions(limit);
            log.info("Found {} recent sessions", sessions.size());
        }

        return sessions.stream()
                .map(this::mapToSessionAuditResponse)
                .collect(Collectors.toList());
    }

    private SessionAuditResponse mapToSessionAuditResponse(SessionAudit sessionAudit) {
        return SessionAuditResponse.builder()
                .sessionId(sessionAudit.getSessionId())
                .userId(sessionAudit.getUser().getUserId())
                .username(sessionAudit.getUsername())
                .loginAt(sessionAudit.getLoginAt())
                .ipAddress(sessionAudit.getIpAddress())
                .userAgent(sessionAudit.getUserAgent())
                .dbUser(sessionAudit.getDbUser())
                .clientIdentifier(sessionAudit.getClientIdentifier())
                .build();
    }
}
