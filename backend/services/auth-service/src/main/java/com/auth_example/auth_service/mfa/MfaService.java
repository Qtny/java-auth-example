package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.mfa.models.CreateMfaResponse;
import com.auth_example.auth_service.mfa.models.EmailValidateResponse;
import com.auth_example.auth_service.redis.RedisService;
import com.auth_example.auth_service.users.UserDtoMapperImpl;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.auth_service.users.models.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

    private final MfaClient mfaClient;
    private final RedisService redisService;
    private final UserDtoMapperImpl userMapper;

    public CreateMfaResponse createMfaChallenge(@Valid RegisterRequest request) {
        return mfaClient.createMfa(request);
    }

    public UUID createRegistrationMfa(RegisterRequest request) {
        Optional<NewUser> redisUser = redisService.findNewUserByEmail(request.email());
        if (redisUser.isPresent()) {
            // fetch challenge id
            return mfaClient.findOneByEmail(request.email());
        } else {
            // store user in redis temporarily
            NewUser newUser = userMapper.registerRequestToNewUser(request);
            redisService.storeUser(newUser, Duration.ofMinutes(5));

            // create mfa challenge of type email
            CreateMfaResponse challengeResponse = mfaClient.createMfa(request);
            return challengeResponse.challengeId();
        }
    }

    public EmailValidateResponse verify(String email, UUID challengeId, String code) {
        return mfaClient.verify(email, challengeId, code);
    }
}
