package com.auth_example.challenge_service.mfa.totp;

import com.auth_example.challenge_service.mfa.MfaChallengeType;
import com.auth_example.challenge_service.mfa.totp.models.TotpProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TotpModelTest {
    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private ObjectMapper objectMapper;

    @Test
    public void prePersist_shouldSetCreatedAt() {
        // given
        TotpProfile totpProfile = TotpProfile.builder()
                .email("test@example.com")
                .secret("test_secret")
                .qrCodeUrl("test_qr_url")
                .build();
        // act
        entityManager.persist(totpProfile);
        entityManager.flush();
        // assert
        assertNotNull(totpProfile.getId());
        assertNotNull(totpProfile.createdAt());
        assertEquals(0, ChronoUnit.DAYS.between(totpProfile.getCreatedAt(), LocalDate.now()));
        assertEquals(MfaChallengeType.TOTP, totpProfile.type());
    }
}
