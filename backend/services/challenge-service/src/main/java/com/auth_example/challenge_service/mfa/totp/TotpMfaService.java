package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.encryption.EncryptionService;
import com.auth_example.challenge_service.exceptions.ChallengeNotFoundException;
import com.auth_example.challenge_service.exceptions.CodeDoesNotMatchException;
import com.auth_example.challenge_service.exceptions.TotpProfileNotFoundException;
import com.auth_example.challenge_service.mfa.BaseMfaService;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.MfaDtoMapperImpl;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import com.auth_example.challenge_service.mfa.totp.models.*;
import com.auth_example.challenge_service.qrcode.QrCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TotpMfaService implements BaseMfaService<
        TotpCreateRequest,
        TotpValidateRequest,
        TotpMfaFindByEmailRequest
        > {

    private final TotpMfaRepository totpRepository;
    private final QrCodeService qrCodeService;
    private final MfaDtoMapperImpl mapper;
    private final TotpService totpService;
    private final EncryptionService encryptionService;

    private static final Duration TIME_STEP = Duration.ofSeconds(30);
    private static final String ISSUER = "auth_example";

    @Override
    public MfaChallengeType getType() {
        return MfaChallengeType.TOTP;
    }

    @Override
    public TotpProfile create(TotpCreateRequest request) {
        // check if user already has a totp profile
        TotpMfaFindByEmailRequest findRequest = new TotpMfaFindByEmailRequest(request.email());
        Optional<TotpProfile> existingProfile = this.findOne(findRequest);
        if (existingProfile.isPresent()) {
            return existingProfile.get();
        }

        // generate secret
        String clientSecret = totpService.generateNewClientSecret(request.email());
        // generate auth url
        String authUrl = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", ISSUER, request.email(), clientSecret, ISSUER);
        // generate qr
        String qrCode = qrCodeService.generateForBase64(authUrl);

        // create totp profile
        TotpProfile profile = mapper.createTotpRequestToTotpProfile(request, clientSecret, qrCode);
        // encrypt secret for the one storing in db
        String encryptedSecret = encryptionService.encryptString(clientSecret);
        profile.setSecret(encryptedSecret);
        return totpRepository.save(profile);
    }

    @Override
    public TotpProfile validate(TotpValidateRequest request) {
        // retrieve client totp profile
        TotpMfaFindByEmailRequest findRequest = new TotpMfaFindByEmailRequest(request.email());
        TotpProfile profile = this.findOneOrThrow(findRequest);

        // decrypt secret
        String decryptedSecret = encryptionService.decryptString(profile.getSecret());

        boolean isValid = totpService.verifyWithSkew(decryptedSecret, request.code(), 2);
        if (!isValid) {
            throw new CodeDoesNotMatchException("totp code does not match");
        }

        return profile;
    }

    @Override
    public TotpProfile findOneById(UUID totpId) {
        return totpRepository.findById(totpId)
                .orElseThrow(() ->
                        new TotpProfileNotFoundException("totp with id " + totpId + " does not exist")
                );
    }

    @Override
    public TotpProfile findOneOrThrow(TotpMfaFindByEmailRequest request) {
        return totpRepository.findOneByEmail(request.email())
                .orElseThrow(() -> new TotpProfileNotFoundException("totp of email " + request.email() + " does not exist"));
    }

    @Override
    public Optional<TotpProfile> findOne(TotpMfaFindByEmailRequest request) {
        return totpRepository.findOneByEmail(request.email());
    }
}

// login ->
// if mfa not setup, return { success: false, error: null, data: { code: "MFA_NOT_ENABLED", message: "", token: "1231231231231" } }
// if mfa is set up, return { success: true, error: null, data: { type: "email", token: "123123123123123", challengeId: "dasdasdasdasdasd" } }
// if mfa is set up, return { success: true, error: null, data: { type: "sms", token: "123123123123123", challengeId: "dasdasdasdasdasd" } }
// if mfa is set up, return { success: true, error: null, data: { type: "totp", token: "123123123123123" } }