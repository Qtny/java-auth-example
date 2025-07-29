package com.auth_example.challenge_service.mfa.email.register;

import com.auth_example.challenge_service.exceptions.ChallengeNotFoundException;
import com.auth_example.challenge_service.exceptions.CodeDoesNotMatchException;
import com.auth_example.challenge_service.exceptions.EmailMismatchException;
import com.auth_example.challenge_service.mfa.BaseMfaService;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.MfaDtoMapperImpl;
import com.auth_example.challenge_service.mfa.email.models.EmailCreateRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaFindByEmailRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailValidateRequest;
import com.auth_example.challenge_service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterEmailMfaService implements BaseMfaService<
        EmailCreateRequest,
        EmailValidateRequest,
        EmailMfaFindByEmailRequest
        > {

    private final OtpService otpService;
    private final MfaDtoMapperImpl mapper;
    private final RegisterEmailMfaRedisService redisService;

    @Override
    public MfaChallengeType getType() {
        return MfaChallengeType.EMAIL;
    }

    @Override
    public EmailMfaChallenge create(EmailCreateRequest request) {
        // generate code
        String code = otpService.generateOtp(6);

        log.info("INFO :: creating challenge");
        EmailMfaChallenge challenge = mapper.createChallengeRequestToMfaChallenge(request, code);
        redisService.storeMfaEntry(challenge, Duration.ofMinutes(5));

        return challenge;
    }

    @Override
    public EmailMfaChallenge validate(EmailValidateRequest request) {
        // fetch challenge from redis using challenge id
        Optional<EmailMfaChallenge> redisAttempt = redisService.findMfaById(request.challengeId());
        if (redisAttempt.isEmpty()) {
            throw new ChallengeNotFoundException("no challenge is bound to email address " + request.email());
        }
        // compare email
        EmailMfaChallenge challenge = redisAttempt.get();
        if (!challenge.email().equals(request.email())) {
            throw new EmailMismatchException("this challenge does not belong to this email");
        }
        // validate code
        if (!challenge.code().equals(request.code())) {
            throw new CodeDoesNotMatchException("otp code is incorrect");
        }
        // return challenge
        return challenge;
    }

    @Override
    public EmailMfaChallenge findOneById(UUID challengeId) {
        Optional<EmailMfaChallenge> redisAttempt = redisService.findMfaById(challengeId);
        if (redisAttempt.isPresent()) {
            return redisAttempt.get();
        }

        throw new ChallengeNotFoundException("challenge for challenge id " + challengeId + " does not exist");
    }

    @Override
    public EmailMfaChallenge findOneOrThrow(EmailMfaFindByEmailRequest request) {
        Optional<EmailMfaChallenge> redisAttempt = redisService.findMfaByIdentity(request.email());
        if (redisAttempt.isPresent()) {
            return redisAttempt.get();
        }

        throw new ChallengeNotFoundException("challenge for email " + request.email() + " does not exist");
    }

    @Override
    public Optional<EmailMfaChallenge> findOne(EmailMfaFindByEmailRequest request) {
        return redisService.findMfaByIdentity(request.email());
    }
}
