package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.encryption.EncryptionService;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.email.EmailService;
import com.auth_example.challenge_service.mfa.totp.models.*;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/mfa/totp")
@RequiredArgsConstructor
@Slf4j
public class TotpMfaController {

    private final TotpMfaService totpMfaService;
    private final EncryptionService encryptionService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<ApiResponse<TotpCreateResponse>> create(@RequestBody @Valid TotpCreateRequest request) {
        // create or retrieve client totp profile
        TotpProfile profile = totpMfaService.create(request);
        // decrypt client secret
        String decryptedSecret = encryptionService.decryptString(profile.getSecret());

        // send qr code to client in case if qr generation fails
        emailService.sendTotpQrCode(profile.getEmail(), profile.getQrCodeUrl());

        TotpCreateResponse response = new TotpCreateResponse(decryptedSecret, profile.getQrCodeUrl());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<TotpValidateResponse>> verify(@RequestBody @Valid TotpValidateRequest request) {
        // validate with server
        TotpProfile profile = totpMfaService.validate(request);

        TotpValidateResponse response = new TotpValidateResponse(profile.getEmail(), MfaChallengeType.TOTP);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
