package com.auth_example.auth_service.auth;

import com.auth_example.auth_service.auth.models.*;
import com.auth_example.auth_service.encryption.EncryptionService;
import com.auth_example.auth_service.exceptions.MfaNotEnabledException;
import com.auth_example.auth_service.jwt.JwtService;
import com.auth_example.auth_service.mfa.MfaService;
import com.auth_example.auth_service.users.UserService;
import com.auth_example.auth_service.users.models.User;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.jwt.TokenPurpose;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

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

    @GetMapping("/headers")
    public ResponseEntity<Map<String, String>> getHeaders(@RequestHeader Map<String, String> headers) {
        return ResponseEntity.ok(headers);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest request) throws JOSEException {
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
    public ResponseEntity<ApiResponse<MfaNotEnabledResponse>> verify(HttpServletRequest httpRequest, @RequestBody @Valid VerifyRegistrationRequest request) throws JOSEException {
        // decrypt challenge id
        UUID challengeId = encryptionService.decryptUuid(request.challengeId());

        // check with challenge service for otp
        String email = httpRequest.getHeader("X-User-Email");
        String responseEmail = mfaService.verifyRegisterEmail(email, challengeId, request.code());

        // create user with user service
        User user = userService.createUser(responseEmail);

        // prompt user to enable mfa
        // this occurrence is special because:
        // 1. it is an error, therefore the error code "MFA_NOT_ENABLED" is needed
        // 2. registration token has to be resigned for mfa
        String token = jwtService.generateTransitionalToken(user.getEmail(), TokenPurpose.VERIFY_MFA);
        throw new MfaNotEnabledException("mfa not enabled", token);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) throws JOSEException {
        // call user to check pw
        User user = userService.validatePassword(request);
        // check is mfa enabled
        if (!user.getMfa().isEnabled()) {
            String token = jwtService.generateTransitionalToken(user.getEmail(), TokenPurpose.VERIFY_MFA);
            throw new MfaNotEnabledException("mfa has not been enabled", token);
        }
        // generate user token
        String token = jwtService.generateUserToken(user.getEmail(), TokenPurpose.AUTHORIZATION);
        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mfa/email/initiate")
    public ResponseEntity<ApiResponse<MfaInitiateResponse>> initiateEmailMfa(@RequestBody @Valid MfaEmailInitiateRequest request) {
        // create challenge
        UUID challengeId = mfaService.createEmailMfa(request.email());

        // encrypt challenge id
        String encryptedUuid = encryptionService.encryptUuid(challengeId);

        MfaInitiateResponse response = new MfaInitiateResponse(encryptedUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mfa/sms/initiate")
    public ResponseEntity<ApiResponse<MfaInitiateResponse>> initiateSmsMfa(HttpServletRequest httpRequest, @RequestBody @Valid MfaSmsInitiateRequest request) {
        String email = httpRequest.getHeader("X-User-Email");

        MfaInitiateResponse response = new MfaInitiateResponse("aa");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mfa/email/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyEmailMfa(HttpServletRequest httpRequest, @RequestBody @Valid VerifyEmailMfaRequest request) throws JOSEException {
        // decrypt challenge id
        UUID challengeId = encryptionService.decryptUuid(request.challengeId());

        // check with mfa service on code
        String email = httpRequest.getHeader("X-User-Email");
        String responseEmail = mfaService.verifyEmail(email, challengeId, request.code());

        // update user
        userService.enableMfa(email);

        // generate user token
        String token = jwtService.generateUserToken(responseEmail, TokenPurpose.AUTHORIZATION);

        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
