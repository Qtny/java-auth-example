package com.auth_example.user_service.users;

import com.auth_example.user_service.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findOneById(UUID userId);

    Optional<User> findOneByEmail(String email);
}
