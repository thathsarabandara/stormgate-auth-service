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
import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.dto.VerifyOtpRequest;
import com.thathsara.authservice.auth_service.dto.VerifyOtpResponse;
import com.thathsara.authservice.auth_service.service.RegisterService;
import com.thathsara.authservice.auth_service.service.VerifyMeService;
import com.thathsara.authservice.auth_service.service.VerifyService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private RegisterService registerService;
    @Autowired private VerifyService verifyService;
    @Autowired private VerifyMeService verifyMeService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@ModelAttribute RegisterRequest request) {
        return  registerService.register(request);
    }
    @PostMapping("/verify")
    public ResponseEntity<VerifyOtpResponse> verify(@ModelAttribute VerifyOtpRequest request) {
        return verifyService.verify(request);
    }
    @PostMapping("/me")
    public ResponseEntity<AuthResponse> verifyme(
        @RequestHeader(value = "Authorization", required = false) String accessToken,
        @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {

        System.out.println("Authorization header: " + accessToken);
        System.out.println("Refresh-Token header: " + refreshToken);

        if (accessToken == null) {
            System.out.println("No Authorization header received");
        }

        return verifyMeService.VerifyMe(accessToken, refreshToken);
    }

}
