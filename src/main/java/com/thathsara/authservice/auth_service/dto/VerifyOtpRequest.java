package com.thathsara.authservice.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank
    private String verifyToken;

    @NotBlank
    private String otp;
}
