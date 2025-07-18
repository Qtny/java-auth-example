package com.auth_example.user_service.users.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Address {
    private String street1;
    private String street2;
    private String city;
    private String postalCode;
    private String state;
    private String country;
}
