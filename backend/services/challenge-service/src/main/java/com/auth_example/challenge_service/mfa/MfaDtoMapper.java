package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.email.EmailCreateRequest;
import com.auth_example.challenge_service.mfa.email.EmailMfaChallenge;
import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MfaDtoMapper {
    MfaChallenge createChallengeRequestToMfaChallenge(CreateChallengeRequest request, String code);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDate.now())")
    EmailMfaChallenge createChallengeRequestToMfaChallenge(EmailCreateRequest request, String code);
}
