package com.auth_example.user_service.users;

import com.auth_example.user_service.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneById(Long userId);

    Optional<User> findOneByEmail(String email);
}
