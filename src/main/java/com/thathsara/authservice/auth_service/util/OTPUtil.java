package com.thathsara.authservice.auth_service.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OTPUtil {
    private static final  SecureRandom random = new SecureRandom();
    private static final int  OTP_LENGTH = 6;

    public static String generateOTP() {
        int otp = random.nextInt(1_000_000);
        return String.format("%6d", otp);
    }
}
