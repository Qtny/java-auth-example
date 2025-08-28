package com.auth_example.challenge_service.mfa.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private TemplateEngine templateEngine;
    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mockMimeMessage;
    private final String TO_EMAIL = "test@example.com";
    private final String OTP = "123456";
    private final String QR_CODE = "base64EncodeString";

    @BeforeEach
    void setup() {
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Mocked Email</html>");
    }

    @Test
    @DisplayName("[EmailService :: sendOtpEmail ] - should call send with correct parameters")
    public void shouldCallSendOtpEmailWithCorrectParameters() {
        // act
        emailService.sendOtpEmail(TO_EMAIL, OTP);
        // assert
        verify(javaMailSender).createMimeMessage();
        verify(templateEngine).process(eq("otp-email"), any(Context.class));
        verify(javaMailSender).send(mockMimeMessage);
    }

    @Test
    @DisplayName("[EmailService :: sendOtpEmail ] - should call handle messaging exception")
    public void shouldHandleMessagingExceptionForSendOtpEmail() {
        // arrange
        doThrow(new MailSendException("simulate error")).when(javaMailSender).send(any(MimeMessage.class));
        // act
        emailService.sendOtpEmail(TO_EMAIL, OTP);
        // assert
        verify(javaMailSender).createMimeMessage();
        verify(templateEngine).process(eq("otp-email"), any(Context.class));
        verify(javaMailSender).send(mockMimeMessage);
    }

    @Test
    @DisplayName("[EmailService :: sendTotpQrCode ] - should call send with correct parameters")
    public void shouldCallSendTotpQrCodeWithCorrectParameters() {
        // act
        emailService.sendTotpQrCode(TO_EMAIL, QR_CODE);
        // assert
        verify(javaMailSender).createMimeMessage();
        verify(templateEngine).process(eq("totp-email"), any(Context.class));
        verify(javaMailSender).send(mockMimeMessage);
    }

    @Test
    @DisplayName("[EmailService :: sendTotpQrCode ] - should call handle messaging exception")
    public void shouldHandleMessagingExceptionForSendTotpQrCode() {
        // arrange
        doThrow(new MailSendException("simulate error")).when(javaMailSender).send(any(MimeMessage.class));
        // act
        emailService.sendTotpQrCode(TO_EMAIL, QR_CODE);
        // assert
        verify(javaMailSender).createMimeMessage();
        verify(templateEngine).process(eq("totp-email"), any(Context.class));
        verify(javaMailSender).send(mockMimeMessage);
    }
}
