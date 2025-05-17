package com.thathsara.authservice.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.dto.VerifyOtpRequest;
import com.thathsara.authservice.auth_service.dto.VerifyOtpResponse;
import com.thathsara.authservice.auth_service.service.RegisterService;
import com.thathsara.authservice.auth_service.service.VerifyService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private RegisterService registerService;
    @Autowired private VerifyService verifyService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@ModelAttribute RegisterRequest request) {
        final RegisterResponse response = registerService.register(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verify(@ModelAttribute VerifyOtpRequest request) {
        final VerifyOtpResponse response = verifyService.verify(request);
        return ResponseEntity.ok(response);
    }
}
