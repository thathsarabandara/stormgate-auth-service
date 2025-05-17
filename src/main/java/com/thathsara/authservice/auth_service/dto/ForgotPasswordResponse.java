package com.thathsara.authservice.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForgotPasswordResponse {
    private String resetToken;
    private String message;
}
