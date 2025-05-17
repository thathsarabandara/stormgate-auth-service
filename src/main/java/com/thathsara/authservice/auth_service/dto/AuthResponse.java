package com.thathsara.authservice.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String authToken;
    private String refreshToken;
    private Long userId;
    private String message;
}
