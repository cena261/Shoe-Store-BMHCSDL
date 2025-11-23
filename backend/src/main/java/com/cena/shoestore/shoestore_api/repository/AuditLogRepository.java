package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a ORDER BY a.eventTime DESC")
    List<AuditLog> findRecentLogs(Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.objectName = :objectName ORDER BY a.eventTime DESC")
    List<AuditLog> findByObjectName(@Param("objectName") String objectName, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.username = :username ORDER BY a.eventTime DESC")
    List<AuditLog> findByUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.orderId = :orderId ORDER BY a.eventTime DESC")
    List<AuditLog> findByOrderId(@Param("orderId") Long orderId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:objectName IS NULL OR a.objectName = :objectName) AND " +
           "(:username IS NULL OR a.username = :username) AND " +
           "(:action IS NULL OR a.action = :action) " +
           "ORDER BY a.eventTime DESC")
    List<AuditLog> findByFilters(
            @Param("objectName") String objectName,
            @Param("username") String username,
            @Param("action") String action,
            Pageable pageable
    );
}
