package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.exceptions.ApiNotSuccessException;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.core.rest.BaseRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }
}
