package com.auth_example.auth_service.jwt;

import com.auth_example.auth_service.jwt.models.UserPrincipal;
import com.auth_example.auth_service.users.UserService;
import com.auth_example.auth_service.users.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findOneByEmail(username);
        return new UserPrincipal(user);
    }
}
