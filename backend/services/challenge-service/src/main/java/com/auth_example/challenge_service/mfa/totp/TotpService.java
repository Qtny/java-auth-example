package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.exceptions.InvalidSkewException;
import lombok.RequiredArgsConstructor;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.jboss.aerogear.security.otp.api.Clock;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TotpService {

    public String generateNewClientSecret(String email) {
        // generate random 16 characters base 32 secret
        return Base32.random();
    }

    public boolean verifyWithSkew(String base32Secret, String code, int skew) {
        if (skew <= 0) {
            throw new InvalidSkewException("invalid skew");
        }

        for (int i = -skew; i <= skew; i++) {
            Totp totp;
            if (i == 0) {
                totp = new Totp(base32Secret);
            } else {
                Clock clock = new Clock(i);
                totp = new Totp(base32Secret, clock);
            }
            if (totp.now().equals(code)) {
                return true;
            }
        }

        return false;
    }
}
