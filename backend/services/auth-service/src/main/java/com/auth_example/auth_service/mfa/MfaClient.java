package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.mfa.models.CreateMfaPayload;
import com.auth_example.auth_service.mfa.models.CreateMfaResponse;
import com.auth_example.auth_service.mfa.models.VerifyMfaPayload;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.core.rest.BaseRestClient;
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
        ApiResponse<CreateMfaResponse> response = restClient.post(MFA_BASE_URL, payload, CreateMfaResponse.class);
        if (!response.isSuccess()) {
            log.info("INFO :: user with email [{}] have registered before, resuming registration process", request.email());
        }

        return response.getData();
    }

    public NewUser verify(UUID userId, UUID challengeId, String code) {
        log.info("sending verification to [challenge service]");
        VerifyMfaPayload payload = new VerifyMfaPayload(userId, challengeId, code);
        String uri = MFA_BASE_URL + "/verify/registration";
        ApiResponse<NewUser> response = restClient.post(uri, payload, NewUser.class);
        if (!response.isSuccess()) {

        }

        return response.getData();
    }
}
