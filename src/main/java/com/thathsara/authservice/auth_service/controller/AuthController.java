package com.thathsara.authservice.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/register")
    public ResponseEntity<String> register(@ModelAttribute RegisterRequest request) {
        System.out.println("Regiester request recieved: " + request);
        return ResponseEntity.ok("user registered");
    }
}
