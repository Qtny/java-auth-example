package com.auth_example.challenge_service.mfa.email;

import com.auth_example.challenge_service.exceptions.ChallengeNotFoundException;
import com.auth_example.challenge_service.exceptions.CodeDoesNotMatchException;
import com.auth_example.challenge_service.exceptions.EmailMismatchException;
import com.auth_example.challenge_service.exceptions.RedisException;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.MfaDtoMapperImpl;
import com.auth_example.challenge_service.mfa.email.models.EmailCreateRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaFindByEmailRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailValidateRequest;
import com.auth_example.challenge_service.otp.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailMfaServiceTest {

    @Mock
    private OtpService otpService;
    @Mock
    private MfaDtoMapperImpl mapper;
    @Mock
    private EmailMfaRedisService redisService;
    @InjectMocks
    private EmailMfaService emailMfaService;

    private final int OTP_LENGTH = 6;
    private final int REDIS_STORE_DURATION_IN_MINUTES = 5;
    private EmailMfaChallenge mockChallenge;

    @BeforeEach
    void setup() {
        mockChallenge = new EmailMfaChallenge(UUID.randomUUID(), "test@example.com", "123456", LocalDate.now());
    }

    @Test
    @DisplayName("[EmailMfaService :: getType] - should return MfaChallengeType.EMAIL")
    public void shouldReturnEmailMfaType() {
        // arrange
        // act
        MfaChallengeType type = emailMfaService.getType();
        // assert
        assertEquals(MfaChallengeType.EMAIL, type);
    }

    @Test
    @DisplayName("[EmailMfaService :: create] - should return challenge object if challenge is created and stored in redis successfully")
    public void shouldReturnChallengeIfChallengeSuccessfullyCreated() {
        // arrange
        EmailCreateRequest mockRequest = new EmailCreateRequest(mockChallenge.email());
        when(otpService.generateOtp(OTP_LENGTH)).thenReturn(mockChallenge.code());
        when(mapper.createChallengeRequestToMfaChallenge(mockRequest, mockChallenge.code())).thenReturn(mockChallenge);
        doNothing().when(redisService).storeMfaEntry(mockChallenge, Duration.ofMinutes(REDIS_STORE_DURATION_IN_MINUTES));
        // act
        EmailMfaChallenge functionCall = emailMfaService.create(mockRequest);
        // assert
        assertEquals(mockChallenge, functionCall);
        verify(otpService).generateOtp(anyInt());
        verify(mapper).createChallengeRequestToMfaChallenge(eq(mockRequest), anyString());
        verify(redisService).storeMfaEntry(mockChallenge, Duration.ofMinutes(REDIS_STORE_DURATION_IN_MINUTES));
    }

    @Test
    @DisplayName("[EmailMfaService :: create] - should throw IllegalArgumentException if otp service cannot generate otp successfully")
    public void shouldNotReturnIfCreateEmailMfaIllegalArgumentException() {
        // arrange
        EmailCreateRequest mockRequest = new EmailCreateRequest(mockChallenge.email());
        when(otpService.generateOtp(OTP_LENGTH)).thenThrow(new IllegalArgumentException("number invalid"));
        // act
        // assert
        assertThrows(IllegalArgumentException.class, () -> emailMfaService.create(mockRequest));
        verify(mapper, never()).createChallengeRequestToMfaChallenge(any(), anyString());
        verify(redisService, never()).storeMfaEntry(any(), any());
    }

    @Test
    @DisplayName("[EmailMfaService :: create] - should throw RedisException if redis service fails to execute store mfa entry")
    public void shouldNotReturnIfCreateEmailMfaRedisException() {
        // arrange
        EmailCreateRequest mockRequest = new EmailCreateRequest(mockChallenge.email());
        when(otpService.generateOtp(OTP_LENGTH)).thenReturn(mockChallenge.code());
        when(mapper.createChallengeRequestToMfaChallenge(mockRequest, mockChallenge.code())).thenReturn(mockChallenge);
        doThrow(new RedisException("redis is down")).when(redisService).storeMfaEntry(mockChallenge, Duration.ofMinutes(REDIS_STORE_DURATION_IN_MINUTES));
        // act
        // assert
        assertThrows(RedisException.class, () -> emailMfaService.create(mockRequest));
        verify(otpService).generateOtp(anyInt());
        verify(mapper).createChallengeRequestToMfaChallenge(any(), anyString());
    }

    @Test
    @DisplayName("[EmailMfaService :: validate] - should return challenge if validation is successful")
    public void shouldReturnChallengeIfChallengeSuccessfullyValidated() {
        // arrange
        EmailValidateRequest mockRequest = new EmailValidateRequest(mockChallenge.id(), mockChallenge.code(), mockChallenge.email());
        when(redisService.findMfaById(mockRequest.challengeId())).thenReturn(Optional.ofNullable(mockChallenge));
        // act
        EmailMfaChallenge functionCall = emailMfaService.validate(mockRequest);
        // assert
        assertEquals(mockChallenge, functionCall);
        verify(redisService).findMfaById(mockRequest.challengeId());
    }

    @Test
    @DisplayName("[EmailMfaService :: validate] - should throw [ChallengeNotFoundException] if no challenge found in redis")
    public void shouldThrowChallengeNotFoundExceptionIfNoChallengeFoundOnRedis() {
        // arrange
        EmailValidateRequest mockRequest = new EmailValidateRequest(UUID.randomUUID(), mockChallenge.code(), mockChallenge.email());
        when(redisService.findMfaById(mockRequest.challengeId())).thenReturn(Optional.empty());
        // act
        // assert
        assertThrows(ChallengeNotFoundException.class, () -> emailMfaService.validate(mockRequest));
        verify(redisService).findMfaById(mockRequest.challengeId());
    }

    @Test
    @DisplayName("[EmailMfaService :: validate] - should throw [EmailMismatchException] if email from redis-stored challenge does not match with the requester")
    public void shouldThrowEmailMismatchExceptionIfEmailDoesNotMatch() {
        // arrange
        EmailValidateRequest mockRequest = new EmailValidateRequest(mockChallenge.id(), mockChallenge.code(), "wrong@example.com");
        when(redisService.findMfaById(mockRequest.challengeId())).thenReturn(Optional.ofNullable(mockChallenge));
        // act
        // assert
        assertThrows(EmailMismatchException.class, () -> emailMfaService.validate(mockRequest));
        verify(redisService).findMfaById(mockRequest.challengeId());
    }

    @Test
    @DisplayName("[EmailMfaService :: validate] - should throw [CodeDoesNotMatchException] if validation code does not match")
    public void shouldThrowCodeDoesNotMatchExceptionIfCodeDoesNotMatch() {
        // arrange
        EmailValidateRequest mockRequest = new EmailValidateRequest(mockChallenge.id(), "", mockChallenge.email());
        when(redisService.findMfaById(mockRequest.challengeId())).thenReturn(Optional.ofNullable(mockChallenge));
        // act
        // assert
        assertThrows(CodeDoesNotMatchException.class, () -> emailMfaService.validate(mockRequest));
        verify(redisService).findMfaById(mockRequest.challengeId());
    }

    @Test
    @DisplayName("[EmailMfaService :: findOneById] - should return challenge if challenge id exist in redis")
    public void shouldReturnChallengeIfIdExistInRedis() {
        // arrange
        UUID mockUuid = mockChallenge.id();
        when(redisService.findMfaById(mockUuid)).thenReturn(Optional.ofNullable(mockChallenge));
        // act
        EmailMfaChallenge functionCall = emailMfaService.findOneById(mockUuid);
        // assert
        assertEquals(mockChallenge, functionCall);
        verify(redisService).findMfaById(mockUuid);
    }

    @Test
    @DisplayName("[EmailMfaService :: findOneById] - should not return anything if challenge id does not exist in redis")
    public void shouldNotReturnIfIdDoesNotExistInRedis() {
        // arrange
        UUID mockUuid = UUID.randomUUID();
        when(redisService.findMfaById(mockUuid)).thenReturn(Optional.empty());
        // act
        // assert
        assertThrows(ChallengeNotFoundException.class, () -> emailMfaService.findOneById(mockUuid));
        verify(redisService).findMfaById(mockUuid);
    }

    @Test
    @DisplayName("[EmailMfaService :: findOneOrThrow] - should return challenge if email exist in redis")
    public void shouldReturnChallengeIfEmailExistInRedis() {
        // arrange
        EmailMfaFindByEmailRequest mockRequest = new EmailMfaFindByEmailRequest(mockChallenge.email());
        when(redisService.findMfaByIdentity(mockRequest.email())).thenReturn(Optional.ofNullable(mockChallenge));
        // act
        EmailMfaChallenge functionCall = emailMfaService.findOneOrThrow(mockRequest);
        // assert
        assertEquals(mockChallenge, functionCall);
        verify(redisService).findMfaByIdentity(mockRequest.email());
    }

    @Test
    @DisplayName("[EmailMfaService :: findOneOrThrow] - should throw [ChallengeNotFoundException] if email exist in redis")
    public void shouldThrowChallengeNotFoundExceptionIfEmailDoesNotExistInRedis() {
        // arrange
        EmailMfaFindByEmailRequest mockRequest = new EmailMfaFindByEmailRequest("wrong@example.com");
        when(redisService.findMfaByIdentity(mockRequest.email())).thenReturn(Optional.empty());
        // act
        // assert
        assertThrows(ChallengeNotFoundException.class, () -> emailMfaService.findOneOrThrow(mockRequest));
        verify(redisService).findMfaByIdentity(mockRequest.email());
    }

    @Test
    @DisplayName("[EmailMfaService :: findOne] - should return challenge if email exist in redis")
    public void shouldReturnChallengeIfFindOneEmailExistInRedis() {
        // arrange
        EmailMfaFindByEmailRequest mockRequest = new EmailMfaFindByEmailRequest(mockChallenge.email());
        when(redisService.findMfaByIdentity(mockRequest.email())).thenReturn(Optional.ofNullable(mockChallenge));
        // act
        Optional<EmailMfaChallenge> functionCall = emailMfaService.findOne(mockRequest);
        // assert
        assertTrue(functionCall.isPresent());
        verify(redisService).findMfaByIdentity(mockRequest.email());
    }

    @Test
    @DisplayName("[EmailMfaService :: findOne] - should not return anything if email does not exist in redis")
    public void shouldNotReturnIfEmailDoesNotExistInRedis() {
        // arrange
        EmailMfaFindByEmailRequest mockRequest = new EmailMfaFindByEmailRequest("wrong@example.com");
        when(redisService.findMfaByIdentity(mockRequest.email())).thenReturn(Optional.empty());
        // act
        Optional<EmailMfaChallenge> functionCall = emailMfaService.findOne(mockRequest);
        // assert
        assertFalse(functionCall.isPresent());
        verify(redisService).findMfaByIdentity(mockRequest.email());
    }
}
