package com.auth_example.user_service.users;

import com.auth_example.user_service.exceptions.MfaNotSetupException;
import com.auth_example.user_service.exceptions.UserMfaAlreadyEnabledException;
import com.auth_example.user_service.exceptions.UserNotFoundException;
import com.auth_example.user_service.exceptions.DuplicatedEmailException;
import com.auth_example.user_service.users.models.api.CreateUserRequest;
import com.auth_example.user_service.users.models.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public UserResponse sanitizeUser(User user) {
        return mapper.userToUserResponse(user);
    }

    public boolean emailExist(String email) {
        Optional<User> user = userRepository.findOneByEmail(email);
        return user.isPresent();
    }

    public void enableMfa(String email, MfaMethod type, String target) {
        User user = userRepository.findOneByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email " + email + " does not exist"));

        Mfa userMfa = user.getMfa();
        boolean isMfaEnabled = userMfa.isEnabled();
        if (isMfaEnabled) {
            throw new UserMfaAlreadyEnabledException("user already enabled mfa");
        }

        user.setStatus(UserStatus.ACTIVE);
        userMfa.setEnabled(true);
        userMfa.setMethod(type);
        userMfa.setTarget(target);
        userMfa.setLastVerifiedDate(LocalDate.now());
        userRepository.save(user);
    }

    public void removeMfa(String email) {
        User user = userRepository.findOneByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user with email " + email + " does not exist"));

        Mfa userMfa = user.getMfa();
        if (!userMfa.isEnabled()) {
            throw new MfaNotSetupException("mfa has not been setup before");
        }
        userMfa.setEnabled(false);
        userMfa.setMethod(null);
        userMfa.setTarget("");
        userRepository.save(user);
    }
}
