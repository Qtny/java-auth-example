package com.auth_example.auth_service.users;

import com.auth_example.auth_service.exceptions.EmailAlreadyExistException;
import com.auth_example.auth_service.users.models.NewUser;
import com.auth_example.auth_service.users.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;

    public void checkIfUserEmailExist(String email) {
        boolean isUserEmailExist = userClient.checkIfEmailExist(email);
        if (isUserEmailExist) {
            throw new EmailAlreadyExistException("the email used already has an existing account");
        }
    }

    public User findOneByEmail(String email) {
        return userClient.findOneByEmail(email);
    }

    public User createUser(NewUser user) {
        return userClient.create(user);
    }
}
