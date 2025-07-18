package com.auth_example.user_service.users;

import com.auth_example.user_service.users.models.CreateUserRequest;
import com.auth_example.user_service.users.models.User;
import com.auth_example.user_service.users.models.Mfa;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    User createUserRequestToUser(CreateUserRequest request, Mfa mfa);
}
