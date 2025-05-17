package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class VerifyService { 
    @Autowired private VerificationTokenRepository verificationTokenRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthTokenRepository authTokenRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private JwtUtil jwtUtil;

    public VerifyOtpResponse verify(VerifyOtpRequest request) {
        final VerificationToken token = verificationTokenRepository
                .findValidTokenByVerifyToken(request.getVerifyToken())
                .orElseThrow(() -> new RuntimeException("Token is invalid or expired"));

        if (!token.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        final User user = token.getUser();
        user.setVerified(true);
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


        return new VerifyOtpResponse(authToken , refreshToken , "verifiedSuccessfully");
    }
}
