package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.totp.models.TotpCreateResponse;
import com.auth_example.challenge_service.mfa.totp.models.TotpValidateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TotpResponseTest {
    @Test
    @DisplayName("[TotpCreateResponse] - should create a response with secret and qr_code_url")
    public void shouldCreateTotpCreateResponse() {
        final String SECRET = "test_secret";
        final String QR_CODE_URL = "test_qr_code_url";
        TotpCreateResponse response = new TotpCreateResponse(SECRET, QR_CODE_URL);

        assertEquals(SECRET, response.secret());
        assertEquals(QR_CODE_URL, response.qrCodeUrl());
    }

    @Test
    @DisplayName("[TotpValidateResponse] - should create a response with secret and qr_code_url")
    public void shouldCreateTotpValidateResponse() {
        final String EMAIL = "test@example.com";
        final MfaChallengeType CHALLENGE_TYPE = MfaChallengeType.TOTP;
        TotpValidateResponse response = new TotpValidateResponse(EMAIL, CHALLENGE_TYPE);

        assertEquals(EMAIL, response.email());
        assertEquals(CHALLENGE_TYPE, response.type());
    }
}
