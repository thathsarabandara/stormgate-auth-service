package com.thathsara.authservice.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.exception.CustomException;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.UserPassword;
import com.thathsara.authservice.auth_service.repository.UserPasswordRepository;
import com.thathsara.authservice.auth_service.repository.UserRepository;
import com.thathsara.authservice.auth_service.util.TenantContext;

@Service
public class RegisterService {
    @Autowired private UserRepository userRepository;
    @Autowired private UserPasswordRepository userPasswordRepository;

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already in use");
        }

        final User user = User.builder()
                        .tenantID(TenantContext.getTenantId())
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .isVerified(false)
                        .status(User.STATUS.PENDING)
                        .build();

        userRepository.save(user);


        final UserPassword userPassword = UserPassword.builder()
                                        .user(user)
                                        .password(request.getPassword())
                                        .isActive(true)
                                        .build();

        userPasswordRepository.save(userPassword);

        return new RegisterResponse("registered successfully");
    }
}
