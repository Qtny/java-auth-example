package com.auth_example.auth_service.auth;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.auth.models.RegisterResponse;
import com.auth_example.auth_service.encryption.EncryptionService;
import com.auth_example.auth_service.mfa.CreateMfaResponse;
import com.auth_example.auth_service.mfa.MfaService;
import com.auth_example.auth_service.users.UserService;
import com.auth_example.common_service.core.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final MfaService mfaService;
    private final EncryptionService encryptionService;

    @GetMapping
    public String hello() {
        log.info("hello world");
        return "world";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("INFO :: start registration");
        // check if user email already exist, and return if it exists
        userService.checkIfUserEmailExist(request.email());

        // create mfa challenge of type email
        UUID challengeId = mfaService.createMfaChallenge(request);

        // encrypt
        String encryptedUuid = encryptionService.encryptUuid(challengeId);

        // return challenge id
        RegisterResponse response = new RegisterResponse(encryptedUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
