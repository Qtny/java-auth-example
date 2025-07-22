package com.auth_example.challenge_service.user.models;

import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntry {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Embedded
    private Address address;
}
