package com.auth_example.challenge_service.mfa.totp;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Clock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TotpServiceTest {
    @Spy
    @InjectMocks
    private TotpService totpService;

    private final static String CORRECT_CODE = "123456";
    private final static String CLIENT_SECRET = "JBSWY3DPEHPK3PXP";
    private final static String CLIENT_CODE = "123456";
    private final static int SKEW = 2;

    @Test
    @DisplayName("[TotpService - generateClientSecret] - should return a random Base32 string")
    public void shouldReturnRandomBase32String() {
        // act
        String functionCall = totpService.generateNewClientSecret("EMAIL");
        // assert
        assertNotNull(functionCall);
        assertEquals(16, functionCall.length());
        assertTrue(functionCall.matches("[A-Z2-7]+"));
    }

    @Test
    @DisplayName("[TotpService :: verifyWithSkew] - should return true if code generated is within timeframe")
    public void shouldReturnTrueIfWithinTimeframe() {
        // arrange
        Totp mockTotp = new Totp(CLIENT_SECRET);
        String mockCode = mockTotp.now();
        // act
        boolean functionCall = totpService.verifyWithSkew(CLIENT_SECRET, mockCode, SKEW);
        // assert
        assertTrue(functionCall);
    }

    @Test
    @DisplayName("[TotpService :: verifyWithSkew] - should return true if code generated is within skew")
    public void shouldReturnTrueIfWithinSkew() {
        // arrange
        Totp mockTotp = new Totp(CLIENT_SECRET, new Clock(2));
        String mockCode = mockTotp.now();
        // act
        boolean functionCall = totpService.verifyWithSkew(CLIENT_SECRET, mockCode, SKEW);
        // assert
        assertTrue(functionCall);
    }

    @Test
    @DisplayName("[TotpService :: verifyWithSkew] - should return false if code generated is out of skew")
    public void shouldReturnFalseIfOutOfSkew() {
        // arrange
        Totp mockTotp = new Totp(CLIENT_SECRET, new Clock(5));
        String mockCode = mockTotp.now();
        // act
        boolean functionCall = totpService.verifyWithSkew(CLIENT_SECRET, mockCode, SKEW);
        // assert
        assertFalse(functionCall);
    }
}
