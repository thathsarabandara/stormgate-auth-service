package com.thathsara.authservice.auth_service.dto;

import com.thathsara.authservice.auth_service.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterResponse {
    private User user;
    private String message;
}
