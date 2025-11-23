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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DbSessionContextService {

    EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setClientIdentifier(Long userId, Set<String> roles) {
        try {
            String identifier;

            if (roles != null && roles.contains("Admin")) {
                identifier = "ADMIN";
                log.debug("Setting CLIENT_IDENTIFIER to ADMIN for admin user");
            } else if (userId != null) {
                identifier = userId.toString();
                log.debug("Setting CLIENT_IDENTIFIER to {} for regular user", userId);
            } else {
                log.warn("No userId or admin role found, setting CLIENT_IDENTIFIER to empty");
                clearClientIdentifier();
                return;
            }

            String sql = "BEGIN DBMS_SESSION.SET_IDENTIFIER(:p_user_id); END;";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("p_user_id", identifier);
            query.executeUpdate();

            log.info("CLIENT_IDENTIFIER set successfully: {}", identifier);

        } catch (Exception e) {
            log.error("Failed to set CLIENT_IDENTIFIER for userId: {}", userId, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearClientIdentifier() {
        try {
            String sql = "BEGIN DBMS_SESSION.CLEAR_IDENTIFIER; END;";
            Query query = entityManager.createNativeQuery(sql);
            query.executeUpdate();

            log.debug("CLIENT_IDENTIFIER cleared successfully");

        } catch (Exception e) {
            log.error("Failed to clear CLIENT_IDENTIFIER", e);
        }
    }

    public String getCurrentClientIdentifier() {
        try {
            String sql = "SELECT SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER') FROM DUAL";
            Query query = entityManager.createNativeQuery(sql);
            Object result = query.getSingleResult();

            return result != null ? result.toString() : null;

        } catch (Exception e) {
            log.error("Failed to get current CLIENT_IDENTIFIER", e);
            return null;
        }
    }
}
