package com.example.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.emailSender}")
    private String emailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Welcome email
    public void sendWelcomeEmail(String recipient, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSender);
        message.setTo(recipient);
        message.setSubject("Welcome to our MiniZon!");
        message.setText("Hi " + name + ",\n\n" +
                "Welcome to MiniZon!\n\n" +
                "We're excited to have you on board. You can now explore our collection of mobiles, tablets, and electronics.\n\n" +
                "If you have any questions or need help, feel free to reach out to our support team.\n\n" +
                "Happy shopping!\n\n" +
                "Best regards,\n" +
                "The MiniZon Team");
        mailSender.send(message);
    }

    // Verification OTP
    public void verificationOTP(String recipient, String name, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSender);
        message.setTo(recipient);
        message.setSubject("Verification OTP");

        String emailBody = "Hello " + name + ",\n\n"
                + "Your One-Time Password (OTP) for verification is: " + otp + "\n"
                + "This OTP is valid for the next 15 minutes only.\n\n"
                + "Best regards,\n"
                + "The MiniZon Team";

        message.setText(emailBody);
        mailSender.send(message);
    }
}
