package com.auth_example.auth_service.users;

import com.auth_example.auth_service.auth.models.LoginRequest;
import com.auth_example.auth_service.exceptions.EmailAlreadyExistException;
import com.auth_example.auth_service.exceptions.InvalidEmailPasswordException;
import com.auth_example.auth_service.exceptions.RedisUserNotFoundException;
import com.auth_example.auth_service.mfa.MfaChallengeType;
import com.auth_example.auth_service.redis.RedisService;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.auth_service.users.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    public void checkIfUserEmailExist(String email) {
        boolean isUserEmailExist = userClient.checkIfEmailExist(email);
        if (isUserEmailExist) {
            throw new EmailAlreadyExistException("the email used already has an existing account");
        }
    }

    public User findOneByEmail(String email) {
        return userClient.findOneByEmail(email);
    }

    public User createUser(String email, MfaChallengeType mfaMethod) {
        // find temporary redis user
        Optional<NewUser> redisAttempt = redisService.findNewUserByEmail(email);
        if (redisAttempt.isEmpty()) {
            throw new RedisUserNotFoundException("user with email " + email + " not found in redis");
        }

        NewUser newUser = redisAttempt.get();

        return userClient.create(newUser);
    }

    public User validatePassword(LoginRequest request) {
        // get user
        User user = userClient.findOneByEmail(request.email());
        // hash password
        boolean isPasswordCorrect = passwordEncoder.matches(request.password(), user.getPassword());
        if (!isPasswordCorrect) {
            throw new InvalidEmailPasswordException("invalid email or password");
        }
        return user;
    }

    public void enableMfa(String email, MfaChallengeType type, String target) {
        userClient.enableMfa(email, type, target);
    }
}
