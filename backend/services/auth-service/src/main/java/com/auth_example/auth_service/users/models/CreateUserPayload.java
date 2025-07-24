package com.auth_example.auth_service.users.models;

import java.util.UUID;

public class CreateUserPayload {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Address address;
}
