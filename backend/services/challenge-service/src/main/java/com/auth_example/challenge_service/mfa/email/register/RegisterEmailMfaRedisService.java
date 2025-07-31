package com.auth_example.challenge_service.mfa.email.register;

import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import com.auth_example.challenge_service.redis.BaseRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterEmailMfaRedisService implements BaseRedisService<EmailMfaChallenge> {

    private final RedisTemplate<String, EmailMfaChallenge> emailMfaTemplate;
    private static final String CHALLENGE_BY_ID_KEY_PREFIX = "mfa:challenge:registration:id:";
    private static final String CHALLENGE_BY_EMAIL_KEY_PREFIX = "mfa:challenge:registration:email:";

    @Override
    public void storeMfaEntry(EmailMfaChallenge entry, Duration ttl) {
        log.info("INFO :: storing email mfa entry");
        emailMfaTemplate.opsForValue().set(CHALLENGE_BY_ID_KEY_PREFIX + entry.id(), entry, ttl);
        emailMfaTemplate.opsForValue().set(CHALLENGE_BY_EMAIL_KEY_PREFIX + entry.email(), entry, ttl);
    }

    @Override
    public Optional<EmailMfaChallenge> findMfaById(UUID id) {
        log.info("INFO :: finding email challenge by challenge id");
        return Optional.ofNullable(emailMfaTemplate.opsForValue().get(CHALLENGE_BY_ID_KEY_PREFIX + id));
    }

    @Override
    public Optional<EmailMfaChallenge> findMfaByIdentity(String identity) {
        log.info("INFO :: finding email challenge by email");
        return Optional.ofNullable(emailMfaTemplate.opsForValue().get(CHALLENGE_BY_EMAIL_KEY_PREFIX + identity));
    }
}