package com.thathsara.authservice.auth_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.authservice.auth_service.model.AuthToken;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.UserPassword;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long>  {
    List<UserPassword> findByUser(User user);
}
