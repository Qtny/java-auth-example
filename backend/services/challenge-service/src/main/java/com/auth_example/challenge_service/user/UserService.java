package com.auth_example.challenge_service.user;

import com.auth_example.challenge_service.exceptions.UserAlreadyExistException;
import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.redis.RedisService;
import com.auth_example.challenge_service.user.models.UserEntry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final RedisService redisService;
    private final UserDtoMapperImpl mapper;

    public void checkIfTempUserExist(String email) {
        Optional<UserEntry> entry = redisService.checkIfTempUserExist(email);
        if (entry.isPresent()) {
            throw new UserAlreadyExistException("user is already in the registration process");
        };
    }

    public void temporaryStoreUser(CreateChallengeRequest request, UUID userId) {
        // create user entry
        UserEntry userEntry = mapper.createChallengeRequestToUserEntry(request, userId);

        // store into redis
        redisService.storeUserEntry(userEntry, Duration.ofMinutes(5));
    }
}
