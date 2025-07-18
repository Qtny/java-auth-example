package com.auth_example.user_service.users;

import com.auth_example.user_service.common.ApiResponse;
import com.auth_example.user_service.users.models.CreateMfaRequest;
import com.auth_example.user_service.users.models.CreateUserRequest;
import com.auth_example.user_service.users.models.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> findAll() {
        return ResponseEntity.ok(ApiResponse.success(userService.findAll()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> findOneById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.findOneById(userId)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> findOneByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(ApiResponse.success(userService.findOneByEmail(email)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.create(request)));
    }

    @PatchMapping("{userId}/mfa")
    public ResponseEntity<ApiResponse<User>> enableMfa(@PathVariable("userId") Long userId, @RequestBody @Valid CreateMfaRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.enableMfa(userId, request)));
    }
}
