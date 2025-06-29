package com.example.pdp_project.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {

    public JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + verificationCode);
        message.setFrom("inosukebest1@gmail.com");
        mailSender.send(message);
    }
}
