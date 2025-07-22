package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MfaService {

    private final MfaClient mfaClient;

    public UUID createMfaChallenge(@Valid RegisterRequest request) {
        CreateMfaResponse response = mfaClient.createMfa(request);
        return response.id();
    }
}
