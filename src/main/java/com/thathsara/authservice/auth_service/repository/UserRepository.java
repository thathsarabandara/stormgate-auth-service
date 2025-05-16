package com.thathsara.authservice.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thathsara.authservice.auth_service.model.User;

public interface  UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existByEmail(String email);
}
