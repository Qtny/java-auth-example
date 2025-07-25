package com.auth_example.auth_service.redis;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.users.models.NewUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, NewUser> newUserRedisTemplate;
    private static final String REGISTRATION_KEY_PREFIX = "auth:new-user:email:";

    public Optional<NewUser> checkIfUserExist(String email) {
        log.info("INFO :: checking if temp user exist in redis");
        Optional<NewUser> user = Optional.ofNullable(newUserRedisTemplate.opsForValue().get(REGISTRATION_KEY_PREFIX + email));
        log.info(user.getClass().getName());
        return user;
    }

    public void storeUser(NewUser entry, Duration ttl) {
        log.info("INFO :: storing temp user");
        newUserRedisTemplate.opsForValue().set(REGISTRATION_KEY_PREFIX + entry.getEmail(), entry, ttl);
    }

    public Optional<NewUser> findNewUserByEmail(String email) {
        return Optional.ofNullable(newUserRedisTemplate.opsForValue().get(REGISTRATION_KEY_PREFIX + email));
    }
}
