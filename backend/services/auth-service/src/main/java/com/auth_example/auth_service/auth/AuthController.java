package com.auth_example.auth_service.auth;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.auth.models.RegisterResponse;
import com.auth_example.auth_service.auth.models.VerifyRegistrationRequest;
import com.auth_example.auth_service.auth.models.VerifyRegistrationResponse;
import com.auth_example.auth_service.encryption.EncryptionService;
import com.auth_example.auth_service.jwt.JwtService;
import com.auth_example.auth_service.mfa.MfaService;
import com.auth_example.auth_service.mfa.models.CreateMfaResponse;
import com.auth_example.auth_service.users.UserService;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.auth_service.users.models.User;
import com.auth_example.common_service.core.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final JwtService jwtService;
    private final EncryptionService encryptionService;

    @GetMapping
    public String hello() {
        log.info(jwtService.generateUserToken("ali@gmail.com"));
        return "world";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("INFO :: start registration");
        // check if user email already exist, and return if it exists
        userService.checkIfUserEmailExist(request.email());

        // create mfa challenge of type email
        CreateMfaResponse challengeResponse = mfaService.createMfaChallenge(request);

        // encrypt challenge id
        String encryptedUuid = encryptionService.encryptUuid(challengeResponse.challengeId());

        // create otp jwt
        String token = jwtService.generateOtpToken(challengeResponse.userId().toString());

        // return challenge id
        RegisterResponse response = new RegisterResponse(token, encryptedUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verify/registration")
    public ResponseEntity<ApiResponse<VerifyRegistrationResponse>> verify(@AuthenticationPrincipal UUID userId, @RequestBody @Valid VerifyRegistrationRequest request) {
        // decrypt challenge id
        UUID challengeId = encryptionService.decryptUuid(request.challengeId());

        // check with challenge service for otp
        NewUser newUser = mfaService.verify(userId, challengeId, request.code());

        // create user with user service
        User user = userService.createUser(newUser);

        // create user jwt
        String token = jwtService.generateUserToken(user.getId().toString());

        return ResponseEntity.ok(ApiResponse.success(new VerifyRegistrationResponse(token)));
    }
}
