package com.auth_example.challenge_service.mfa;

import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.mfa.models.MfaChallenge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MfaDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MfaChallenge createChallengeRequestToMfaChallenge(CreateChallengeRequest request, UUID userId, String code);
}
