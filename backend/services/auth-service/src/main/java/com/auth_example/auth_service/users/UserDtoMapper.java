package com.auth_example.auth_service.users;

import com.auth_example.auth_service.users.models.CreateUserPayload;
import com.auth_example.auth_service.users.models.NewUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    CreateUserPayload userToCreateUserPayload(NewUser user);
}
