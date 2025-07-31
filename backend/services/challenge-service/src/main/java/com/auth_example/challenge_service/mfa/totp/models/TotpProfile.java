package com.auth_example.challenge_service.mfa.totp.models;

import com.auth_example.challenge_service.mfa.BaseMfaChallenge;
import com.auth_example.challenge_service.mfa.MfaChallengeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "totp-profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class TotpProfile implements BaseMfaChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String secret;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String qrCodeUrl;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    @Override
    public MfaChallengeType type() {
        return MfaChallengeType.TOTP;
    }

    @Override
    public LocalDate createdAt() {
        return this.createdAt;
    }
}
