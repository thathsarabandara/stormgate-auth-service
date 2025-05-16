package com.thathsara.authservice.auth_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.UserPassword;

public interface  UserPasswordRepository extends JpaRepository<UserPassword, Long> {
    List<UserPassword> findByUser(User user);
    Optional<UserPassword> findTopByUserOrderByCreatedAtDesc(User user);
}
