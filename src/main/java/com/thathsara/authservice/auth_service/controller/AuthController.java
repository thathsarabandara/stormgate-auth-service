package com.thathsara.authservice.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.authservice.auth_service.dto.AuthResponse;
import com.thathsara.authservice.auth_service.dto.LogoutResponse;
import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.dto.VerifyOtpRequest;
import com.thathsara.authservice.auth_service.dto.VerifyOtpResponse;
import com.thathsara.authservice.auth_service.service.LogoutService;
import com.thathsara.authservice.auth_service.service.RegisterService;
import com.thathsara.authservice.auth_service.service.VerifyMeService;
import com.thathsara.authservice.auth_service.service.VerifyService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private RegisterService registerService;
    @Autowired private VerifyService verifyService;
    @Autowired private VerifyMeService verifyMeService;
    @Autowired private LogoutService logoutService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@ModelAttribute RegisterRequest request) {
        return  registerService.register(request);
    }
    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verify(
        @RequestHeader(value = "Verify-Token", required = true) String verifyToken,
        @ModelAttribute VerifyOtpRequest request) {
        return verifyService.verify(verifyToken , request);
    }
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> verifyme(
        @RequestHeader(value = "Authorization", required = false) String accessToken,
        @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
        return verifyMeService.VerifyMe(accessToken, refreshToken);
    }
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
        @RequestHeader(value = "Authorization", required = false) String accessToken,
        @RequestHeader(value = "Refresh-Token", required = false) String refreshToken ){
        return logoutService.logout(accessToken, refreshToken);
    } 
}
