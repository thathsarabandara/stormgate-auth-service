package com.thathsara.authservice.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.authservice.auth_service.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long>  {
    
}
