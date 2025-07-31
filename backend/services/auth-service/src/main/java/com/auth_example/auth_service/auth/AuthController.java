package com.auth_example.auth_service.auth;

import com.auth_example.auth_service.auth.models.*;
import com.auth_example.auth_service.encryption.EncryptionService;
import com.auth_example.auth_service.exceptions.MfaNotEnabledException;
import com.auth_example.auth_service.jwt.JwtService;
import com.auth_example.auth_service.mfa.MfaChallengeType;
import com.auth_example.auth_service.mfa.MfaService;
import com.auth_example.auth_service.mfa.models.email.EmailValidateResponse;
import com.auth_example.auth_service.mfa.models.totp.CreateTotpMfaResponse;
import com.auth_example.auth_service.mfa.models.totp.VerifyTotpMfaResponse;
import com.auth_example.auth_service.users.UserService;
import com.auth_example.auth_service.users.models.Mfa;
import com.auth_example.auth_service.users.models.User;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.jwt.TokenPurpose;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<MfaNotEnabledResponse>> verify(HttpServletRequest httpRequest, @RequestBody @Valid VerifyRegistrationRequest request) {
        // decrypt challenge id
        UUID challengeId = encryptionService.decryptUuid(request.challengeId());

        // check with challenge service for otp
        String email = httpRequest.getHeader("X-User-Email");
        EmailValidateResponse mfaResponse = mfaService.verifyRegisterEmail(email, challengeId, request.code());

        // create user with user service
        User user = userService.createUser(mfaResponse.email(), mfaResponse.type());

        // prompt user to enable mfa
        // this occurrence is special because:
        // 1. it is an error, therefore the error code "MFA_NOT_ENABLED" is needed
        // 2. registration token has to be resigned for mfa
        String token = jwtService.generateTransitionalToken(user.getEmail(), TokenPurpose.VERIFY_MFA);
        throw new MfaNotEnabledException("mfa not enabled", token);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginMfaResponse>> login(@RequestBody @Valid LoginRequest request) {
        // call user to check pw
        User user = userService.validatePassword(request);
        // check is mfa enabled
        Mfa userMfa = user.getMfa();
        if (!userMfa.isEnabled()) {
            String token = jwtService.generateTransitionalToken(user.getEmail(), TokenPurpose.VERIFY_MFA);
            throw new MfaNotEnabledException("mfa has not been enabled", token);
        }

        // initiate mfa method
        String encryptedChallengeId = "";
        UUID challengeId = mfaService.verifyLogin(userMfa.getMethod(), userMfa.getTarget());
        if (challengeId != null) {
            encryptedChallengeId = encryptionService.encryptUuid(challengeId);
        }

        // generate user token
        String token = jwtService.generateTransitionalToken(user.getEmail(), TokenPurpose.VERIFY_MFA);
        LoginMfaResponse response = new LoginMfaResponse(token, userMfa.getMethod(), encryptedChallengeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login/email/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyLoginByEmail(HttpServletRequest httpRequest, @RequestBody @Valid VerifyEmailMfaRequest request) {
        // decrypt challenge id
        UUID challengeId = encryptionService.decryptUuid(request.challengeId());

        // check with mfa service on code
        String email = httpRequest.getHeader("X-User-Email");
        EmailValidateResponse mfaResponse = mfaService.verifyEmail(email, challengeId, request.code());

        // update user
        userService.enableMfa(email, mfaResponse.type(), mfaResponse.email());

        // generate user token
        String token = jwtService.generateUserToken(mfaResponse.email(), TokenPurpose.AUTHORIZATION);

        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mfa/email/initiate")
    public ResponseEntity<ApiResponse<MfaInitiateResponse>> initiateEmailMfa(HttpServletRequest httpRequest) {
        // create challenge
        String email = httpRequest.getHeader("X-User-Email");
        UUID challengeId = mfaService.createEmailMfa(email);

        // encrypt challenge id
        String encryptedUuid = encryptionService.encryptUuid(challengeId);

        MfaInitiateResponse response = new MfaInitiateResponse(encryptedUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mfa/sms/initiate")
    public ResponseEntity<ApiResponse<MfaInitiateResponse>> initiateSmsMfa(HttpServletRequest httpRequest) {
        String email = httpRequest.getHeader("X-User-Email");

        MfaInitiateResponse response = new MfaInitiateResponse("aa");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mfa/totp/initiate")
    public ResponseEntity<ApiResponse<TotpMfaInitiateResponse>> initiateTotpMfa(HttpServletRequest httpRequest) {
        // get shared secret and qrcode
        String email = httpRequest.getHeader("X-User-Email");
        CreateTotpMfaResponse mfaResponse = mfaService.createTotpMfa(email);

        TotpMfaInitiateResponse response = new TotpMfaInitiateResponse(mfaResponse.secret(), mfaResponse.qrCodeUrl());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mfa/login/totp/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyLoginByTotp(HttpServletRequest httpRequest, @RequestBody @Valid VerifyTotpMfaRequest request) {
        // get shared secret and qrcode
        String email = httpRequest.getHeader("X-User-Email");
        VerifyTotpMfaResponse mfaResponse = mfaService.verifyTotp(email, request.code());

        // update user mfa
        userService.enableMfa(email, mfaResponse.type(), "");

        // generate user token
        String token = jwtService.generateUserToken(mfaResponse.email(), TokenPurpose.AUTHORIZATION);

        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
