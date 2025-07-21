package com.auth_example.user_service.users;

import com.auth_example.user_service.users.models.CreateUserRequest;
import com.auth_example.user_service.users.models.User;
import com.auth_example.user_service.users.models.Mfa;
import com.auth_example.user_service.users.models.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    User createUserRequestToUser(CreateUserRequest request, Mfa mfa);

    @Mapping(target = "mfaEnabled", source = "mfa.enabled")
    UserResponse userToUserResponse(User user);
}
