package com.auth_example.user_service.users;

import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.user_service.users.models.CreateMfaRequest;
import com.auth_example.user_service.users.models.CreateUserRequest;
import com.auth_example.user_service.users.models.User;
import com.auth_example.user_service.users.models.UserResponse;
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

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> findOneById(@PathVariable("userId") Long userId) {
        // fetch user from db
        User user = userService.findOneById(userId);
        // sanitize user
        UserResponse sanitizedUser = userService.sanitizeUser(user);

        return ResponseEntity.ok(ApiResponse.success(sanitizedUser));
    }

    @GetMapping("/{userId}/raw")
    public ResponseEntity<ApiResponse<User>> findOneUnsanitizedById(@PathVariable("userId") Long userId) {
        // fetch user from db
        User user = userService.findOneById(userId);

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/email/{email}")
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

    @PatchMapping("{userId}/mfa")
    public ResponseEntity<ApiResponse<User>> enableMfa(@PathVariable("userId") Long userId, @RequestBody @Valid CreateMfaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.enableMfa(userId, request)));
    }
}
