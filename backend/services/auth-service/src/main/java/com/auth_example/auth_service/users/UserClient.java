package com.auth_example.auth_service.users;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.exceptions.ApiNotSuccessException;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.core.rest.BaseRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserClient {

    private static final String USER_BASE_URL = "http://localhost:8080/api/v1/users";
    private final BaseRestClient restClient;

//    public ApiResponse<Boolean> createNewUser(RegisterRequest request) {
//        return restClient.post(USER_BASE_URL, request, Boolean.class);
//    }

    public boolean checkIfEmailExist(String email) {
        CheckUserEmailExistPayload payload = new CheckUserEmailExistPayload(email);
        String uri = USER_BASE_URL + "/email/" + email;
        ApiResponse<Boolean> response = restClient.get(uri, Boolean.class);
        if (!response.isSuccess()) {
            throw new ApiNotSuccessException("Api error");
        }

        return response.getData();
    }
}
