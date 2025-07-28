package com.auth_example.user_service.users;

import com.auth_example.user_service.exceptions.UserMfaAlreadyEnabledException;
import com.auth_example.user_service.exceptions.UserNotFoundException;
import com.auth_example.user_service.exceptions.DuplicatedEmailException;
import com.auth_example.user_service.users.models.api.CreateUserRequest;
import com.auth_example.user_service.users.models.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapperImpl mapper;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findOneByEmail(String email) {
        return userRepository.findOneByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email " + email + " does not exist"));
    }

    public User create(@Valid CreateUserRequest request) {
        // check if user with this email already existed
        Optional<User> existingUser = userRepository.findOneByEmail(request.email());
        if (existingUser.isPresent()) {
            throw new DuplicatedEmailException("user with this email already existed");
        }

        Mfa mfa = new Mfa(false, null, "", null);
        User newUser = mapper.createUserRequestToUser(request, mfa);
        return userRepository.save(newUser);
    }

//    public User enableMfa(UUID userId, @Valid CreateMfaRequest request) {
//        User user = userRepository.findOneById(userId)
//                .orElseThrow(() -> new UserNotFoundException("user with id " + userId + " does not exist"));
//
//        Mfa userMfa = user.getMfa();
//        boolean isMfaEnabled = userMfa.isEnabled();
//        if (isMfaEnabled) {
//            throw new UserMfaAlreadyEnabledException("this user has already enabled mfa");
//        }
//
//        userMfa.setEnabled(true);
//        userMfa.setTarget(request.target());
//        userMfa.setMethod(request.method());
//
//        return userRepository.save(user);
//    }

    public UserResponse sanitizeUser(User user) {
        return mapper.userToUserResponse(user);
    }

    public boolean emailExist(String email) {
        Optional<User> user = userRepository.findOneByEmail(email);
        return user.isPresent();
    }

    public void enableMfa(String email) {
        User user = userRepository.findOneByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email " + email + " does not exist"));

        Mfa userMfa = user.getMfa();
        boolean isMfaEnabled = userMfa.isEnabled();
        if (isMfaEnabled) {
            throw new UserMfaAlreadyEnabledException("this user has already enabled mfa");
        }

        userMfa.setEnabled(true);
        userRepository.save(user);
    }
}
