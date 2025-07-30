package com.auth_example.challenge_service.mfa.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendOtpEmail(String toEmail, String otp) {

        try {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject("Your OTP code for Auth-Example Project");
            helper.setFrom("no-reply@auth-example.com");

            Context context = new Context();
            context.setVariable("otp", otp);

            String html = templateEngine.process("otp-email", context);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("ERROR :: something went wrong with sending OTP to {}", toEmail);
        }
    }
}
