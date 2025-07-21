package com.auth_example.auth_service.auth.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class Address {

    @NotBlank(message = "street 1 is required")
    private String street1;
    @NotNull(message = "street 2 is required")
    private String street2;
    @NotBlank(message = "city is required")
    private String city;
    @NotBlank(message = "postal code is required")
    private String postalCode;
    @NotBlank(message = "state is required")
    private String state;
    @NotBlank(message = "country is required")
    private String country;
}
