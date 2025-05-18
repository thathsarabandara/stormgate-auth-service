package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.ChangePasswordRequest;
import com.thathsara.authservice.auth_service.dto.ChangePasswordResponse;
import com.thathsara.authservice.auth_service.dto.ForgotPasswordRequest;
import com.thathsara.authservice.auth_service.dto.ForgotPasswordResponse;
import com.thathsara.authservice.auth_service.dto.ResetPasswordRequest;
import com.thathsara.authservice.auth_service.dto.ResetPasswordResponse;
import com.thathsara.authservice.auth_service.model.PasswordResetToken;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.UserPassword;
import com.thathsara.authservice.auth_service.repository.PasswordResetTokenRepository;
import com.thathsara.authservice.auth_service.repository.UserPasswordRepository;
import com.thathsara.authservice.auth_service.repository.UserRepository;
import com.thathsara.authservice.auth_service.util.JwtUtil;
import com.thathsara.authservice.auth_service.util.OTPUtil;
import com.thathsara.authservice.auth_service.util.PasswordUtils;

import jakarta.transaction.Transactional;

@Service
public class ForgotPasswordService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserPasswordRepository userPasswordRepository;
    @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired private MailService mailService;
    @Autowired private JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
        try {
            final Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ForgotPasswordResponse(null , "No account registered with this email."));
            }

            final User user = userOpt.get();

            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ForgotPasswordResponse(null , "Account is not verified."));
            }

            // Generate Reset Token and OTP
            final String resetToken = jwtUtil.generateToken(user.getId());
            final String otp = OTPUtil.generateOTP();

            // Save PasswordResetToken
            final PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .user(user)
                    .resetToken(resetToken)
                    .resetOtp(otp)
                    .expiredAt(LocalDateTime.now().plusMinutes(30))
                    .build();

            passwordResetTokenRepository.save(passwordResetToken);

            // Send Email with token + otp
            mailService.sendOtpEmail(user.getEmail(), 
            "StormGate-AuthService -Password Reset OTP Service - Don't Reply", otp, "password_reset");

            return ResponseEntity.ok(
                new ForgotPasswordResponse(resetToken , "Reset token and OTP sent to your email."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ForgotPasswordResponse(null , "Failed to process password reset request."));
        }
    }
    @Transactional
    public ResponseEntity<ResetPasswordResponse> handleResetPasswordRequest(
        String resetToken, ResetPasswordRequest request) {
        try {
            // Check if reset token exists
            final Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByResetToken(resetToken);

            if (tokenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResetPasswordResponse(null, "Invalid or expired reset token."));
            }

            final PasswordResetToken passwordResetToken = tokenOpt.get();
            System.out.println(passwordResetToken);
            System.out.println(request.getResetOTP());
            final User user = passwordResetToken.getUser();

            // Check if user is verified
            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResetPasswordResponse(null, "User account is not verified."));
            }

            // Check if OTP matches
            if (!passwordResetToken.getResetOtp().equals(request.getResetOTP())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResetPasswordResponse(null, "Invalid OTP."));
            }

            // Mark OTP as verified
            passwordResetToken.setOTPVerified(true);
            passwordResetTokenRepository.save(passwordResetToken);

            // Return response
            return ResponseEntity.ok(
                new ResetPasswordResponse(resetToken, "OTP verified successfully. New reset token issued."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResetPasswordResponse(null, "Failed to process reset password request."));
        }
    }
    @Transactional
    public ResponseEntity<ChangePasswordResponse> changePassword(String resetToken, ChangePasswordRequest request) {
        try {
            final Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByResetToken(resetToken);

            if (tokenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ChangePasswordResponse("Invalid or expired reset token."));
            }

            final PasswordResetToken passwordResetToken = tokenOpt.get();

            // Check if OTP was verified
            if (!passwordResetToken.isOTPVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ChangePasswordResponse("OTP has not been verified for this reset token."));
            }

            // Check if token has expired
            if (passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ChangePasswordResponse("Reset token has expired."));
            }

            // All good — deactivate token, update password
            passwordResetToken.setExpiredAt(LocalDateTime.now());

            final User user = passwordResetToken.getUser();
            final Optional<UserPassword> oldPassword = userPasswordRepository.findTopByUserOrderByCreatedAtDesc(user);

            oldPassword.get().setActive(false);
            userPasswordRepository.save(oldPassword.get());

            // Assuming you have PasswordEncoder bean injected
            final String encodedPassword = PasswordUtils.hashedPassword(request.getNewPassword());
            final UserPassword newPassword = UserPassword.builder()
                                        .password(encodedPassword)
                                        .isActive(true)
                                        .user(user)
                                        .build();
            userPasswordRepository.save(newPassword);

            return ResponseEntity.ok(new ChangePasswordResponse("Password updated successfully."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChangePasswordResponse("Failed to change password."));
        }
    }

}