package com.auth_example.auth_service.users;

import com.auth_example.auth_service.exceptions.ApiNotSuccessException;
import com.auth_example.auth_service.mfa.MfaChallengeType;
import com.auth_example.auth_service.users.models.*;
import com.auth_example.auth_service.users.models.api.CheckUserEmailExistPayload;
import com.auth_example.auth_service.users.models.api.CreateUserPayload;
import com.auth_example.auth_service.users.models.api.FindUserByEmailPayload;
import com.auth_example.auth_service.users.models.api.EnableMfaPayload;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.core.rest.BaseRestClient;
import com.auth_example.common_service.core.rest.RequestOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserClient {

    private static final String USER_BASE_URL = "http://localhost:8080/api/v1/users";
    private final BaseRestClient restClient;
    private final UserDtoMapperImpl mapper;

    public boolean checkIfEmailExist(String email) {
        CheckUserEmailExistPayload payload = new CheckUserEmailExistPayload(email);
        String uri = USER_BASE_URL + "/email/" + email + "/exist";
        ApiResponse<Boolean> response = restClient.get(uri, RequestOption.internalNone(), Boolean.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }

    public User findOneByEmail(String email) {
        FindUserByEmailPayload payload = new FindUserByEmailPayload(email);
        String uri = USER_BASE_URL + "/email/" + email + "/raw";
        ApiResponse<User> response = restClient.get(uri, RequestOption.internalNone(), User.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }

    public User create(NewUser user) {
        log.info("creating user");
        CreateUserPayload payload = mapper.userToCreateUserPayload(user);
        ApiResponse<User> response = restClient.post(USER_BASE_URL, payload, RequestOption.internalNone(), User.class);
        return response.getData();
    }

    public void enableMfa(String email, MfaChallengeType type, String target) {
        String uri = USER_BASE_URL + "/mfa/enable";
        EnableMfaPayload payload = new EnableMfaPayload(email, type, target);
        ApiResponse<Void> response = restClient.patch(uri, payload, RequestOption.internalNone(), Void.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }
    }
}
