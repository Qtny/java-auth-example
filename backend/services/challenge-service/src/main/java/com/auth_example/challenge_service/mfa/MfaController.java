package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.email.*;
import com.auth_example.challenge_service.mfa.models.CreateMfaResponse;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import com.auth_example.common_service.core.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mfa")
@RequiredArgsConstructor
@Slf4j
public class MfaController {

    private final MfaService mfaService;
    private final EmailMfaService emailMfaService;

    @GetMapping
    public String hello() {
        log.info("hello world");
        return "world";
    }

    @PostMapping("/email")
    public ResponseEntity<ApiResponse<CreateMfaResponse>> create(@RequestBody @Valid EmailCreateRequest request) {
        // check for existing email challenge
        EmailMfaFindByEmailRequest findRequest = new EmailMfaFindByEmailRequest(request.email());
        Optional<EmailMfaChallenge> existingChallenge = emailMfaService.findOne(findRequest);

        // fetch if present, create if missing
        EmailMfaChallenge challenge = existingChallenge.orElseGet(() -> emailMfaService.create(request));

        CreateMfaResponse response = new CreateMfaResponse(challenge.id());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<EmailMfaChallenge>> findByEmail(@PathVariable("email") String email) {
        EmailMfaChallenge challenge = emailMfaService.findOneOrThrow(new EmailMfaFindByEmailRequest(email));
        return ResponseEntity.ok(ApiResponse.success(challenge));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<EmailValidateResponse>> verify(@RequestBody @Valid EmailValidateRequest request) {
        // check code for userid / email for request
        EmailMfaChallenge challenge = emailMfaService.validate(request);


        EmailValidateResponse response = new EmailValidateResponse(challenge.type(), challenge.email());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
