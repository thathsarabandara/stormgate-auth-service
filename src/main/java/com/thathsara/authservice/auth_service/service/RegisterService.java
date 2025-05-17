package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.UserPassword;
import com.thathsara.authservice.auth_service.model.VerificationToken;
import com.thathsara.authservice.auth_service.repository.UserPasswordRepository;
import com.thathsara.authservice.auth_service.repository.UserRepository;
import com.thathsara.authservice.auth_service.repository.VerificationTokenRepository;
import com.thathsara.authservice.auth_service.util.JwtUtil;
import com.thathsara.authservice.auth_service.util.OTPUtil;
import com.thathsara.authservice.auth_service.util.PasswordUtils;
import com.thathsara.authservice.auth_service.util.TenantContext;

@Service
public class RegisterService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserPasswordRepository userPasswordRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private VerificationTokenRepository verificationTokenRepository;
    @Autowired private MailService mailService;

    @Transactional
    public ResponseEntity<RegisterResponse> register(RegisterRequest request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null || request.getUsername() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponse(null, "Empty Credentials"));
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponse(null, "User already registered"));
            }

            final User user = User.builder()
                    .tenantID(TenantContext.getTenantId())
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .isVerified(false)
                    .status(User.STATUS.PENDING)
                    .build();

            userRepository.save(user);

            final String hashedPassword = PasswordUtils.hashedPassword(request.getPassword());
            final UserPassword userPassword = UserPassword.builder()
                    .user(user)
                    .password(hashedPassword)
                    .isActive(true)
                    .build();

            userPasswordRepository.save(userPassword);

            final String token = jwtUtil.generateToken(user.getId());
            final String otp = OTPUtil.generateOTP();

            final VerificationToken verificationToken = VerificationToken.builder()
                    .user(user)
                    .verifyToken(token)
                    .otp(otp)
                    .expiredAt(LocalDateTime.now().plusMinutes(10))
                    .build();

            verificationTokenRepository.save(verificationToken);

            mailService.sendOtpEmail(request.getEmail(),
                    "StormGate-AuthService - OTP Service - Don't Reply", otp,"verify");

            return ResponseEntity.ok(new RegisterResponse(token, "registered successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegisterResponse(null, "Registration failed due to server error"));
        }
    }
}
