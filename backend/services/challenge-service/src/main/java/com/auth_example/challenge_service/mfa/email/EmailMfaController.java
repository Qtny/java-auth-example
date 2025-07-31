package com.auth_example.challenge_service.mfa.email;

import com.auth_example.challenge_service.mfa.email.models.*;
import com.auth_example.challenge_service.mfa.models.CreateMfaResponse;
import com.auth_example.challenge_service.mfa.email.register.RegisterEmailMfaService;
import com.auth_example.common_service.core.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mfa/email")
public class EmailMfaController {

    private final EmailMfaService emailMfaService;
    private final EmailService emailService;
    private final RegisterEmailMfaService registerEmailMfaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateMfaResponse>> create(@RequestBody @Valid EmailCreateRequest request) {
        // check for existing email challenge
        EmailMfaFindByEmailRequest findRequest = new EmailMfaFindByEmailRequest(request.email());
        Optional<EmailMfaChallenge> existingChallenge = emailMfaService.findOne(findRequest);

        // fetch if present, create if missing
        EmailMfaChallenge challenge = existingChallenge.orElseGet(() -> emailMfaService.create(request));

        // create mfa challenge
        CreateMfaResponse response = new CreateMfaResponse(challenge.id());

        // send email
        emailService.sendOtpEmail(challenge.email(), challenge.code());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<EmailMfaChallenge>> findByEmail(@PathVariable("email") String email) {
        EmailMfaChallenge challenge = emailMfaService.findOneOrThrow(new EmailMfaFindByEmailRequest(email));
        return ResponseEntity.ok(ApiResponse.success(challenge));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<EmailValidateResponse>> verify(@RequestBody @Valid EmailValidateRequest request) {
        // check code for userid / email for request
        EmailMfaChallenge challenge = emailMfaService.validate(request);

        EmailValidateResponse response = new EmailValidateResponse(challenge.type(), challenge.email());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
