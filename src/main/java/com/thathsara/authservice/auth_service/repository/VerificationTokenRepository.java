package com.thathsara.authservice.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thathsara.authservice.auth_service.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Query("SELECT v FROM VerificationToken v WHERE v.verifyToken = :token AND v.expiredAt > CURRENT_TIMESTAMP")
    Optional<VerificationToken> findValidTokenByVerifyToken(String token);
}
