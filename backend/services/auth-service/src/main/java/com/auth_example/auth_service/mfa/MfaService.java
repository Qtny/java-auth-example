package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.exceptions.ApiNotSuccessException;
import com.auth_example.auth_service.mfa.models.CreateMfaResponse;
import com.auth_example.auth_service.mfa.models.email.EmailValidateResponse;
import com.auth_example.auth_service.mfa.models.totp.CreateTotpMfaResponse;
import com.auth_example.auth_service.mfa.models.totp.VerifyTotpMfaResponse;
import com.auth_example.auth_service.redis.RedisService;
import com.auth_example.auth_service.users.UserDtoMapperImpl;
import com.auth_example.auth_service.users.models.NewUser;
import jakarta.validation.constraints.NotBlank;
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
    private final PasswordEncoder passwordEncoder;
    ;

    public UUID createRegistrationMfa(RegisterRequest request) {
        Optional<NewUser> redisUser = redisService.findNewUserByEmail(request.email());
        if (redisUser.isPresent()) {
            // fetch challenge id
            return mfaClient.findOneRegisterMfaByEmail(request.email());
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

    public EmailValidateResponse verifyRegisterEmail(String email, UUID challengeId, String code) {
        return mfaClient.verifyRegisterMfa(email, challengeId, code);
    }

    public EmailValidateResponse verifyEmail(String email, UUID challengeId, String code) {
        return mfaClient.verifyEmailMfa(email, challengeId, code);
    }

    public UUID verifyLogin(MfaChallengeType method, String target) {
        // split the types
        CreateMfaResponse response = switch (method) {
            case EMAIL -> mfaClient.createEmailMfa(target);
            default -> throw new ApiNotSuccessException("LMAO");
        };

        return response.challengeId();
    }

    public CreateTotpMfaResponse createTotpMfa(String email) {
        return mfaClient.setupTotpMfa(email);
    }

    public VerifyTotpMfaResponse verifyTotp(String email, String code) {
        // verify code
        return mfaClient.verifyTotpMfa(email, code);
    }
}
