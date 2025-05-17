package com.thathsara.authservice.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class ResetPasswordResponse {
    private String resetToken;
    private String message;
}
