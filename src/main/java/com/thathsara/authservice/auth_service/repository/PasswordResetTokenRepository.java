package com.thathsara.authservice.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thathsara.authservice.auth_service.model.PasswordResetToken;

public interface  PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByResetToken(String token);
    
}
