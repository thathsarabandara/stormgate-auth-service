package com.thathsara.authservice.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thathsara.authservice.auth_service.model.AuthToken;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.UserPassword;

import io.lettuce.core.dynamic.annotation.Param;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long>  {
    @Query("SELECT v FROM AuthToken v WHERE v.authToken = :token AND v.expiredAt > CURRENT_TIMESTAMP")
    Optional<AuthToken> findValidTokenByAuthToken(@Param("token") String token);
    List<UserPassword> findByUser(User user);
}
