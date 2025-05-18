package com.thathsara.authservice.auth_service.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OTPUtil {
    private static final  SecureRandom random = new SecureRandom();
    public static String generateOTP() {
        final int otp = random.nextInt(1_000_000);
        return String.format("%6d", otp);
    }
}
