package com.kgyp.kgypsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.from:noreply@kgyp.com}")
    private String fromEmail;

    @Value("${kgyp.system.name:KGYP Sistemi}")
    private String systemName;

    /**
     * Basit email gönderme
     */
    public boolean sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("[" + systemName + "] " + subject);
            message.setText(text);

            emailSender.send(message);
            logger.info("Email gönderildi: {} -> {}", subject, to);
            return true;

        } catch (Exception e) {
            logger.error("Email gönderimi hatası: {} -> {}", subject, to, e);
            return false;
        }
    }

    /**
     * Test email gönder
     */
    public boolean sendTestEmail(String to) {
        return sendEmail(to, "Test Bildirimi",
                "Bu bir test emailidir. KGYP sisteminiz email gönderebiliyor!");
    }
}