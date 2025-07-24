package com.auth_example.auth_service.users.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUser {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Address address;
}
