package com.auth_example.gateway_service.jwt;

public enum TokenPurpose {
    VERIFY_REGISTRATION,
    VERIFY_MFA,
    AUTHORIZATION
}