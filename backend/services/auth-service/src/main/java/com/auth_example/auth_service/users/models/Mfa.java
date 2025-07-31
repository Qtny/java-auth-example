package com.auth_example.auth_service.users.models;

import com.auth_example.auth_service.mfa.MfaChallengeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class Mfa {
    private boolean isEnabled;
    private MfaChallengeType method;
    private String target;
    private LocalDate lastVerifiedDate;
}
