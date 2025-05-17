package com.thathsara.authservice.auth_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thathsara.authservice.auth_service.dto.RegisterRequest;
import com.thathsara.authservice.auth_service.dto.RegisterResponse;
import com.thathsara.authservice.auth_service.exception.CustomException;
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

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already in use");
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
                                                    .verifyToken(token)
                                                    .otp(otp)
                                                    .expiredAt(LocalDateTime.now().plusMinutes(10))
                                                    .build();

        verificationTokenRepository.save(verificationToken);

        mailService.sendOtpEmail(request.getEmail(), "StormGate OTP Service - Don't Reply", otp);

        return new RegisterResponse(token,"registered successfully");
    }
}
