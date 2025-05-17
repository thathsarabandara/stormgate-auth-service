package com.thathsara.authservice.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpResendResponse {
    private String verifyToken;
    private String message;
}
