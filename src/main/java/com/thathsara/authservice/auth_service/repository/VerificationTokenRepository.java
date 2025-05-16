package com.thathsara.authservice.auth_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.authservice.auth_service.model.User;

public interface VerificationToken extends JpaRepository<VerificationToken, Long> {
    List<VerificationToken> findByUser(User user);
}
