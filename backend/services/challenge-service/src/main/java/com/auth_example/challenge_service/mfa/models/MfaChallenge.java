package com.auth_example.challenge_service.mfa.models;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "challenges")
@EntityListeners(AuditingEntityListener.class)
public class MfaChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID )
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private MfaChallengeType type;

    @Column(nullable = false)
    private String code;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @PrePersist
    public void prePersist() {
        this.updatedAt = LocalDate.now();
    }
}
