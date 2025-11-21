package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByEmailAndCodeAndIsUsed(String email, String code, Integer isUsed);

    void deleteByExpiresAtBefore(Instant expiresAt);

    void deleteByEmail(String email);
}
