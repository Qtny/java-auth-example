package com.auth_example.challenge_service.redis;

import com.auth_example.challenge_service.mfa.email.EmailMfaChallenge;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, MfaChallenge> mfaEntryRedisTemplate;
    private final RedisTemplate<String, EmailMfaChallenge> emailMfaTemplate;
    private static final String CHALLENGE_BY_ID_KEY_PREFIX = "mfa:challenge:id:";
    private static final String CHALLENGE_BY_EMAIL_KEY_PREFIX = "mfa:challenge:email:";

    public void storeUserEntry(MfaChallenge entry, Duration ttl) {
        log.info("INFO :: storing mfa entry");
        mfaEntryRedisTemplate.opsForValue().set(CHALLENGE_BY_ID_KEY_PREFIX + entry.getId(), entry, ttl);
        mfaEntryRedisTemplate.opsForValue().set(CHALLENGE_BY_EMAIL_KEY_PREFIX + entry.getEmail(), entry, ttl);
    }

    public Optional<MfaChallenge> findChallengeByEmail(String email) {
        log.info("INFO :: finding challenge by email");
        return Optional.ofNullable(mfaEntryRedisTemplate.opsForValue().get(CHALLENGE_BY_EMAIL_KEY_PREFIX + email));
    }

    public Optional<MfaChallenge> findChallengeByChallengeId(UUID id) {
        log.info("INFO :: finding challenge by challenge id");
        return Optional.ofNullable(mfaEntryRedisTemplate.opsForValue().get(CHALLENGE_BY_ID_KEY_PREFIX + id));
    }


    public void storeEmailMfaEntry(EmailMfaChallenge entry, Duration ttl) {
        log.info("INFO :: storing email mfa entry");
        emailMfaTemplate.opsForValue().set(CHALLENGE_BY_ID_KEY_PREFIX + entry.id(), entry, ttl);
        emailMfaTemplate.opsForValue().set(CHALLENGE_BY_EMAIL_KEY_PREFIX + entry.email(), entry, ttl);
    }

    public Optional<EmailMfaChallenge> findEmailMfaById(UUID id) {
        log.info("INFO :: finding email challenge by challenge id");
        return Optional.ofNullable(emailMfaTemplate.opsForValue().get(CHALLENGE_BY_ID_KEY_PREFIX + id));
    }

    public Optional<EmailMfaChallenge> findEmailMfaByEmail(String email) {
        log.info("INFO :: finding email challenge by email");
        return Optional.ofNullable(emailMfaTemplate.opsForValue().get(CHALLENGE_BY_EMAIL_KEY_PREFIX + email));
    }
}
