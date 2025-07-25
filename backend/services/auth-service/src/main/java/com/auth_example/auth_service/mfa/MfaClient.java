package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.exceptions.ApiNotSuccessException;
import com.auth_example.auth_service.mfa.models.*;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.core.rest.BaseRestClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    public CreateMfaResponse createMfa(RegisterRequest request) {
        CreateMfaPayload payload = mapper.registerRequestToCreateMfaPayload(request, MfaChallengeType.EMAIL);
        String uri = MFA_BASE_URL + "/email";
        ApiResponse<CreateMfaResponse> response = restClient.post(uri, payload, CreateMfaResponse.class);
        if (!response.isSuccess()) {
            log.info("INFO :: user with email [{}] have registered before, resuming registration process", request.email());
        }

        return response.getData();
    }

    public EmailValidateResponse verify(String email, UUID challengeId, String code) {
        log.info("sending verification to [challenge service]");
        VerifyMfaPayload payload = new VerifyMfaPayload(email, challengeId, code);
        String uri = MFA_BASE_URL + "/verify/email";
        ApiResponse<EmailValidateResponse> response = restClient.post(uri, payload, EmailValidateResponse.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }

    public UUID findOneByEmail(String email) {
        log.info("finding existing challenge");
        String uri = MFA_BASE_URL + "/email/" + email;
        ApiResponse<MfaChallenge> response = restClient.get(uri, MfaChallenge.class);
        return response.getData().getId();
    }
}
