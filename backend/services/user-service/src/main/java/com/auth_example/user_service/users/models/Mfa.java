package com.auth_example.user_service.users.models;

import com.auth_example.user_service.users.MfaMethod;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private MfaMethod method;

    private String target;

    private LocalDate lastVerifiedDate;
}
