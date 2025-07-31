package com.auth_example.auth_service.mfa.models;

import com.auth_example.auth_service.mfa.MfaChallengeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MfaChallenge {
    private UUID id ;
    private String email;
    private MfaChallengeType type;
    private String code;
    private LocalDate createdAt;
}
