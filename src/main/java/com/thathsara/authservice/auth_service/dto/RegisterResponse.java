package com.thathsara.authservice.auth_service.dto;

import lombok.Data;

@Data
public class RegisterResponse {
    private String verifyToken;
    private String message;
}
