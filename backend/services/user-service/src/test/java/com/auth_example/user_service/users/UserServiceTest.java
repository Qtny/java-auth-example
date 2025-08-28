package com.auth_example.user_service.users;

import com.auth_example.user_service.exceptions.DuplicatedEmailException;
import com.auth_example.user_service.exceptions.MfaNotSetupException;
import com.auth_example.user_service.exceptions.UserMfaAlreadyEnabledException;
import com.auth_example.user_service.exceptions.UserNotFoundException;
import com.auth_example.user_service.users.models.Address;
import com.auth_example.user_service.users.models.Mfa;
import com.auth_example.user_service.users.models.User;
import com.auth_example.user_service.users.models.api.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDtoMapperImpl mapper;
    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setup() {
        Mfa mfa = new Mfa(false, null, "", null);
        Address address = new Address("test_street_1", "test_street_2", "test_city", "test_postcode", "test_state", "test_country");
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .password("test_password")
                .mfa(mfa)
                .address(address)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }

    // findAll() - if has list
    @Test
    @DisplayName("[User Service :: findAll] - should return list of user if there are users in database")
    void shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        List<User> users = userService.findAll();

        assertEquals(1, users.size());
        verify(userRepository).findAll();
    }

    // findAll() - if empty
    @DisplayName("[User Service :: findAll] - should return empty array in data if no users are present in database")
    @Test
    void shouldReturnEmptyListIfEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> users = userService.findAll();

        assertTrue(users.isEmpty());
        verify(userRepository).findAll();
    }

    // findOneByEmail(String email) - success
    @DisplayName("[User Service :: findOneByEmail] - should return user if user exist in database")
    @Test
    void shouldReturnUserWithEmail() {
        String mockEmail = "test@example.com";
        when(userRepository.findOneByEmail(mockEmail)).thenReturn(Optional.ofNullable(sampleUser));
        User user = userService.findOneByEmail(mockEmail);

        assertEquals(mockEmail, user.getEmail());
        verify(userRepository).findOneByEmail(mockEmail);
    }

    // findOneByEmail(String email) - success
    @Test
    @DisplayName("[User Service :: findOneByEmail] - should throw [UserNotFoundException] if no user's email matches in database")
    void shouldThrowWhenUserNotFoundByEmail() {
        String mockEmail = "test@example.com";
        when(userRepository.findOneByEmail(mockEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.findOneByEmail(mockEmail);
        });
    }

    // create - success
    @Test
    @DisplayName("[User Service :: create] - should return user if user is successfully created")
    void shouldCreateUserIfEmailDoesNotExist() {
        Address address = new Address("test_street_1", "test_street_2", "test_city", "test_postcode", "test_state", "test_country");
        CreateUserRequest mockRequest = new CreateUserRequest("John", "Doe", "test@example.com", "test_password", address);

        // arrange
        when(userRepository.findOneByEmail(mockRequest.email())).thenReturn(Optional.empty());
        when(mapper.createUserRequestToUser(eq(mockRequest), any(Mfa.class)))
                .thenReturn(sampleUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        // act
        User newUser = userService.create(mockRequest);

        // assert
        assertEquals(mockRequest.email(), newUser.getEmail());
        verify(userRepository).save(sampleUser);
    }

    // create - failed
    @Test
    @DisplayName("[User Service :: create] - should throw [DuplicatedEmailException] if email used already exist in database")
    void shouldThrowDuplicatedEmailExceptionIfEmailExist() {
        Address address = new Address("test_street_1", "test_street_2", "test_city", "test_postcode", "test_state", "test_country");
        CreateUserRequest mockRequest = new CreateUserRequest("John", "Doe", "test@example.com", "test_password", address);

        // arrange
        when(userRepository.findOneByEmail(mockRequest.email())).thenReturn(Optional.ofNullable(sampleUser));
        // act
        // assert
        assertThrows(DuplicatedEmailException.class, () -> {
            userService.create(mockRequest);
        });
        verify(userRepository, never()).save(any());
    }

    // sanitizeUser - success (for coverage)
    @Test
    @DisplayName("[User Service :: sanitizeUser] - placeholder to complete coverage")
    void shouldSanitizeUser() {
        // act
        userService.sanitizeUser(sampleUser);
    }

    // emailExist - exist
    @Test
    @DisplayName("[User Service :: emailExist] - should return true if email exist in database")
    void shouldReturnTrueIfEmailExist() {
        // arrange
        String mockEmail = "mock@email.com";
        when(userRepository.findOneByEmail(mockEmail)).thenReturn(Optional.ofNullable(sampleUser));
        // act
        boolean exist = userService.emailExist(mockEmail);
        // assert
        assertTrue(exist);
        verify(userRepository).findOneByEmail(mockEmail);
    }

    // emailExist - does not exist
    @Test
    @DisplayName("[User Service :: emailExist] - should return false if email does not exist in database")
    void shouldReturnFalseIfEmailNotExist() {
        // arrange
        String mockEmail = "mock@email.com";
        when(userRepository.findOneByEmail(mockEmail)).thenReturn(Optional.empty());
        // act
        boolean exist = userService.emailExist(mockEmail);
        // assert
        assertFalse(exist);
        verify(userRepository).findOneByEmail(mockEmail);
    }

    // enableMfa - user already exist
    @Test
    @DisplayName("[User Service :: enableMfa] - should throw [UserMfaAlreadyEnabledException] if user mfa is already enabled")
    void shouldThrowUserMfaAlreadyEnabledExceptionIfEmailAlreadyExist() {
        // arrange
        sampleUser.getMfa().setEnabled(true);
        when(userRepository.findOneByEmail(sampleUser.getEmail())).thenReturn(Optional.ofNullable(sampleUser));
        // act
        // assert
        MfaMethod mockMethod = MfaMethod.EMAIL;
        String mockTarget = sampleUser.getEmail();
        assertThrows(UserMfaAlreadyEnabledException.class, () -> userService.enableMfa(sampleUser.getEmail(), mockMethod, mockTarget));
        verify(userRepository, never()).save(sampleUser);
    }

    // enableMfa - success (email otp)
    @Test
    @DisplayName("[User Service :: enableMfa] - should return nothing when email mfa is successfully enabled")
    void shouldEnableEmailMfaWhenDisabled() {
        // precondition
        assertFalse(sampleUser.getMfa().isEnabled());
        // arrange
        when(userRepository.findOneByEmail(sampleUser.getEmail())).thenReturn(Optional.ofNullable(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // act
        MfaMethod mockMethod = MfaMethod.EMAIL;
        String mockTarget = sampleUser.getEmail();
        userService.enableMfa(sampleUser.getEmail(), mockMethod, mockTarget);
        // assert
        assertTrue(sampleUser.getMfa().isEnabled());
        assertEquals(mockMethod, sampleUser.getMfa().getMethod());
        assertEquals(mockTarget, sampleUser.getMfa().getTarget());
        verify(userRepository).save(sampleUser);
    }

    // enableMfa - success (totp otp)
    @Test
    @DisplayName("[User Service :: enableMfa] - should return nothing when totp mfa is successfully enabled")
    void shouldEnableTotpMfaWhenDisabled() {
        // precondition
        assertFalse(sampleUser.getMfa().isEnabled());
        // arrange
        when(userRepository.findOneByEmail(sampleUser.getEmail())).thenReturn(Optional.ofNullable(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // act
        MfaMethod mockMethod = MfaMethod.TOTP;
        userService.enableMfa(sampleUser.getEmail(), mockMethod, null);
        // assert
        assertTrue(sampleUser.getMfa().isEnabled());
        assertEquals(mockMethod, sampleUser.getMfa().getMethod());
        verify(userRepository).save(sampleUser);
    }

    // removeMfa - success
    @Test
    @DisplayName("[User Service :: removeMfa] - should return nothing when mfa is successfully removed")
    void shouldRemoveMfaIfEnabled() {
        // arrange
        MfaMethod mockMethod = MfaMethod.EMAIL;
        String mockTarget = sampleUser.getEmail();
        sampleUser.getMfa().setEnabled(true);
        sampleUser.getMfa().setMethod(mockMethod);
        sampleUser.getMfa().setTarget(mockTarget);
        when(userRepository.findOneByEmail(sampleUser.getEmail())).thenReturn(Optional.ofNullable(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // precondition
        assertTrue(sampleUser.getMfa().isEnabled());
        // act
        userService.removeMfa(sampleUser.getEmail());
        // assert
        assertFalse(sampleUser.getMfa().isEnabled());
        assertNull(sampleUser.getMfa().getMethod());
        assertEquals("", sampleUser.getMfa().getTarget());
        verify(userRepository).save(sampleUser);
    }

    // removeMfa - fail
    @Test
    @DisplayName("[User Service :: removeMfa] - should throw [UserNotFoundException] if email does not exist in database")
    void shouldThrowUserNotFoundIfEmailNotExist() {
        // arrange
        when(userRepository.findOneByEmail(sampleUser.getEmail())).thenReturn(Optional.empty());
        // act
        // assert
        assertThrows(UserNotFoundException.class, () -> userService.removeMfa(sampleUser.getEmail()));
        verify(userRepository, never()).save(sampleUser);
    }

    // removeMfa - fail
    @Test
    @DisplayName("[User Service :: removeMfa] - should throw [MfaNotSetupException] if user mfa has never been set up before")
    void shouldThrowMfaNotSetupIfEmailNotExist() {
        // arrange
        when(userRepository.findOneByEmail(sampleUser.getEmail())).thenReturn(Optional.ofNullable(sampleUser));
        // act
        // assert
        assertThrows(MfaNotSetupException.class, () -> userService.removeMfa(sampleUser.getEmail()));
        verify(userRepository, never()).save(sampleUser);
    }
}
