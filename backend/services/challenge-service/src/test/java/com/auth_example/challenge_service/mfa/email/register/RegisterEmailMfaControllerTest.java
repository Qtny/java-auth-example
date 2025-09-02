package com.auth_example.challenge_service.mfa.email.register;

import com.auth_example.challenge_service.exceptions.ChallengeNotFoundException;
import com.auth_example.challenge_service.exceptions.CodeDoesNotMatchException;
import com.auth_example.challenge_service.exceptions.EmailMismatchException;
import com.auth_example.challenge_service.exceptions.RedisException;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.email.EmailService;
import com.auth_example.challenge_service.mfa.email.models.EmailCreateRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaFindByEmailRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailValidateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.auth_example.common_service.core.responses.ApiErrorCode.*;
import static com.auth_example.common_service.core.responses.ApiErrorCode.ENTITY_NOT_FOUND;
import static com.auth_example.common_service.core.responses.ApiErrorCode.MFA_CODE_INCORRECT;
import static com.auth_example.common_service.core.responses.ApiErrorCode.UNAUTHORIZED;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RegisterEmailMfaControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RegisterEmailMfaService registerEmailMfaService;
    @MockitoBean
    private EmailService emailService;
    private final ObjectMapper om = new ObjectMapper();

    private RequestPostProcessor internalUseHeader() {
        return request -> {
            request.addHeader("X-Internal-Use", "gateway");
            return request;
        };
    }

    private final UUID CHALLENGE_ID = UUID.randomUUID();
    private final String EMAIL = "test@example.com";
    private final String OTP_CODE = "123456";
    private EmailMfaChallenge mockChallenge;

    @BeforeEach
    void setup() {
        mockChallenge = new EmailMfaChallenge(CHALLENGE_ID, EMAIL, OTP_CODE, LocalDate.now());
    }

    @Test
    @DisplayName("GET [/api/v1/mfa/register/email/{email} - should return status 403 for external API call")
    void shouldReturnForbiddenIfNotInternal() throws Exception {
        mockMvc.perform(get("/api/v1/mfa/register/email/" + EMAIL))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/register/email] - should return status 200 with challenge id when challenge is successfully created")
    public void shouldReturnChallengeIdIfChallengeCreatedSuccessfully() throws Exception {
        // arrange
        EmailCreateRequest mockCreateRequest = new EmailCreateRequest(EMAIL);
        EmailMfaFindByEmailRequest mockFindRequest = new EmailMfaFindByEmailRequest(mockCreateRequest.email());

        when(registerEmailMfaService.findOne(mockFindRequest)).thenReturn(Optional.empty());
        when(registerEmailMfaService.create(mockCreateRequest)).thenReturn(mockChallenge);
        doNothing().when(emailService).sendOtpEmail(EMAIL, OTP_CODE);
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(nullValue()))
                .andExpect(jsonPath("$.data.challengeId").value(mockChallenge.id().toString()));

    }

    @Test
    @DisplayName("POST [/api/v1/mfa/register/email] - should return status 500 with [INTERNAL ERROR] when otp service given invalid number length")
    public void shouldThrowInternalErrorIfChallengeCreationInvalidOtpLength() throws Exception {
        // arrange
        EmailCreateRequest mockCreateRequest = new EmailCreateRequest(EMAIL);
        EmailMfaFindByEmailRequest mockFindRequest = new EmailMfaFindByEmailRequest(mockCreateRequest.email());

        when(registerEmailMfaService.findOne(mockFindRequest)).thenReturn(Optional.empty());
        when(registerEmailMfaService.create(mockCreateRequest)).thenThrow(new IllegalArgumentException("simulate invalid otp length"));
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateRequest))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(INTERNAL_ERROR.name()));

        verify(registerEmailMfaService).findOne(mockFindRequest);
        verify(registerEmailMfaService).create(mockCreateRequest);
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource("invalidCreateChallengeInput")
    @DisplayName("POST [/api/v1/mfa/register/email] - should return status 400 with [VALIDATION_ERROR] when given invalid parameters")
    public void shouldThrowValidationErrorIfChallengeCreationParameterInvalid(EmailCreateRequest invalidRequests) throws Exception {
        // arrange
        EmailMfaFindByEmailRequest mockFindRequest = new EmailMfaFindByEmailRequest(invalidRequests.email());

        when(registerEmailMfaService.findOne(mockFindRequest)).thenReturn(Optional.empty());
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidRequests))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(VALIDATION_ERROR.name()));

        verify(registerEmailMfaService, never()).findOne(any());
        verify(registerEmailMfaService, never()).create(any());
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    static Stream<EmailCreateRequest> invalidCreateChallengeInput() {
        return Stream.of(
                new EmailCreateRequest(""),
                new EmailCreateRequest("invalid-email"),
                new EmailCreateRequest(null)
        );
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/register/email] - should return status 500 with [INTERNAL_ERROR] when failed to save challenge to redis")
    public void shouldThrowValidationErrorIfChallengeCreationCannotSaveToRedis() throws Exception {
        // arrange
        EmailCreateRequest mockCreateRequest = new EmailCreateRequest(EMAIL);
        EmailMfaFindByEmailRequest mockFindRequest = new EmailMfaFindByEmailRequest(mockCreateRequest.email());

        when(registerEmailMfaService.findOne(mockFindRequest)).thenReturn(Optional.empty());
        when(registerEmailMfaService.create(mockCreateRequest)).thenThrow(new RedisException("simulate unable to save to redis"));
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateRequest))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(INTERNAL_ERROR.name()));

        verify(registerEmailMfaService).findOne(mockFindRequest);
        verify(registerEmailMfaService).create(mockCreateRequest);
        verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    }

    @Test
    @DisplayName("GET [/api/v1/mfa/register/email/{email}] - should return status 200 with challenge when challenge with email exist in redis")
    public void shouldReturnChallengeIfChallengeWithEmailExistInRedis() throws Exception {
        // arrange
        EmailMfaFindByEmailRequest mockFindRequest = new EmailMfaFindByEmailRequest(EMAIL);
        when(registerEmailMfaService.findOneOrThrow(mockFindRequest)).thenReturn(mockChallenge);
        // act assert
        mockMvc.perform(get("/api/v1/mfa/register/email/" + EMAIL).with(internalUseHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(nullValue()))
                .andExpect(jsonPath("$.data.id").value(mockChallenge.id().toString()))
                .andExpect(jsonPath("$.data.code").value(mockChallenge.code()))
                .andExpect(jsonPath("$.data.email").value(mockChallenge.email()));
    }

    @Test
    @DisplayName("GET [/api/v1/mfa/register/email/{email}] - should return status 404 with [ENTITY_NOT_FOUND] when challenge with email does not exist in redis")
    public void shouldThrowEntityNotFoundIfEmailDoesNotExistInRedisForFindOne() throws Exception {
        // arrange
        EmailMfaFindByEmailRequest mockFindRequest = new EmailMfaFindByEmailRequest(EMAIL);
        when(registerEmailMfaService.findOneOrThrow(mockFindRequest)).thenThrow(new ChallengeNotFoundException("simulate missing challenge"));
        // act assert
        mockMvc.perform(get("/api/v1/mfa/register/email/" + EMAIL).with(internalUseHeader()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(ENTITY_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/register/email/verify] - should return status 200 with challenge type and target when challenge is successful")
    public void shouldReturnTypeAndTargetIfChallengeSuccess() throws Exception {
        // arrange
        EmailValidateRequest mockValidateRequest = new EmailValidateRequest(CHALLENGE_ID, OTP_CODE, EMAIL);
        when(registerEmailMfaService.validate(mockValidateRequest)).thenReturn(mockChallenge);
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email/verify")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(nullValue()))
                .andExpect(jsonPath("$.data.email").value(mockChallenge.email()))
                .andExpect(jsonPath("$.data.type").value(MfaChallengeType.EMAIL.name()));
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/register/email/verify] - should return status 404 with [ENTITY_NOT_FOUND] when challenge with email does not exist in redis")
    public void shouldThrowEntityNotFoundIfEmailDoesNotExistInRedisForVerify() throws Exception {
        // arrange
        EmailValidateRequest mockValidateRequest = new EmailValidateRequest(CHALLENGE_ID, OTP_CODE, EMAIL);
        when(registerEmailMfaService.validate(mockValidateRequest)).thenThrow(new ChallengeNotFoundException("simulate unable to find challenge"));
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email/verify")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(ENTITY_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/register/email/verify] - should return status 401 with [UNAUTHORIZED] when challenge email does not match with requester email")
    public void shouldThrowUnauthorizedIfEmailMismatchForEmailMfa() throws Exception {
        // arrange
        EmailValidateRequest mockValidateRequest = new EmailValidateRequest(CHALLENGE_ID, OTP_CODE, EMAIL);
        when(registerEmailMfaService.validate(mockValidateRequest)).thenThrow(new EmailMismatchException("simulate mismatched email"));
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email/verify")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(UNAUTHORIZED.name()));
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/register/email/verify] - should return status 500 with [MFA_CODE_INCORRECT] when code given does not match with challenge from redis")
    public void shouldThrowMfaCodeIncorrectIfCodeMismatchForEmailMfa() throws Exception {
        // arrange
        EmailValidateRequest mockValidateRequest = new EmailValidateRequest(CHALLENGE_ID, OTP_CODE, EMAIL);
        when(registerEmailMfaService.validate(mockValidateRequest)).thenThrow(new CodeDoesNotMatchException("simulate mismatched code"));
        // act assert
        mockMvc.perform(post("/api/v1/mfa/register/email/verify")
                        .with(internalUseHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(MFA_CODE_INCORRECT.name()));
    }
}
