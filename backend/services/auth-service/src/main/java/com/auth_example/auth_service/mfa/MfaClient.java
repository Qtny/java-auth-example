package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.exceptions.ApiNotSuccessException;
import com.auth_example.auth_service.mfa.models.*;
import com.auth_example.auth_service.mfa.models.email.EmailValidateResponse;
import com.auth_example.auth_service.mfa.models.email.VerifyEmailMfaPayload;
import com.auth_example.auth_service.mfa.models.email.CreateEmailMfaPayload;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.core.rest.BaseRestClient;
import com.auth_example.common_service.core.rest.RequestOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaClient {

    private static final String MFA_BASE_URL = "http://localhost:8082/api/v1/mfa";
    private final BaseRestClient restClient;
    private final MfaDtoMapperImpl mapper;

    public CreateMfaResponse createRegistrationMfa(RegisterRequest request) {
        log.info("creating mfa challenge for registration");
        CreateEmailMfaPayload payload = new CreateEmailMfaPayload(request.email(), MfaChallengeType.EMAIL);
        String uri = MFA_BASE_URL + "/register/email";
        ApiResponse<CreateMfaResponse> response = restClient.post(uri, payload, RequestOption.internalNone(), CreateMfaResponse.class);
        if (!response.isSuccess()) {
            log.info("INFO :: user with email [{}] have registered before, resuming registration process", request.email());
        }

        return response.getData();
    }

    public EmailValidateResponse verifyRegisterMfa(String email, UUID challengeId, String code) {
        log.info("sending registration email verification to [challenge service]");
        VerifyEmailMfaPayload payload = new VerifyEmailMfaPayload(email, challengeId, code);
        String uri = MFA_BASE_URL + "/register/email/verify";
        ApiResponse<EmailValidateResponse> response = restClient.post(uri, payload, RequestOption.internalNone(), EmailValidateResponse.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }

    public CreateMfaResponse createEmailMfa(String email) {
        log.info("creating mfa challenge for email");
        CreateEmailMfaPayload payload = new CreateEmailMfaPayload(email, MfaChallengeType.EMAIL);
        String uri = MFA_BASE_URL + "/email";
        ApiResponse<CreateMfaResponse> response = restClient.post(uri, payload, RequestOption.internalNone(), CreateMfaResponse.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }


    public EmailValidateResponse verifyEmailMfa(String email, UUID challengeId, String code) {
        log.info("sending email verification to [challenge service]");
        VerifyEmailMfaPayload payload = new VerifyEmailMfaPayload(email, challengeId, code);
        String uri = MFA_BASE_URL + "/email/verify";
        ApiResponse<EmailValidateResponse> response = restClient.post(uri, payload, RequestOption.internalNone(), EmailValidateResponse.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }

    public UUID findOneRegisterMfaByEmail(String email) {
        log.info("finding existing challenge");
        String uri = MFA_BASE_URL + "/register/email/" + email;
        ApiResponse<MfaChallenge> response = restClient.get(uri, RequestOption.internalNone(), MfaChallenge.class);
        return response.getData().getId();
    }
}
