package com.auth_example.auth_service.jwt.models;

public record UserToken (
        String accessToken,
        String refreshToken
) {
}
