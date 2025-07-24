package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.mfa.models.CreateMfaResponse;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.auth_service.users.models.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

    private final MfaClient mfaClient;

    public CreateMfaResponse createMfaChallenge(@Valid RegisterRequest request) {
        return mfaClient.createMfa(request);
    }

    public NewUser verify(UUID userId, UUID challengeId, String code) {
        return mfaClient.verify(userId, challengeId, code);
    }
}
