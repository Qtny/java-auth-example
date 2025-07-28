package com.auth_example.auth_service.users;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.users.models.api.CreateUserPayload;
import com.auth_example.auth_service.users.models.NewUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    NewUser registerRequestToNewUser(RegisterRequest request);
    CreateUserPayload userToCreateUserPayload(NewUser user);
}
