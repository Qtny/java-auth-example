package com.auth_example.challenge_service.mfa;

import java.util.Optional;
import java.util.UUID;

public interface BaseMfaService<
        A extends BaseMfaCreateRequest,
        B extends BaseMfaValidateRequest,
        C extends BaseMfaFindRequest
        > {

    MfaChallengeType getType();
    BaseMfaChallenge create(A request);
    BaseMfaChallenge validate(B request);
    BaseMfaChallenge findOneById(UUID challengeId);
    BaseMfaChallenge findOneOrThrow(C request);
    Optional<? extends BaseMfaChallenge> findOne(C request);
}
