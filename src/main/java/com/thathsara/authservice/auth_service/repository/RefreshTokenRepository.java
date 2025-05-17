package com.thathsara.authservice.auth_service.repository;

import com.thathsara.authservice.auth_service.model.RefreshToken;
import com.thathsara.authservice.auth_service.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;


public interface  RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshTokenAndIsRevoked(String refreshToken, boolean isRevoked);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.isRevoked = true WHERE r.user = :user")
    void revokeAllByUser(@Param("user") User user);

    List<RefreshToken> findByUser(User user);
}
