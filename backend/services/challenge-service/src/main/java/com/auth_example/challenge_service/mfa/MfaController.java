package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.mfa.models.CreateMfaResponse;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import com.auth_example.challenge_service.redis.RedisService;
import com.auth_example.challenge_service.user.UserService;
import com.auth_example.common_service.core.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mfa")
@RequiredArgsConstructor
@Slf4j
public class MfaController {

    private final MfaService mfaService;
    private final UserService userService;

    @GetMapping
    public String hello() {
        log.info("hello world");
        return "world";
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateMfaResponse>> create(@RequestBody @Valid CreateChallengeRequest request) {
        // check redis record, and reject request if email already exist
        userService.checkIfTempUserExist(request.email());

        // create challenge
        MfaChallenge challenge = mfaService.create(request);

        // store temp user in redis
        userService.temporaryStoreUser(request, challenge.getUserId());

        return ResponseEntity.ok(ApiResponse.success(new CreateMfaResponse(challenge.getId())));
    }
}
