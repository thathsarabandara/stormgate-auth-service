package com.thathsara.authservice.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.exception.CustomException;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.repository.UserRepository;

@Service
public class RegisterService {
    @Autowired private UserRepository userRepository;

    public RegisterResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw  new CustomException("Email already in use");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setTenantID(request.get);
    }
}
