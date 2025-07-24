package com.auth_example.challenge_service.redis;

import com.auth_example.challenge_service.user.models.UserEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, UserEntry> userRedisTemplate;
    private static final String REGISTRATION_KEY_PREFIX = "registration:pending:";

    public Optional<UserEntry> checkIfTempUserExist(String email) {
        log.info("INFO :: checking if temp user exist in redis");
        Optional<UserEntry> user = Optional.ofNullable(userRedisTemplate.opsForValue().get(REGISTRATION_KEY_PREFIX + email));
        log.info(user.getClass().getName());
        return user;
    }

    public void storeUserEntry(UserEntry entry, Duration ttl) {
        log.info("INFO :: storing temp user");
        userRedisTemplate.opsForValue().set(REGISTRATION_KEY_PREFIX + entry.getEmail(), entry, ttl);
    }

    public Optional<UserEntry> findUserEntryByUserId(String email) {
        return Optional.ofNullable(userRedisTemplate.opsForValue().get(REGISTRATION_KEY_PREFIX + email));
    }
}
