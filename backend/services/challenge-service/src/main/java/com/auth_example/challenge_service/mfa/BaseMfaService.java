package com.auth_example.challenge_service.mfa;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

public interface BaseMfaService<
        A extends BaseMfaCreateRequest,
        B extends BaseMfaValidateRequest,
        C extends BaseMfaFindRequest
        > {

    MfaChallengeType getType();
    BaseMfaChallenge create(A request) throws NoSuchAlgorithmException, IOException, WriterException;
    BaseMfaChallenge validate(B request);
    BaseMfaChallenge findOneById(UUID challengeId);
    BaseMfaChallenge findOneOrThrow(C request);
    Optional<? extends BaseMfaChallenge> findOne(C request);
}
