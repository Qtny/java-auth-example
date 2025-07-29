package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.email.models.EmailCreateRequest;
import com.auth_example.challenge_service.mfa.email.models.EmailMfaChallenge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MfaDtoMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDate.now())")
    EmailMfaChallenge createChallengeRequestToMfaChallenge(EmailCreateRequest request, String code);
}
