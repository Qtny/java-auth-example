package com.auth_example.challenge_service.redis;

import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
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

    private final RedisTemplate<String, EmailMfaChallenge> emailMfaTemplate;
    private static final String CHALLENGE_BY_ID_KEY_PREFIX = "mfa:challenge:id:";
    private static final String CHALLENGE_BY_EMAIL_KEY_PREFIX = "mfa:challenge:email:";
    private static final String CHALLENGE_BY_REGISTRATION_EMAIL_KEY_PREFIX = "mfa:challenge:registration:email:";
    private static final String CHALLENGE_BY_REGISTRATION_ID_KEY_PREFIX = "mfa:challenge:registration:id:";

    public void storeRegistrationEmailMfaEntry(EmailMfaChallenge entry, Duration ttl) {
        log.info("INFO :: storing registration email mfa entry");
        emailMfaTemplate.opsForValue().set(CHALLENGE_BY_REGISTRATION_ID_KEY_PREFIX + entry.id(), entry, ttl);
        emailMfaTemplate.opsForValue().set(CHALLENGE_BY_REGISTRATION_EMAIL_KEY_PREFIX + entry.email(), entry, ttl);
    }

    public Optional<EmailMfaChallenge> findRegistrationEmailMfaById(UUID id) {
        log.info("INFO :: finding registration email challenge by challenge id");
        return Optional.ofNullable(emailMfaTemplate.opsForValue().get(CHALLENGE_BY_REGISTRATION_ID_KEY_PREFIX + id));
    }

    public Optional<EmailMfaChallenge> findRegistrationEmailMfaByEmail(String email) {
        log.info("INFO :: finding registration email challenge by email");
        return Optional.ofNullable(emailMfaTemplate.opsForValue().get(CHALLENGE_BY_REGISTRATION_EMAIL_KEY_PREFIX + email));
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
