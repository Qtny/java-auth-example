package com.auth_example.challenge_service.user;

import com.auth_example.challenge_service.mfa.models.CreateChallengeRequest;
import com.auth_example.challenge_service.user.models.UserEntry;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserEntry createChallengeRequestToUserEntry(CreateChallengeRequest request, UUID userId);
}
