package com.auth_example.auth_service.mfa;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.mfa.models.email.CreateEmailMfaPayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MfaDtoMapper {

    CreateEmailMfaPayload registerRequestToCreateMfaPayload(RegisterRequest request);
}
