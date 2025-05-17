package com.thathsara.authservice.auth_service.repository;

import com.thathsara.authservice.auth_service.model.RefreshToken;
import com.thathsara.authservice.auth_service.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface  RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
     List<RefreshToken> findByUser(User user);
}
