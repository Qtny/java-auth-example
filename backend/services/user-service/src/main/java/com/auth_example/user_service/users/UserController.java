package com.auth_example.user_service.users;

import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.user_service.users.models.api.CreateUserRequest;
import com.auth_example.user_service.users.models.User;
import com.auth_example.user_service.users.models.UserResponse;
import com.auth_example.user_service.users.models.api.UpdateMfaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        // fetch all users from db
        List<User> users = userService.findAll();
        // sanitize the users
        List<UserResponse> userResponses = users
                .stream()
                .map(userService::sanitizeUser)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(userResponses));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> findOneById(@PathVariable("email") String email) {
        // fetch user from db
        User user = userService.findOneByEmail(email);
        // sanitize user
        UserResponse sanitizedUser = userService.sanitizeUser(user);

        return ResponseEntity.ok(ApiResponse.success(sanitizedUser));
    }

    @GetMapping("/email/{email}/raw")
    public ResponseEntity<ApiResponse<User>> findOneUnsanitizedById(@PathVariable("email") String email) {
        // fetch user from db
        User user = userService.findOneByEmail(email);

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/email/{email}/exist")
    public ResponseEntity<ApiResponse<Boolean>> findOneByEmail(@PathVariable("email") String email) {
        log.info("INFO :: checking if email exist");
        return ResponseEntity.ok(ApiResponse.success(userService.emailExist(email)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody @Valid CreateUserRequest request) {
        // create new user
        User newUser = userService.create(request);
        // sanitize output
        UserResponse sanitizedUser = userService.sanitizeUser(newUser);

        return ResponseEntity.ok(ApiResponse.success(sanitizedUser));
    }

    @PatchMapping("/mfa/enable")
    public ResponseEntity<ApiResponse<Void>> updateMfa(@RequestBody @Valid UpdateMfaRequest request) {
        // update user mfa
        userService.enableMfa(request.email());

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
