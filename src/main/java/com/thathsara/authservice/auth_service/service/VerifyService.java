package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.VerifyOtpRequest;
import com.thathsara.authservice.auth_service.dto.VerifyOtpResponse;
import com.thathsara.authservice.auth_service.model.AuthToken;
import com.thathsara.authservice.auth_service.model.RefreshToken;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.VerificationToken;
import com.thathsara.authservice.auth_service.repository.AuthTokenRepository;
import com.thathsara.authservice.auth_service.repository.RefreshTokenRepository;
import com.thathsara.authservice.auth_service.repository.UserRepository;
import com.thathsara.authservice.auth_service.repository.VerificationTokenRepository;
import com.thathsara.authservice.auth_service.util.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class VerifyService {
    @Autowired private VerificationTokenRepository verificationTokenRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthTokenRepository authTokenRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<VerifyOtpResponse> verify(VerifyOtpRequest request) {
        try {
            if (request.getVerifyToken() == null || request.getOtp() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new VerifyOtpResponse(null, null, "Empty credentials provided."));
            }

            final VerificationToken token = verificationTokenRepository
                    .findValidTokenByVerifyToken(request.getVerifyToken())
                    .orElse(null);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new VerifyOtpResponse(null, null, "Invalid or expired verification token."));
            }

            final User user = token.getUser();

            if (user.isVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new VerifyOtpResponse(null, null, "User already verified."));
            }

            if (!token.getOtp().equals(request.getOtp())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new VerifyOtpResponse(null, null, "Invalid OTP provided."));
            }

            user.setVerified(true);
            user.setStatus(User.STATUS.ACTIVE);
            userRepository.save(user);

            final String authToken = jwtUtil.generateToken(user.getId());
            final String refreshToken = jwtUtil.generateToken(user.getId());

            final AuthToken authTokens = AuthToken.builder()
                    .user(user)
                    .authToken(authToken)
                    .expiredAt(LocalDateTime.now().plusHours(2))
                    .build();
            authTokenRepository.save(authTokens);

            final RefreshToken refreshTokens = RefreshToken.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .is_revoked(false)
                    .expiredAt(LocalDateTime.now().plusHours(3))
                    .build();
            refreshTokenRepository.save(refreshTokens);

            return ResponseEntity.ok(new VerifyOtpResponse(authToken, refreshToken, "User verified successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new VerifyOtpResponse(null, null, "Internal server error occurred."));
        }
    }
}
