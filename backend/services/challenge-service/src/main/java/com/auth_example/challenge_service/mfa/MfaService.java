package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import com.auth_example.challenge_service.otp.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

    private final OtpService otpService;
    private final MfaDtoMapperImpl mapper;
    private final MfaRepository mfaRepository;

    public MfaChallenge create(@Valid CreateChallengeRequest request) {
        // generate uuid for temporary user
        log.info("generating temporary uuid for user");
        UUID userTempId = UUID.randomUUID();

        // generate code
        String code = otpService.generateOtp(6);

        log.info("INFO :: creating challenge");
        MfaChallenge challenge = mapper.createChallengeRequestToMfaChallenge(request, userTempId, code);
        return mfaRepository.save(challenge);
    }
}
