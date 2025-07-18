package com.auth_example.user_service.users.models;

import com.auth_example.user_service.users.MfaMethod;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class Mfa {
    private boolean isEnabled;

    private MfaMethod method;

    private String target;

    private LocalDate lastVerifiedDate;
}
