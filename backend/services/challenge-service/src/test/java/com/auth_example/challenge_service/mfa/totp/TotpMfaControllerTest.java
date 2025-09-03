package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.encryption.EncryptionService;
import com.auth_example.challenge_service.exceptions.*;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.email.EmailService;
import com.auth_example.challenge_service.mfa.totp.models.TotpCreateRequest;
import com.auth_example.challenge_service.mfa.totp.models.TotpProfile;
import com.auth_example.challenge_service.mfa.totp.models.TotpValidateRequest;
import com.auth_example.common_service.core.responses.ApiErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.UUID;

import static com.auth_example.common_service.core.responses.ApiErrorCode.*;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.util.AssertionErrors.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TotpMfaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TotpMfaService totpMfaService;
    @MockitoBean
    private EncryptionService encryptionService;
    @MockitoBean
    private EmailService emailService;

    private final ObjectMapper om = new ObjectMapper();
    private final UUID TOTP_ID = UUID.randomUUID();
    private final String EMAIL = "test@example.com";
    private final String OTP_CODE = "123456";
    private TotpProfile mockTotpProfile;

    private RequestPostProcessor internalUserHeader() {
        return request -> {
                request.addHeader("X-Internal-Use", "gateway");
                return request;
        };
    }

    @BeforeEach
    void setup() {
        String ENCRYPTED_SECRET = "encrypted_test_secret";
        String QR_CODE_URL = "test_qr_code_url";
        mockTotpProfile = TotpProfile.builder()
                .id(TOTP_ID)
                .email(EMAIL)
                .secret(ENCRYPTED_SECRET)
                .qrCodeUrl(QR_CODE_URL)
                .createdAt(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp] - should return status 403 for external API call")
    void shouldReturnForbiddenIfNotInternal() throws Exception {
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        mockMvc.perform(post("/api/v1/mfa/totp")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateRequest))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp] - should return 200 with decrypted totp client secret and qr code url")
    public void shouldReturnDecryptSecretAndQrCodeUrlIfChallengeCreationSuccessful() throws Exception {
        // arrange
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        when(totpMfaService.create(mockCreateRequest)).thenReturn(mockTotpProfile);
        String DECRYPTED_SECRET = "decrypted_test_secret";
        when(encryptionService.decryptString(anyString())).thenReturn(DECRYPTED_SECRET);
        doNothing().when(emailService).sendTotpQrCode(anyString(), anyString());
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(nullValue()))
                .andExpect(jsonPath("$.data.secret").value(DECRYPTED_SECRET))
                .andExpect(jsonPath("$.data.qrCodeUrl").value(mockTotpProfile.getQrCodeUrl()))
        ;
        verify(totpMfaService).create(mockCreateRequest);
        verify(encryptionService).decryptString(anyString());
        verify(emailService).sendTotpQrCode(anyString(), anyString());
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp] - should throw 500 with [ASSET_GENERATION_ERROR] when qr code failed to generate")
    public void shouldThrowInternalErrorIfQrCodeGenerationFailed() throws Exception {
        // arrange
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        when(totpMfaService.create(mockCreateRequest)).thenThrow(new ErrorGeneratingQRException("simulate error generating qr code"));
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateRequest))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(ASSET_GENERATION_ERROR.name()))
        ;
        verify(totpMfaService).create(mockCreateRequest);
        verify(encryptionService, never()).decryptString(anyString());
        verify(emailService, never()).sendTotpQrCode(anyString(), anyString());
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp] - should throw 500 with [ASSET_GENERATION_ERROR] when failed to encrypt client secret")
    public void shouldAssetGenerationErrorIfEncryptionFailed() throws Exception {
        // arrange
        TotpCreateRequest mockCreateRequest = new TotpCreateRequest(EMAIL);
        when(totpMfaService.create(mockCreateRequest)).thenThrow(new EncryptionException("simulate encryption error"));
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockCreateRequest))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(INTERNAL_ERROR.name()))
        ;
        verify(totpMfaService).create(mockCreateRequest);
        verify(encryptionService, never()).decryptString(anyString());
        verify(emailService, never()).sendTotpQrCode(anyString(), anyString());
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp/verify] - should return 200 with user email and mfa type (totp)")
    public void shouldReturnUserEmailAndMfaTypeIfValidateSuccessful() throws Exception {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(TOTP_ID, EMAIL, OTP_CODE);
        when(totpMfaService.validate(mockValidateRequest)).thenReturn(mockTotpProfile);
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp/verify")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").value(nullValue()))
                .andExpect(jsonPath("$.data.email").value(mockTotpProfile.getEmail()))
                .andExpect(jsonPath("$.data.type").value(MfaChallengeType.TOTP.name()))
        ;
        verify(totpMfaService).validate(mockValidateRequest);
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp/verify] - should throw 404 with [ENTITY_NOT_FOUND] when failed to retrieve totp profile")
    public void shouldThrowEntityNotFoundIfTotpProfileMissingForVerify() throws Exception {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(TOTP_ID, EMAIL, OTP_CODE);
        when(totpMfaService.validate(mockValidateRequest)).thenThrow(new TotpProfileNotFoundException("simulate missing totp profile"));
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp/verify")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(ENTITY_NOT_FOUND.name()))
        ;
        verify(totpMfaService).validate(mockValidateRequest);
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp/verify] - should throw 404 with [INTERNAL_ERROR] when validation skew error occurs")
    public void shouldThrowInternalErrorIfSkewIsInvalid() throws Exception {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(TOTP_ID, EMAIL, OTP_CODE);
        when(totpMfaService.validate(mockValidateRequest)).thenThrow(new InvalidSkewException("simulate missing totp profile"));
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp/verify")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(INTERNAL_ERROR.name()))
        ;
        verify(totpMfaService).validate(mockValidateRequest);
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp/verify] - should throw 500 with [INTERNAL_ERROR] when client secret failed to decrypt client secret")
    public void shouldThrowInternalErrorIfDecryptionFailed() throws Exception {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(TOTP_ID, EMAIL, OTP_CODE);
        when(totpMfaService.validate(mockValidateRequest)).thenThrow(new EncryptionException("simulate decryption failed"));
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp/verify")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(INTERNAL_ERROR.name()))
        ;
        verify(totpMfaService).validate(mockValidateRequest);
    }

    @Test
    @DisplayName("POST [/api/v1/mfa/totp/verify] - should throw 401 with [MFA_CODE_INCORRECT] when code requested does not match with generated code")
    public void shouldThrowMfaCodeIncorrectIfCodeIncorrect() throws Exception {
        // arrange
        TotpValidateRequest mockValidateRequest = new TotpValidateRequest(TOTP_ID, EMAIL, OTP_CODE);
        when(totpMfaService.validate(mockValidateRequest)).thenThrow(new CodeDoesNotMatchException("simulate code mismatch"));
        // act
        // assert
        mockMvc.perform(post("/api/v1/mfa/totp/verify")
                        .with(internalUserHeader())
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(mockValidateRequest))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()))
                .andExpect(jsonPath("$.error.code").value(MFA_CODE_INCORRECT.name()))
        ;
        verify(totpMfaService).validate(mockValidateRequest);
    }
}
