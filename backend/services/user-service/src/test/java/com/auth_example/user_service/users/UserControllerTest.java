package com.auth_example.user_service.users;

import com.auth_example.common_service.core.responses.ApiErrorCode;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.user_service.exceptions.DuplicatedEmailException;
import com.auth_example.user_service.exceptions.MfaNotSetupException;
import com.auth_example.user_service.exceptions.UserMfaAlreadyEnabledException;
import com.auth_example.user_service.exceptions.UserNotFoundException;
import com.auth_example.user_service.users.models.Address;
import com.auth_example.user_service.users.models.Mfa;
import com.auth_example.user_service.users.models.User;
import com.auth_example.user_service.users.models.UserResponse;
import com.auth_example.user_service.users.models.api.CreateUserRequest;
import com.auth_example.user_service.users.models.api.UpdateMfaRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    private final ObjectMapper om = new ObjectMapper();

    private final String mockEmail = "test@example.com";
    private Mfa sampleMfa;
    private User sampleUser;
    private UserResponse sampleUserResponse;

    private RequestPostProcessor internalUseHeader() {
        return request -> {
            request.addHeader("X-Internal-Use", "gateway");
            return request;
        };
    }

    @BeforeEach
    void setup() {
        sampleMfa = new Mfa(true, MfaMethod.EMAIL, "", null);
        Address address = new Address("test_street_1", "test_street_2", "test_city", "test_postcode", "test_state", "test_country");
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email(mockEmail)
                .password("test_password")
                .mfa(sampleMfa)
                .address(address)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
        sampleUserResponse = new UserResponse(sampleUser.getId(), sampleUser.getFirstName(), sampleUser.getLastName(), sampleUser.getEmail(), sampleUser.getMfa().isEnabled(), sampleUser.getStatus(), sampleUser.getAddress());
    }

    @Test
    @DisplayName("GET [/api/v1/users] - should return status 403 for external API call")
    void shouldReturnForbiddenIfNotInternal() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET [/api/v1/users] - should return status 200 with a list of users if there are existing users in the database")
    void shouldReturnUserList() throws Exception {
        List<User> users = List.of(sampleUser);
        List<UserResponse> sanitizedUsers = List.of(sampleUserResponse);
        // arrange
        when(userService.findAll()).thenReturn(users);
        when(userService.sanitizeUser(any(User.class))).thenReturn(sampleUserResponse);
        // act
        mockMvc.perform(get("/api/v1/users").with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].email").value(sampleUser.getEmail()))
                .andExpect(jsonPath("$.data[0].mfaEnabled").value(sampleUser.getMfa().isEnabled()))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
    }

    @Test
    @DisplayName("GET [/api/v1/users] - should return status 200 with a an empty list if there are no users in the database")
    void shouldReturnEmptyUserList() throws Exception {
        List<User> users = List.of(sampleUser);
        // arrange
        when(userService.findAll()).thenReturn(Collections.emptyList());
        // act
        mockMvc.perform(get("/api/v1/users").with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET [/api/v1/users/email/{email}] - should return status 200 with the user response if user exist in the database")
    void shouldReturnUserIfEmailExist() throws Exception {
        // arrange
        when(userService.findOneByEmail(mockEmail)).thenReturn(sampleUser);
        when(userService.sanitizeUser(any(User.class))).thenReturn(sampleUserResponse);
        // act
        mockMvc.perform(get("/api/v1/users/email/" + mockEmail).with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.email").value(sampleUser.getEmail()))
                .andExpect(jsonPath("$.data.mfaEnabled").value(sampleUser.getMfa().isEnabled()))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @DisplayName("GET [/api/v1/users/email/{email}] - should return status 404 if user with email does not exist in the database")
    void shouldThrowEntityNotFoundIfEmailNotExistForSanitizedUser() throws Exception {
        // arrange
        when(userService.findOneByEmail(mockEmail)).thenThrow(new UserNotFoundException("user not found"));
        // act
        mockMvc.perform(get("/api/v1/users/email/" + mockEmail).with(internalUseHeader()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.ENTITY_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("GET [/api/v1/users/email/{email}/raw] - should return status 200 with the user with password if user exist in the database")
    void shouldReturnRawUserIfExist() throws Exception {
        // arrange
        when(userService.findOneByEmail(mockEmail)).thenReturn(sampleUser);
        // act
        mockMvc.perform(get("/api/v1/users/email/" + mockEmail + "/raw").with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.id").value(sampleUser.getId().toString()))
                .andExpect(jsonPath("$.data.email").value(sampleUser.getEmail()))
                .andExpect(jsonPath("$.data.password").value(sampleUser.getPassword()));
    }

    @Test
    @DisplayName("GET [/api/v1/users/email/{email}/raw] - should return status 404 with 'ENTITY_NOT_FOUND' if user exist in the database")
    void shouldThrowEntityNotFoundIfEmailNotExistForRawUser() throws Exception {
        // arrange
        when(userService.findOneByEmail(mockEmail)).thenThrow(new UserNotFoundException("user not found"));
        // act
        mockMvc.perform(get("/api/v1/users/email/" + mockEmail + "/raw").with(internalUseHeader()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.ENTITY_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("GET [/api/v1/users/email/{email}/exist] - should return status 200 with true if email exist in the database")
    void shouldReturnTrueIfEmailExist() throws Exception {
        // arrange
        when(userService.emailExist(mockEmail)).thenReturn(true);
        // act
        mockMvc.perform(get("/api/v1/users/email/" + mockEmail + "/exist").with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("GET [/api/v1/users/email/{email}/exist] - should return status 200 with false if email does not exist in the database")
    void shouldReturnFalseIfEmailNotExist() throws Exception {
        // arrange
        when(userService.emailExist(mockEmail)).thenReturn(false);
        // act
        mockMvc.perform(get("/api/v1/users/email/" + mockEmail + "/exist").with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("POST [/api/v1/users] - should return status 200 with the sanitized user if user is successfully created")
    void shouldReturnSanitizedUserIfUserCreateSuccess() throws Exception {
        // arrange
        CreateUserRequest mockCreateUserRequest = new CreateUserRequest(sampleUser.getFirstName(), sampleUser.getLastName(), sampleUser.getEmail(), sampleUser.getPassword(), sampleUser.getAddress());
        when(userService.create(mockCreateUserRequest)).thenReturn(sampleUser);
        when(userService.sanitizeUser(sampleUser)).thenReturn(sampleUserResponse);
        mockMvc.perform(post("/api/v1/users")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateUserRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.email").value(sampleUser.getEmail()))
                .andExpect(jsonPath("$.data.mfaEnabled").value(sampleUser.getMfa().isEnabled()))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @DisplayName("POST [/api/v1/users] - should return status 405 with 'METHOD_NOT_ALLOWED' if user tries to register with email that already is in the database")
    void shouldThrowMethodNotAllowedIfUserCreateRepeatEmail() throws Exception {
        // arrange
        when(userService.findOneByEmail(mockEmail)).thenThrow(new DuplicatedEmailException("user with email already exist"));
        // act
        mockMvc.perform(get("/api/v1/users/email/" + mockEmail + "/raw").with(internalUseHeader()))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.METHOD_NOT_ALLOWED.name()));
    }

    @ParameterizedTest
    @MethodSource("invalidCreateUserRequests")
    @DisplayName("POST [/api/v1/users] - should return status 400 with 'VALIDATION_ERROR' if UpdateMfaRequest has invalid parameters")
    void shouldThrowValidationErrorIfUserCreationParameterInvalid(CreateUserRequest invalidRequests) throws Exception {
        // act
        mockMvc.perform(post("/api/v1/users")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidRequests))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.VALIDATION_ERROR.name()));
    }

    static Stream<CreateUserRequest> invalidCreateUserRequests() {
        Address validAddress = new Address("test_street_1", "test_street_2", "test_city", "test_postcode", "test_state", "test_country");
        return Stream.of(
                new CreateUserRequest("", "Doe", "test@example.com", "test_password", validAddress),
                new CreateUserRequest(null, "Doe", "test@example.com", "test_password", validAddress),
                new CreateUserRequest("John", "", "test@example.com", "test_password", validAddress),
                new CreateUserRequest("John", null, "test@example.com", "test_password", validAddress),
                new CreateUserRequest("John", null, "invalid_email", "test_password", validAddress),
                new CreateUserRequest("John", "Doe", "", "test_password", validAddress),
                new CreateUserRequest("John", "Doe", null, "test_password", validAddress),
                new CreateUserRequest("John", "Doe", "test@example.com", "", validAddress),
                new CreateUserRequest("John", "Doe", "test@example.com", null, validAddress),
                new CreateUserRequest("John", "Doe", "test@example.com", "test_password", new Address(null, "test_street_2", "test_city", "test_postcode", "test_state", "test_country")),
                new CreateUserRequest("John", "Doe", "test@example.com", "test_password", new Address("test_street_1", null, "test_city", "test_postcode", "test_state", "test_country")),
                new CreateUserRequest("John", "Doe", "test@example.com", "test_password", new Address("test_street_1", "test_street_2", null, "test_postcode", "test_state", "test_country")),
                new CreateUserRequest("John", "Doe", "test@example.com", "test_password", new Address("test_street_1", "test_street_2", "test_city", null, "test_state", "test_country")),
                new CreateUserRequest("John", "Doe", "test@example.com", "test_password", new Address("test_street_1", "test_street_2", "test_city", "test_postcode", null, "test_country")),
                new CreateUserRequest("John", "Doe", "test@example.com", "test_password", new Address("test_street_1", "test_street_2", "test_city", "test_postcode", "test_state", null))
        );
    }

    @Test
    @DisplayName("PATCH [/api/v1/users/mfa/enable] - should return status 200 with no data if user mfa is successfully enabled")
    void shouldReturnNullIfMfaEnabledSuccessfully() throws Exception {
        // arrange
        UpdateMfaRequest mockRequest = new UpdateMfaRequest(sampleUser.getEmail(), sampleUser.getMfa().getMethod(), sampleUser.getMfa().getTarget());
        doNothing().when(userService).enableMfa(anyString(), eq(MfaMethod.EMAIL), anyString());
        // act and assert
        mockMvc.perform(patch("/api/v1/users/mfa/enable")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @ParameterizedTest
    @MethodSource("invalidUpdateUserMfaRequests")
    @DisplayName("PATCH [/api/v1/users/mfa/enable] - should return status 400 with 'VALIDATION_ERROR' if UpdateMfaRequest has invalid parameters")
    void shouldThrowValidationErrorIfMfaUpdateParameterInvalid(UpdateMfaRequest invalidRequests) throws Exception {
        // act
        mockMvc.perform(patch("/api/v1/users/mfa/enable")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidRequests))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.VALIDATION_ERROR.name()));
    }

    static Stream<UpdateMfaRequest> invalidUpdateUserMfaRequests() {
        return Stream.of(
                new UpdateMfaRequest("", MfaMethod.EMAIL, "test@example.com"),
                new UpdateMfaRequest(null, MfaMethod.EMAIL, "test@example.com"),
                new UpdateMfaRequest("test@example.com", null, "test@example.com"),
                new UpdateMfaRequest("test@example.com", MfaMethod.EMAIL, null)
        );
    }

    @Test
    @DisplayName("PATCH [/api/v1/users/mfa/enable] - should return status 405 with 'METHOD_NOT_ALLOWED' if user mfa is already enabled")
    void shouldThrowMethodNotAllowedIfUserAlreadyEnabledMfa() throws Exception {
        // arrange
        UpdateMfaRequest mockRequest = new UpdateMfaRequest(sampleUser.getEmail(), sampleUser.getMfa().getMethod(), sampleUser.getMfa().getTarget());
        doThrow(new UserMfaAlreadyEnabledException("user already enabled mfa")).when(userService).enableMfa(anyString(), eq(MfaMethod.EMAIL), anyString());
        // act
        mockMvc.perform(patch("/api/v1/users/mfa/enable")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockRequest))
                )
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.METHOD_NOT_ALLOWED.name()));
    }

    @Test
    @DisplayName("PATCH [/api/v1/users/mfa/enable] - should return status 404 with 'ENTITY_NOT_FOUND' if user with email provided does not exist")
    void shouldThrowEntityNotFoundIfEmailNotExistForEnableMfa() throws Exception {
        // arrange
        UpdateMfaRequest mockRequest = new UpdateMfaRequest(sampleUser.getEmail(), sampleUser.getMfa().getMethod(), sampleUser.getMfa().getTarget());
        doThrow(new UserNotFoundException("user not found")).when(userService).enableMfa(anyString(), eq(MfaMethod.EMAIL), anyString());
        // act
        mockMvc.perform(patch("/api/v1/users/mfa/enable")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockRequest))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.ENTITY_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("DELETE [/api/v1/users/mfa] - should return status 200 with no data if user mfa is successfully removed")
    void shouldReturnNullIfMfaRemoveSuccessfully() throws Exception {
        // arrange
        doNothing().when(userService).removeMfa(mockEmail);
        // act and assert
        mockMvc.perform(delete("/api/v1/users/mfa").with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("DELETE [/api/v1/users/mfa] - should return status 404 with 'ENTITY_NOT_FOUND' if user with email provided does not exist")
    void shouldThrowEntityNotFoundIfEmailNotExistForRemoveMfa() throws Exception {
        // arrange
        doThrow(new UserNotFoundException("user not found")).when(userService).removeMfa(anyString());
        // act
        mockMvc.perform(delete("/api/v1/users/mfa")
                        .with(internalUseHeader())
                        .header("X-User-Email", "")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.ENTITY_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("DELETE [/api/v1/users/mfa] - should return status 405 with 'METHOD_NOT_ALLOWED' if user mfa is not enabled")
    void shouldThrowMethodNotAllowedIfMfaNotEnabledPreviously() throws Exception {
        // arrange
        doThrow(new MfaNotSetupException("mfa hasn't been setup")).when(userService).removeMfa(mockEmail);
        // act
        mockMvc.perform(delete("/api/v1/users/mfa")
                        .with(internalUseHeader())
                        .header("X-User-Email", mockEmail)
                )
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(ApiErrorCode.METHOD_NOT_ALLOWED.name()));
    }
}
