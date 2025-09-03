package com.auth_example.challenge_service.mfa.totp;


import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.totp.models.TotpCreateRequest;
import com.auth_example.challenge_service.mfa.totp.models.TotpMfaFindByEmailRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TotpMfaRequestTest {
    private final String EMAIL = "test@example.com";

    @Test
    @DisplayName("[TotpMfaFindByEmailRequest] - should return MfaChallengeType.EMAIL as request type")
    public void shouldReturnTypeTotpForTotpMfaFindByEmailRequest() {
        // arrange
        TotpMfaFindByEmailRequest mockRequest = new TotpMfaFindByEmailRequest(EMAIL);
        // act assert
        assertEquals(MfaChallengeType.TOTP, mockRequest.type());
    }

    @Test
    @DisplayName("[TotpCreateRequest] - should return MfaChallengeType.EMAIL as request type")
    public void shouldReturnTypeTotpForTotpCreateRequest() {
        // arrange
        TotpCreateRequest mockRequest = new TotpCreateRequest(EMAIL);
        // act assert
        assertEquals(MfaChallengeType.TOTP, mockRequest.type());
    }
}
