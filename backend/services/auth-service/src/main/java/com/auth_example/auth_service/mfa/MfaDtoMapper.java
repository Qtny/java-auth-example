package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MfaDtoMapper {

    CreateMfaPayload registerRequestToCreateMfaPayload(RegisterRequest request, MfaChallengeType type);
}
