package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.LogoutResponse;
import com.thathsara.authservice.auth_service.model.AuthToken;
import com.thathsara.authservice.auth_service.model.RefreshToken;
import com.thathsara.authservice.auth_service.repository.AuthTokenRepository;
import com.thathsara.authservice.auth_service.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class LogoutService {

    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private AuthTokenRepository authTokenRepository;

    @Transactional
    public ResponseEntity<LogoutResponse> logout(String authToken, String refreshToken) {
        try {
            boolean tokenUpdated = false;

            // Expire AuthToken if exists
            if (authToken != null) {
                final Optional<AuthToken> authTokenOpt = authTokenRepository.findValidTokenByAuthToken(authToken);
                if (authTokenOpt.isPresent()) {
                    final AuthToken token = authTokenOpt.get();
                    token.setExpiredAt(LocalDateTime.now());
                    authTokenRepository.save(token);
                    tokenUpdated = true;
                }
            }

            // Revoke RefreshToken if exists
            if (refreshToken != null) {
                final Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository
                .findByRefreshTokenAndIsRevoked(refreshToken, false);
                if (refreshTokenOpt.isPresent()) {
                    final RefreshToken token = refreshTokenOpt.get();
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    tokenUpdated = true;
                }
            }

            if (tokenUpdated) {
                return ResponseEntity.ok(new LogoutResponse("Successfully logged out"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new LogoutResponse("No valid token found to logout"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponse("Logout failed due to server error"));
        }
    }
}
