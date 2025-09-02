package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.encryption.EncryptionService;
import com.auth_example.challenge_service.exceptions.CodeDoesNotMatchException;
import com.auth_example.challenge_service.exceptions.EncryptionException;
import com.auth_example.challenge_service.exceptions.ErrorGeneratingQRException;
import com.auth_example.challenge_service.exceptions.TotpProfileNotFoundException;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.MfaDtoMapperImpl;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import com.auth_example.challenge_service.mfa.totp.models.TotpCreateRequest;
import com.auth_example.challenge_service.mfa.totp.models.TotpMfaFindByEmailRequest;
import com.auth_example.challenge_service.mfa.totp.models.TotpProfile;
import com.auth_example.challenge_service.mfa.totp.models.TotpValidateRequest;
import com.auth_example.challenge_service.qrcode.QrCodeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.AssertionErrors;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;

@ExtendWith(MockitoExtension.class)
public class TotpMfaServiceTest {

    @Mock
    private TotpMfaRepository totpRepository;
    @Mock
    private QrCodeService qrCodeService;
    @Mock
    private MfaDtoMapperImpl mapper;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private TotpService totpService;
    @InjectMocks
    private TotpMfaService totpMfaService;

    private static final Duration TIME_STEP = Duration.ofSeconds(30);
    private static final String ISSUER = "auth_example";
    private static final UUID TOTP_ID = UUID.randomUUID();
    private static final String EMAIL = "test@example.com";
    private static final String QR_CODE_URL = "sample_url";
    private static final String OTP_CODE = "123456";
    private static final String PLAIN_TEXT_SECRET = "sample_plain_secret";
    private static final String ENCRYPTED_SECRET = "sample_encrypted_secret";
    private TotpProfile mockTotpProfile;

    @BeforeEach
    void setup() {
        mockTotpProfile = TotpProfile.builder()
                .id(TOTP_ID)
                .qrCodeUrl(QR_CODE_URL)
                .secret(ENCRYPTED_SECRET)
                .email(EMAIL)
                .createdAt(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("[TotpMfaService :: getType] - should return MfaChallengeType.TOTP")
    public void shouldReturnTotpMfaType() {
        // arrange
        // act
        MfaChallengeType type = totpMfaService.getType();
        // assert
        assertEquals(MfaChallengeType.TOTP, type);
    }

    @Test
    @DisplayName("[TotpMfaService :: create] - should return totp profile if totp challenge is created successfully")
    public void shouldReturnTotpProfileIfChallengeCreatedSuccessfully() {
        // arrange
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(mockCreateRequest.email());
        when(totpRepository.findOneByEmail(mockFindRequest.email())).thenReturn(Optional.empty());
        when(totpService.generateNewClientSecret(mockCreateRequest.email())).thenReturn(PLAIN_TEXT_SECRET);
        when(qrCodeService.generateForBase64(anyString())).thenReturn(QR_CODE_URL);
        when(mapper.createTotpRequestToTotpProfile(eq(mockCreateRequest), anyString(), eq(QR_CODE_URL))).thenReturn(mockTotpProfile);
        when(encryptionService.encryptString(anyString())).thenReturn(ENCRYPTED_SECRET);
        when(totpRepository.save(any())).thenReturn(mockTotpProfile);
        // act
        TotpProfile functionCall = totpMfaService.create(mockCreateRequest);
        // assert
        assertEquals(functionCall.getEmail(), mockTotpProfile.getEmail());
        assertEquals(functionCall.getSecret(), mockTotpProfile.getSecret());
        assertEquals(functionCall.getQrCodeUrl(), mockTotpProfile.getQrCodeUrl());
        verify(qrCodeService).generateForBase64(anyString());
        verify(encryptionService).encryptString(anyString());
        verify(totpRepository).save(any());
    }

    @Test
    @DisplayName("[TotpMfaService :: create] - should retrieve and return totp profile if totp challenge already exist in database")
    public void shouldReturnTotpProfileIfChallengeExistInDatabase() {
        // arrange
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(mockCreateRequest.email());
        when(totpRepository.findOneByEmail(mockFindRequest.email())).thenReturn(Optional.ofNullable(mockTotpProfile));
        // act
        TotpProfile functionCall = totpMfaService.create(mockCreateRequest);
        // assert
        assertEquals(functionCall.getEmail(), mockTotpProfile.getEmail());
        assertEquals(functionCall.getSecret(), mockTotpProfile.getSecret());
        assertEquals(functionCall.getQrCodeUrl(), mockTotpProfile.getQrCodeUrl());
        verify(qrCodeService, never()).generateForBase64(anyString());
        verify(encryptionService, never()).encryptString(anyString());
        verify(totpRepository, never()).save(any());
    }

    @Test
    @DisplayName("[TotpMfaService :: create] - should throw [ErrorGeneratingQrCodeException] if error occur when generating qr code")
    public void shouldThrowErrorGeneratingQrCodeExceptionIfFailedToGenerateQRCode() {
        // arrange
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(mockCreateRequest.email());
        when(totpRepository.findOneByEmail(mockFindRequest.email())).thenReturn(Optional.empty());
        when(totpService.generateNewClientSecret(mockCreateRequest.email())).thenReturn(PLAIN_TEXT_SECRET);
        when(qrCodeService.generateForBase64(anyString())).thenThrow(new ErrorGeneratingQRException("simulate error generating qr code"));
        // act assert
        assertThrows(ErrorGeneratingQRException.class, () -> totpMfaService.create(mockCreateRequest));
        verify(qrCodeService).generateForBase64(anyString());
        verify(encryptionService, never()).encryptString(anyString());
        verify(totpRepository, never()).save(any());
    }

    @Test
    @DisplayName("[TotpMfaService :: create] - should throw [EncryptionException] if error occur when encrypting client secret")
    public void shouldThrowEncryptionExceptionIfFailedToEncryptSecret() {
        // arrange
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(mockCreateRequest.email());
        when(totpRepository.findOneByEmail(mockFindRequest.email())).thenReturn(Optional.empty());
        when(totpService.generateNewClientSecret(mockCreateRequest.email())).thenReturn(PLAIN_TEXT_SECRET);
        when(qrCodeService.generateForBase64(anyString())).thenReturn(QR_CODE_URL);
        when(mapper.createTotpRequestToTotpProfile(eq(mockCreateRequest), anyString(), eq(QR_CODE_URL))).thenReturn(mockTotpProfile);
        when(encryptionService.encryptString(anyString())).thenThrow(new EncryptionException("simulate error during encryption"));
        // act assert
        assertThrows(EncryptionException.class, () -> totpMfaService.create(mockCreateRequest));
        verify(qrCodeService).generateForBase64(anyString());
        verify(encryptionService).encryptString(anyString());
        verify(totpRepository, never()).save(any());
    }

    @Test
    @DisplayName("[TotpMfaService :: validate] - should return totp profile if totp challenge is validated successfully")
    public void shouldReturnTotpProfileIfChallengeValidatedSuccessfully() {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(UUID.randomUUID(), EMAIL, OTP_CODE);
        when(totpRepository.findOneByEmail(mockValidateRequest.email())).thenReturn(Optional.ofNullable(mockTotpProfile));
        when(encryptionService.decryptString(mockTotpProfile.getSecret())).thenReturn(PLAIN_TEXT_SECRET);
        when(totpService.verifyWithSkew(PLAIN_TEXT_SECRET, OTP_CODE, 2)).thenReturn(true);
        // act
        TotpProfile functionCall = totpMfaService.validate(mockValidateRequest);
        // assert
        assertEquals(functionCall, mockTotpProfile);
        verify(totpRepository).findOneByEmail(mockValidateRequest.email());
        verify(encryptionService).decryptString(anyString());
        verify(totpService).verifyWithSkew(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("[TotpMfaService :: validate] - should throw [TotpProfileNotFoundException] if totp profile with email does not exist in database")
    public void shouldThrowTotpProfileNotFoundExceptionIfProfileNotFoundInDatabaseForValidate() {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(UUID.randomUUID(), EMAIL, OTP_CODE);
        when(totpRepository.findOneByEmail(anyString())).thenThrow(new TotpProfileNotFoundException("simulate totp profile missing"));
        // act
        // assert
        assertThrows(TotpProfileNotFoundException.class, () -> totpMfaService.validate(mockValidateRequest));
        verify(totpRepository).findOneByEmail(mockValidateRequest.email());
        verify(encryptionService, never()).decryptString(anyString());
        verify(totpService, never()).verifyWithSkew(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("[TotpMfaService :: validate] - should throw [EncryptionException] if error occur when decrypting client secret")
    public void shouldThrowEncryptionExceptionIfFailedToDecryptSecret() {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(UUID.randomUUID(), EMAIL, OTP_CODE);
        when(totpRepository.findOneByEmail(mockValidateRequest.email())).thenReturn(Optional.ofNullable(mockTotpProfile));
        when(encryptionService.decryptString(mockTotpProfile.getSecret())).thenThrow(new EncryptionException("simulate encryption error"));
        // act
        // assert
        assertThrows(EncryptionException.class, () -> totpMfaService.validate(mockValidateRequest));
        verify(totpRepository).findOneByEmail(mockValidateRequest.email());
        verify(encryptionService).decryptString(anyString());
        verify(totpService, never()).verifyWithSkew(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("[TotpMfaService :: validate] - should throw [CodeDoesNotMatchException] if code given does not match with totp generated code")
    public void shouldThrowCodeDoesNotMatchExceptionIfCodeMismatchForTotp() {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(UUID.randomUUID(), EMAIL, OTP_CODE);
        when(totpRepository.findOneByEmail(mockValidateRequest.email())).thenReturn(Optional.ofNullable(mockTotpProfile));
        when(encryptionService.decryptString(mockTotpProfile.getSecret())).thenReturn(PLAIN_TEXT_SECRET);
        when(totpService.verifyWithSkew(PLAIN_TEXT_SECRET, OTP_CODE, 2)).thenReturn(false);
        // act
        // assert
        assertThrows(CodeDoesNotMatchException.class, () -> totpMfaService.validate(mockValidateRequest));
        verify(totpRepository).findOneByEmail(mockValidateRequest.email());
        verify(encryptionService).decryptString(anyString());
        verify(totpService).verifyWithSkew(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("[TotpMfaService :: findOneById] - should return totp profile if profile with id exist in database")
    public void shouldReturnTotpProfileIfProfileWithIdExistInDatabaseForFindOneById() {
        // arrange
        when(totpRepository.findById(TOTP_ID)).thenReturn(Optional.ofNullable(mockTotpProfile));
        // act
        TotpProfile functionCall = totpMfaService.findOneById(TOTP_ID);
        // assert
        assertEquals(mockTotpProfile, functionCall);
        verify(totpRepository).findById(TOTP_ID);
    }

    @Test
    @DisplayName("[TotpMfaService :: findOneById] - should throw [TotpProfileNotFoundException] if profile with id does not exist in database")
    public void shouldThrowTotpProfileNotFoundExceptionIfIdDoesNotExistInDatabaseForFindOneById() {
        // arrange
        when(totpRepository.findById(TOTP_ID)).thenReturn(Optional.empty());
        // act
        // assert
        assertThrows(TotpProfileNotFoundException.class, () -> totpMfaService.findOneById(TOTP_ID));
        verify(totpRepository).findById(any());
    }

    @Test
    @DisplayName("[TotpMfaService :: findOneOrThrow] - should return totp profile if profile with email exist in database")
    public void shouldReturnTotpProfileIfProfileWithEmailExistInDatabaseForFindOneOrThrow() {
        // arrange
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(EMAIL);
        when(totpRepository.findOneByEmail(mockFindRequest.email())).thenReturn(Optional.ofNullable(mockTotpProfile));
        // act
        TotpProfile functionCall = totpMfaService.findOneOrThrow(mockFindRequest);
        // assert
        assertEquals(mockTotpProfile, functionCall);
        verify(totpRepository).findOneByEmail(mockFindRequest.email());
    }

    @Test
    @DisplayName("[TotpMfaService :: findOneOrThrow] - should throw [TotpProfileNotFoundException] if profile with email does not exist in database")
    public void shouldThrowTotpProfileNotFoundExceptionIfEmailDoesNotExistInDatabaseForFindOneOrThrow() {
        // arrange
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(EMAIL);
        when(totpRepository.findOneByEmail(anyString())).thenReturn(Optional.empty());
        // act
        // assert
        assertThrows(TotpProfileNotFoundException.class, () -> totpMfaService.findOneOrThrow(mockFindRequest));
        verify(totpRepository).findOneByEmail(anyString());
    }

    @Test
    @DisplayName("[TotpMfaService :: findOne] - should return totp profile if profile with email exist")
    public void shouldReturnTotpProfileIfProfileExistInDatabaseForFindOne() {
        // arrange
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(EMAIL);
        when(totpRepository.findOneByEmail(mockFindRequest.email())).thenReturn(Optional.ofNullable(mockTotpProfile));
        // act
        Optional<TotpProfile> functionCall = totpMfaService.findOne(mockFindRequest);
        // assert
        assertTrue(functionCall.isPresent());
        verify(totpRepository).findOneByEmail(mockFindRequest.email());
    }

    @Test
    @DisplayName("[TotpMfaService :: findOne] - should return nothing if profile with email does exist")
    public void shouldReturnNothingIfProfileDoesNotExistInDatabaseForFindOne() {
        // arrange
        TotpMfaFindByEmailRequest mockFindRequest = new TotpMfaFindByEmailRequest(EMAIL);
        when(totpRepository.findOneByEmail(mockFindRequest.email())).thenReturn(Optional.empty());
        // act
        Optional<TotpProfile> functionCall = totpMfaService.findOne(mockFindRequest);
        // assert
        assertTrue(functionCall.isEmpty());
        verify(totpRepository).findOneByEmail(mockFindRequest.email());
    }
}
