package com.auth_example.challenge_service.otp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTest {
    @Spy
    private OtpService otpService;
    private final int LENGTH = 6;

    @Test
    @DisplayName("[OtpService :: generateOtp] - should return 6 digit string")
    public void shouldReturnSixDigits() {
        // arrange
        // act
        String functionCall = otpService.generateOtp(LENGTH);
        // assert
        assertEquals(6, functionCall.length());
        assertTrue(functionCall.matches("^[0-9]+$"));
    }

    @Test
    @DisplayName("[OtpService :: generateOtp] - should throw [IllegalArgumentException] if length input is invalid")
    public void shouldThrowIllegalArgumentExceptionIfInvalidLength() {
        // arrange
        // act
        // assert
        assertThrows(IllegalArgumentException.class, () -> otpService.generateOtp(0));
    }
}
