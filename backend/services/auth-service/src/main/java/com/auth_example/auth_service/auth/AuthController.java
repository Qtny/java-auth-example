package com.auth_example.auth_service.auth;

import com.auth_example.auth_service.auth.models.*;
import com.auth_example.auth_service.encryption.EncryptionService;
import com.auth_example.auth_service.jwt.JwtService;
import com.auth_example.auth_service.mfa.MfaService;
import com.auth_example.auth_service.mfa.models.EmailValidateResponse;
import com.auth_example.auth_service.users.UserService;
import com.auth_example.auth_service.users.models.User;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.jwt.TokenPurpose;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.auth_example.common_service.core.responses.ApiErrorCode.MFA_NOT_ENABLED;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final MfaService mfaService;
    private final JwtService jwtService;
    private final EncryptionService encryptionService;

    @GetMapping
    public String hello() {
        return "world";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("INFO :: start registration");
        // check if user email already exist, and return if it exists
        userService.checkIfUserEmailExist(request.email());

        // check if user exist in redis, if true, return challengeId, else call mfa service to create challenge
        UUID challengeId = mfaService.createRegistrationMfa(request);

        // encrypt challenge id
        String encryptedUuid = encryptionService.encryptUuid(challengeId);

        // create otp jwt
        String token = jwtService.generateTransitionalToken(request.email(), TokenPurpose.VERIFY_REGISTRATION);

        // return challenge id
        RegisterResponse response = new RegisterResponse(token, encryptedUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<ApiResponse<MfaNotEnabledResponse>> verify(@AuthenticationPrincipal String email, @RequestBody @Valid VerifyRegistrationRequest request) {
        // decrypt challenge id
        UUID challengeId = encryptionService.decryptUuid(request.challengeId());

        // check with challenge service for otp
        EmailValidateResponse validationResponse = mfaService.verify(email, challengeId, request.code());

        // create user with user service
        User user = userService.createUser(validationResponse);

        // prompt user to enable mfa
        // this occurrence is special because:
        // 1. it is an error, therefore the error code "MFA_NOT_ENABLED" is needed
        // 2. registration token has to be resigned for mfa
        String token = jwtService.generateTransitionalToken(user.getEmail(), TokenPurpose.VERIFY_MFA);
        MfaNotEnabledResponse response = new MfaNotEnabledResponse(MFA_NOT_ENABLED, "mfa not enabled", token);
        return ResponseEntity.ok(ApiResponse.failure(response));
    }
}
