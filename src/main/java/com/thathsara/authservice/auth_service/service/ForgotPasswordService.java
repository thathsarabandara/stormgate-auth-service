package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.ForgotPasswordResponse;
import com.thathsara.authservice.auth_service.model.PasswordResetToken;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.repository.PasswordResetTokenRepository;
import com.thathsara.authservice.auth_service.repository.UserRepository;
import com.thathsara.authservice.auth_service.util.JwtUtil;
import com.thathsara.authservice.auth_service.util.OTPUtil;

import jakarta.transaction.Transactional;

@Service
public class ForgotPasswordService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired private MailService mailService;
    @Autowired private JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ForgotPasswordResponse(null , "No account registered with this email."));
            }

            User user = userOpt.get();

            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ForgotPasswordResponse(null , "Account is not verified."));
            }

            // Generate Reset Token and OTP
            String resetToken = jwtUtil.generateToken(user.getId());
            String otp = OTPUtil.generateOTP();

            // Save PasswordResetToken
            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .user(user)
                    .resetToken(resetToken)
                    .otp(otp)
                    .expiredAt(LocalDateTime.now().plusMinutes(30))
                    .build();

            passwordResetTokenRepository.save(passwordResetToken);

            // Send Email with token + otp
            emailService.sendForgotPasswordEmail(user.getEmail(), otp, resetToken);

            return ResponseEntity.ok(new ForgotPasswordResponse(resetToken ,"Reset token and OTP sent to your email."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ForgotPasswordResponse(null , "Failed to process password reset request."));
        }
    }
}
