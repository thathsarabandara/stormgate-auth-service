package com.thathsara.authservice.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.service.RegisterService;
import com.thathsara.authservice.auth_service.util.TenantContext;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private RegisterService registerService;
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@ModelAttribute RegisterRequest request) {
        System.out.println("Regiester request recieved: " + request + TenantContext.getTenantId());
        final RegisterResponse response = registerService.register(request);
        return ResponseEntity.ok(response);
    }
}
