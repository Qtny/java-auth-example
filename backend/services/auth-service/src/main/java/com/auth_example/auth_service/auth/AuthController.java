package com.auth_example.auth_service.auth;

import com.auth_example.auth_service.auth.models.RegisterRequest;
import com.auth_example.auth_service.common.ApiResponse;
import com.auth_example.auth_service.users.UserClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserClient userClient;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest request) {
        // create user on user service
        userClient.createNewUser(request);

        // create mfa challenge of type email

        // return challenge id
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
