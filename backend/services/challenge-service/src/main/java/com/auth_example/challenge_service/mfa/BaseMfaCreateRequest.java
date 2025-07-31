package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.email.models.EmailCreateRequest;
import com.auth_example.challenge_service.mfa.sms.SmsCreateRequest;
import com.auth_example.challenge_service.mfa.totp.models.TotpCreateRequest;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        property = "type",
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmailCreateRequest.class, name = "EMAIL"),
        @JsonSubTypes.Type(value = SmsCreateRequest.class, name = "SMS"),
        @JsonSubTypes.Type(value = TotpCreateRequest.class, name = "TOTP")
})
public interface BaseMfaCreateRequest {
    MfaChallengeType type();
}
