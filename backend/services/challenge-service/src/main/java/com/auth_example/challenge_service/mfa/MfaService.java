package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.exceptions.ChallengeNotFoundException;
import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import com.auth_example.challenge_service.otp.OtpService;
import com.auth_example.challenge_service.redis.RedisService;
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

    private final OtpService otpService;
    private final MfaDtoMapperImpl mapper;
    private final RedisService redisService;

    public MfaChallenge create(@Valid CreateChallengeRequest request) {
        // generate code
        String code = otpService.generateOtp(6);

        log.info("INFO :: creating challenge");
        MfaChallenge challenge = mapper.createChallengeRequestToMfaChallenge(request, code);
        redisService.storeUserEntry(challenge, Duration.ofMinutes(5));

        return challenge;
    }

    public boolean compare(UUID challengeId, String code) {
        log.info("comparing code with challenge");
        return true;
    }

    public MfaChallenge findOneByEmail(String email) {
        Optional<MfaChallenge> redisAttempt = redisService.findChallengeByEmail(email);
        if (redisAttempt.isPresent()) {
            return redisAttempt.get();
        }

        throw new ChallengeNotFoundException("challenge for email " + email + " does not exist");
    }

    public MfaChallenge findOneByChallengeId(UUID challengeId) {
        Optional<MfaChallenge> redisAttempt = redisService.findChallengeByChallengeId(challengeId);
        if (redisAttempt.isPresent()) {
            return redisAttempt.get();
        }

        throw new ChallengeNotFoundException("challenge for challenge id " + challengeId + " does not exist");
    }
}
