package com.auth_example.auth_service.users;

import com.auth_example.auth_service.exceptions.EmailAlreadyExistException;
import com.auth_example.auth_service.exceptions.RedisUserNotFoundException;
import com.auth_example.auth_service.mfa.models.EmailValidateResponse;
import com.auth_example.auth_service.redis.RedisService;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.auth_service.users.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;
    private final RedisService redisService;

    public void checkIfUserEmailExist(String email) {
        boolean isUserEmailExist = userClient.checkIfEmailExist(email);
        if (isUserEmailExist) {
            throw new EmailAlreadyExistException("the email used already has an existing account");
        }
    }

    public User findOneByEmail(String email) {
        return userClient.findOneByEmail(email);
    }

    public User createUser(EmailValidateResponse response) {
        // find temporary redis user
        Optional<NewUser> redisAttempt = redisService.findNewUserByEmail(response.target());
        if (redisAttempt.isEmpty()) {
            throw new RedisUserNotFoundException("user with email " + response.target() + " not found in redis");
        }

        NewUser newUser = redisAttempt.get();
        return userClient.create(newUser);
    }
}
