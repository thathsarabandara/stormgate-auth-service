package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.OtpResendResponse;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.VerificationToken;
import com.thathsara.authservice.auth_service.repository.VerificationTokenRepository;
import com.thathsara.authservice.auth_service.util.JwtUtil;
import com.thathsara.authservice.auth_service.util.OTPUtil;

import jakarta.transaction.Transactional;

@Service
public class ResendOtpService {

    @Autowired private VerificationTokenRepository verificationTokenRepository;
    @Autowired private MailService emailService;
    @Autowired private JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<OtpResendResponse> resendOtp(String oldToken) {
        try {
            Optional<VerificationToken> existingTokenOpt = verificationTokenRepository
                    .findValidTokenByVerifyToken(oldToken);

            if (existingTokenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new OtpResendResponse(null,"Invalid token."));
            }

            VerificationToken existingToken = existingTokenOpt.get();
            User user = existingToken.getUser();

            if(user.isVerified() == true) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new OtpResendResponse(null,"Already verified token"));
            }

            existingToken.setExpiredAt(LocalDateTime.now());
            verificationTokenRepository.save(existingToken);

            // Generate new token and OTP
            String newToken = jwtUtil.generateToken(user.getId());
            String newOtp = OTPUtil.generateOTP();

            VerificationToken newVerificationToken = VerificationToken.builder()
                    .user(user)
                    .verifyToken(newToken)
                    .otp(newOtp)
                    .expiredAt(LocalDateTime.now().plusMinutes(10))
                    .build();

            verificationTokenRepository.save(newVerificationToken);

            // Send email
           emailService.sendOtpEmail(user.getEmail(),
           "StormGate-AuthService - OTP Service - Don't Reply" , newOtp, "password_reset");

            return ResponseEntity.ok(new OtpResendResponse(newToken , "OTP resent successfully."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OtpResendResponse(null, "Failed to resend OTP due to server error."));
        }
    }
}
