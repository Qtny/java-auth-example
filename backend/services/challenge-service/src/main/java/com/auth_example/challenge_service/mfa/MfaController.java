package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.mfa.models.CreateMfaResponse;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import com.auth_example.challenge_service.mfa.models.VerifyRequest;
import com.auth_example.challenge_service.redis.RedisService;
import com.auth_example.challenge_service.user.UserService;
import com.auth_example.challenge_service.user.models.UserEntry;
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
    private final UserService userService;

    @GetMapping
    public String hello() {
        log.info("hello world");
        return "world";
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateMfaResponse>> create(@RequestBody @Valid CreateChallengeRequest request) {
        // check redis record, and reject request if email already exist
        Optional<UserEntry> userEntry = userService.checkIfTempUserExist(request.email());
        if (userEntry.isPresent()) {
            UUID userId = userEntry.get().getUserId();
            MfaChallenge challenge = mfaService.findOneByUserId(userId);
            return ResponseEntity.ok(ApiResponse.failure(new CreateMfaResponse(challenge.getId(), userId)));
        }

        // create challenge
        MfaChallenge challenge = mfaService.create(request);

        // store temp user in redis
        UserEntry newUserEntry = userService.temporaryStoreUser(request, challenge.getUserId());

        return ResponseEntity.ok(ApiResponse.success(new CreateMfaResponse(challenge.getId(), newUserEntry.getUserId())));
    }

    @PostMapping("/verify/registration")
    public ResponseEntity<ApiResponse<UserEntry>> verify(@RequestBody @Valid VerifyRequest request) {
        // check code for userid / email for request
        boolean isValid = mfaService.compare(request.challengeId(), request.code());

        // get user from redis
        UserEntry userEntry = userService.retrieveUserEntry(request.userId());

        // pass user object back to auth
        return ResponseEntity.ok(ApiResponse.success(userEntry));
    }
}
