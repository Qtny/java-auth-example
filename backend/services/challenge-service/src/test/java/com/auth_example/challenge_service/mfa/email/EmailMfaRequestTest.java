package com.auth_example.challenge_service.mfa.email;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.email.models.EmailCreateRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaFindByEmailRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EmailMfaRequestTest {

    private final String EMAIL = "test@example.com";

    @Test
    @DisplayName("[EmailMfaFindBtEmailRequest] - should return MfaChallengeType.EMAIL as request type")
    public void shouldReturnTypeEmailForEmailMfaFindByEmailRequest() {
        // arrange
        EmailMfaFindByEmailRequest mockRequest = new EmailMfaFindByEmailRequest(EMAIL);
        // act assert
        assertEquals(MfaChallengeType.EMAIL, mockRequest.type());
    }

    @Test
    @DisplayName("[EmailCreateRequest] - should return MfaChallengeType.EMAIL as request type")
    public void shouldReturnTypeEmailForEmailCreateRequest() {
        // arrange
        EmailCreateRequest mockRequest = new EmailCreateRequest(EMAIL);
        // act assert
        assertEquals(MfaChallengeType.EMAIL, mockRequest.type());
    }
}
