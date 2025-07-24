package com.auth_example.auth_service.users.models;

import com.auth_example.auth_service.users.UserRole;
import com.auth_example.auth_service.users.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private String email;
    private String password;
    private Mfa mfa;
    private Address address;
    private UserRole role;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
