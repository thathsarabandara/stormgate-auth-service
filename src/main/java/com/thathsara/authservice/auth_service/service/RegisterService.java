package com.thathsara.authservice.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.exception.CustomException;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.repository.UserRepository;
import com.thathsara.authservice.auth_service.util.TenantContext;

@Service
public class RegisterService {
    @Autowired private UserRepository userRepository;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already in use");
        }

        final User user = User.builder()
                        .email(request.getEmail())
                        .username(request.getUsername())
                        .tenantID(TenantContext.getTenantId())
                        .build();

        return new RegisterResponse(user, "registered successfully");
    }
}
