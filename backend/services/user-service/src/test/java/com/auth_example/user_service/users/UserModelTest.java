package com.auth_example.user_service.users;

import com.auth_example.user_service.users.models.Address;
import com.auth_example.user_service.users.models.Mfa;
import com.auth_example.user_service.users.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserModelTest {
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void prePersist_shouldSetUpdatedAt() {
        // given
        Mfa mockMfa = new Mfa(false, null, "", null);
        Address mockAddress = new Address("test_street_1", "test_street_2", "test_city", "test_postcode", "test_state", "test_country");
        User mockUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("test@example.com")
                .password("test_password")
                .mfa(mockMfa)
                .address(mockAddress)
                .build();
        // when
        entityManager.persist(mockUser);
        entityManager.flush();
        // assert
        assertNotNull(mockUser.getUpdatedAt());
        assertEquals(0, ChronoUnit.DAYS.between(mockUser.getUpdatedAt(), LocalDate.now()));
    }
}
