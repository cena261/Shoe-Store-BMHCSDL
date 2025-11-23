package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.SessionAudit;
import com.cena.shoestore.shoestore_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionAuditRepository extends JpaRepository<SessionAudit, Long> {

    @Query("SELECT sa FROM SessionAudit sa WHERE sa.user = :user ORDER BY sa.loginAt DESC")
    List<SessionAudit> findByUserOrderByLoginAtDesc(@Param("user") User user);

    @Query("SELECT sa FROM SessionAudit sa WHERE sa.user = :user ORDER BY sa.loginAt DESC LIMIT 1")
    Optional<SessionAudit> findLatestByUser(@Param("user") User user);

    @Query(value = "SELECT * FROM SESSION_AUDIT WHERE USER_ID = :userId ORDER BY LOGIN_AT DESC FETCH FIRST 1 ROWS ONLY", nativeQuery = true)
    Optional<SessionAudit> findLatestByUserId(@Param("userId") Long userId);
}
