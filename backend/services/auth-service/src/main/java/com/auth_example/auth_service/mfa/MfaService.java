package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.mfa.models.CreateMfaResponse;
import com.auth_example.auth_service.mfa.models.email.EmailValidateResponse;
import com.auth_example.auth_service.redis.RedisService;
import com.auth_example.auth_service.users.UserDtoMapperImpl;
import com.auth_example.auth_service.users.models.NewUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder  = new BCryptPasswordEncoder(12);;

    public UUID createRegistrationMfa(RegisterRequest request) {
        Optional<NewUser> redisUser = redisService.findNewUserByEmail(request.email());
        if (redisUser.isPresent()) {
            // fetch challenge id
            return mfaClient.findOneByEmail(request.email());
        } else {
            // store user in redis temporarily
            NewUser newUser = userMapper.registerRequestToNewUser(request);
            String hashedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(hashedPassword);
            redisService.storeUser(newUser, Duration.ofMinutes(5));

            // create mfa challenge of type email
            CreateMfaResponse challengeResponse = mfaClient.createRegistrationMfa(request);
            return challengeResponse.challengeId();
        }
    }

    public UUID createEmailMfa(String email) {
        // create challenge
        CreateMfaResponse response = mfaClient.createEmailMfa(email);
        return response.challengeId();
    }

    public String verifyEmail(String email, UUID challengeId, String code) {
        EmailValidateResponse response = mfaClient.verifyEmailMfa(email, challengeId, code);
        return response.email();
    }
}
