package com.auth_example.challenge_service.mfa.email.register;

import com.auth_example.challenge_service.exceptions.RedisException;
import com.auth_example.challenge_service.mfa.email.EmailMfaRedisService;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.SerializationException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterEmailMfaRedisServiceTest {
    @Mock
    private RedisTemplate<String, EmailMfaChallenge> emailMfaTemplate;
    @Mock
    private ValueOperations<String, EmailMfaChallenge> valueOperations;
    @InjectMocks
    private RegisterEmailMfaRedisService redisService;

    private static final String CHALLENGE_BY_ID_KEY_PREFIX = "mfa:challenge:registration:id:";
    private static final String CHALLENGE_BY_EMAIL_KEY_PREFIX = "mfa:challenge:registration:email:";
    private final UUID mockChallengeId = UUID.randomUUID();
    private final String mockEmail = "user@example.com";
    private final Duration TTL = Duration.ofMinutes(5);
    private EmailMfaChallenge mockChallenge;

    @BeforeEach
    void setup() {
        mockChallenge = new EmailMfaChallenge(mockChallengeId, mockEmail, "123456", LocalDate.now());
        when(emailMfaTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("[EmailMfaRedisService :: storeMfaEntry] - should return nothing if challenge is stored successfully into redis")
    public void shouldReturnNothingIfEntryStoredSuccessfully() {
        // arrange
        // act
        redisService.storeMfaEntry(mockChallenge, TTL);
        // assert
        verify(valueOperations).set(CHALLENGE_BY_ID_KEY_PREFIX + mockChallenge.id(), mockChallenge, TTL);
        verify(valueOperations).set(CHALLENGE_BY_EMAIL_KEY_PREFIX + mockChallenge.email(), mockChallenge, TTL);
    }

    @ParameterizedTest
    @MethodSource("invalidRedisExceptions")
    @DisplayName("[EmailMfaRedisService :: storeMfaEntry] - should throw [RedisException] if entry failed to store")
    public void shouldThrowRedisExceptionIfEntryFailedToStore(RuntimeException redisExceptions) {
        // arrange
        doThrow(redisExceptions).when(valueOperations).set(CHALLENGE_BY_ID_KEY_PREFIX + mockChallenge.id(), mockChallenge, TTL);
        // act
        // assert
        RedisException thrown = assertThrows(RedisException.class, () -> redisService.storeMfaEntry(mockChallenge, TTL));
        assertEquals("something went wrong with redis", thrown.getMessage());
    }

    static Stream<RuntimeException> invalidRedisExceptions() {
        return Stream.of(
                new RedisConnectionFailureException("redis down"),
                new SerializationException("serialization failed"),
                new IllegalArgumentException("invalid key")
        );
    }

    @Test
    @DisplayName("[EmailMfaRedisService :: findMfaById] - should return email mfa challenge if challenge id exist in redis")
    public void shouldReturnEntryIfIdExistInRedis() {
        // arrange
        when(valueOperations.get(CHALLENGE_BY_ID_KEY_PREFIX + mockChallengeId)).thenReturn(mockChallenge);
        // act
        Optional<EmailMfaChallenge> functionCall = redisService.findMfaById(mockChallengeId);
        // assert
        assertTrue(functionCall.isPresent());
        assertEquals(mockChallenge, functionCall.get());
    }

    @Test
    @DisplayName("[EmailMfaRedisService :: findMfaById] - should return empty if challenge id does not exist in db")
    public void shouldReturnEmptyIfIdDoesNotExistInRedis() {
        // arrange
        when(valueOperations.get(CHALLENGE_BY_ID_KEY_PREFIX + mockChallengeId)).thenReturn(null);
        // act
        Optional<EmailMfaChallenge> functionCall = redisService.findMfaById(mockChallengeId);
        // assert
        assertTrue(functionCall.isEmpty());
    }

    @Test
    @DisplayName("[EmailMfaRedisService :: findMfaByIdentity] - should return email mfa challenge if main identifier (email) exist in redis")
    public void shouldReturnEntryIfIdentifierExistInRedis() {
        // arrange
        when(valueOperations.get(CHALLENGE_BY_EMAIL_KEY_PREFIX + mockEmail)).thenReturn(mockChallenge);
        // act
        Optional<EmailMfaChallenge> functionCall = redisService.findMfaByIdentity(mockEmail);
        // assert
        assertTrue(functionCall.isPresent());
        assertEquals(mockChallenge, functionCall.get());
    }

    @Test
    @DisplayName("[EmailMfaRedisService :: findMfaByIdentity] - should return empty mfa challenge if main identifier (email) does not exist in redis")
    public void shouldReturnEmptyIfIdentifierDoesNotExistInRedis() {
        // arrange
        when(valueOperations.get(CHALLENGE_BY_EMAIL_KEY_PREFIX + mockEmail)).thenReturn(null);
        // act
        Optional<EmailMfaChallenge> functionCall = redisService.findMfaByIdentity(mockEmail);
        // assert
        assertTrue(functionCall.isEmpty());
    }
}
