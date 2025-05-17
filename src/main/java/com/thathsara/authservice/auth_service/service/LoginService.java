package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.LoginRequest;
import com.thathsara.authservice.auth_service.dto.LoginResponse;
import com.thathsara.authservice.auth_service.model.AuthToken;
import com.thathsara.authservice.auth_service.model.RefreshToken;
import com.thathsara.authservice.auth_service.model.User;
import com.thathsara.authservice.auth_service.model.UserPassword;
import com.thathsara.authservice.auth_service.repository.AuthTokenRepository;
import com.thathsara.authservice.auth_service.repository.RefreshTokenRepository;
import com.thathsara.authservice.auth_service.repository.UserPasswordRepository;
import com.thathsara.authservice.auth_service.repository.UserRepository;
import com.thathsara.authservice.auth_service.util.JwtUtil;
import com.thathsara.authservice.auth_service.util.PasswordUtils;

import jakarta.transaction.Transactional;

@Service
public class LoginService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserPasswordRepository userPasswordRepository;
    @Autowired private AuthTokenRepository authTokenRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordUtils passwordUtils;

    @Transactional
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        try {
            // Check if user exists by email
            final Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, null, null, "Invalid credentials"));
            }

            final User user = userOpt.get();
            final Optional<UserPassword> password = userPasswordRepository.findTopByUserOrderByCreatedAtDesc(user);

            // Validate account status
            if (user.isVerified() == false || 
            !user.getStatus().equals(User.STATUS.ACTIVE) || password.get().isActive() == true ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, null, null, "Account not active or verified"));
            }
            final boolean isValid = passwordUtils.verifyPassword(request.getPassword(), password.get().getPassword());

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, null, null, "Invalid credentials"));
            }

            // Generate new AuthToken (2 hours expiry)
            final String authTokenStr = jwtUtil.generateToken(user.getId());
            final AuthToken authToken = AuthToken.builder()
                    .user(user)
                    .authToken(authTokenStr)
                    .expiredAt(LocalDateTime.now().plusHours(2))
                    .build();
            authTokenRepository.save(authToken);

            // Generate new RefreshToken (3 hours expiry)
            final String refreshTokenStr = jwtUtil.generateToken(user.getId());
            final RefreshToken refreshToken = RefreshToken.builder()
                    .user(user)
                    .refreshToken(refreshTokenStr)
                    .expiredAt(LocalDateTime.now().plusHours(3))
                    .isRevoked(false)
                    .build();
            refreshTokenRepository.save(refreshToken);

            return ResponseEntity.ok(new LoginResponse(
                    authTokenStr,
                    refreshTokenStr,
                    user.getId(),
                    "Login successful"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(null, null, null, "Login failed due to server error"));
        }
    }
}
