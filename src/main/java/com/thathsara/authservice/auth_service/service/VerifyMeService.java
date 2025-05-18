package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.AuthResponse;
import com.thathsara.authservice.auth_service.model.AuthToken;
import com.thathsara.authservice.auth_service.model.RefreshToken;
import com.thathsara.authservice.auth_service.repository.AuthTokenRepository;
import com.thathsara.authservice.auth_service.repository.RefreshTokenRepository;
import com.thathsara.authservice.auth_service.util.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class VerifyMeService {

    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private AuthTokenRepository authTokenRepository;
    @Autowired private JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<AuthResponse> VerifyMe(String authToken, String refreshToken) {
        try {
            if (authToken == null && refreshToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponse(null, null, null, "No token provided"));
            }

            // Case: if AuthToken provided — validate it
            if (authToken != null) {
                final Optional<AuthToken> authTokenOpt = authTokenRepository.findValidTokenByAuthToken(authToken);
                if (authTokenOpt.isPresent()) {
                    final AuthToken validToken = authTokenOpt.get();
                    return ResponseEntity.ok(new AuthResponse(
                            validToken.getAuthToken(),
                            refreshToken,
                            validToken.getUser().getId(),
                            "Authorized"
                    ));
                }
            }

            // Case: if AuthToken invalid or missing, and RefreshToken provided — validate it
            if (refreshToken != null) {
                final Optional<RefreshToken> reTokenOpt = refreshTokenRepository
                .findByRefreshTokenAndIsRevoked(refreshToken, false);
                if (reTokenOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new AuthResponse(null, null, null, "RefreshToken has expired!"));
                }

                // Revoke old refresh token
                final RefreshToken reToken = reTokenOpt.get();
                reToken.setRevoked(true);
                refreshTokenRepository.save(reToken);

                // Generate new AuthToken
                final String newAuthToken = jwtUtil.generateToken(reToken.getUser().getId());
                final AuthToken newAuth = AuthToken.builder()
                        .user(reToken.getUser())
                        .authToken(newAuthToken)
                        .expiredAt(LocalDateTime.now().plusHours(2))
                        .build();

                authTokenRepository.save(newAuth);

                return ResponseEntity.ok(new AuthResponse(
                        newAuthToken,
                        null,
                        reToken.getUser().getId(),
                        "New AuthToken Issued"
                ));
            }

            // Fallback — no valid authToken and no refreshToken
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, null, "Unauthorized access"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, null, "Verification failed due to server error"));
        }
    }
}
