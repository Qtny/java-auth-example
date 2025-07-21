package com.auth_example.auth_service.users;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final RestClient restClient = RestClient.create();
    private static final String USER_BASE_URL = "http://localhost:8080/api/v1/users";

    public void createNewUser(RegisterRequest request) {
        restClient.post()
                .uri(USER_BASE_URL)
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    };
}
