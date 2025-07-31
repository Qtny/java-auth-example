package com.auth_example.challenge_service.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    public String generateOtp(int length) {
        log.info("INFO :: generating OTP code");
        if (length == 0) {
            throw new IllegalArgumentException("otp length must be more than 0");
        }

        String digits = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(digits.length());
            otp.append(digits.charAt(index));
        }

        return otp.toString();
    }
}
