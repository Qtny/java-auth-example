package com.auth_example.auth_service.users;

import com.auth_example.auth_service.exceptions.EmailAlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
