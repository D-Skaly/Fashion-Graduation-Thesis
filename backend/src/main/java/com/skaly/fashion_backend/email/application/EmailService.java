package com.skaly.fashion_backend.email.application;

import com.skaly.fashion_backend.common.infrastructure.config.AppProperties;
import com.skaly.fashion_backend.common.infrastructure.config.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailProperties emailProperties;
    private final AppProperties appProperties;

    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(variables);
            context.setVariable("frontendUrl", appProperties.getUrl());

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    @Async
    public void sendOrderConfirmationEmail(String to, String orderNumber, String customerName,
                                           double totalAmount, String orderDetailsUrl) {
        Map<String, Object> variables = Map.of(
                "customerName", customerName,
                "orderNumber", orderNumber,
                "totalAmount", totalAmount,
                "orderDetailsUrl", orderDetailsUrl
        );
        sendEmail(to, "OrderEntity Confirmation - " + orderNumber, "order-confirmation", variables);
    }

    @Async
    public void sendShippingNotificationEmail(String to, String orderNumber, String trackingNumber,
                                               String carrier, String trackingUrl) {
        Map<String, Object> variables = Map.of(
                "orderNumber", orderNumber,
                "trackingNumber", trackingNumber,
                "carrier", carrier,
                "trackingUrl", trackingUrl
        );
        sendEmail(to, "Your OrderEntity Has Been Shipped - " + orderNumber, "shipping-notification", variables);
    }

    @Async
    public void sendWelcomeEmail(String to, String customerName, String verificationUrl) {
        Map<String, Object> variables = Map.of(
                "customerName", customerName,
                "verificationUrl", verificationUrl
        );
        sendEmail(to, "Welcome to Fashion Store!", "welcome-email", variables);
    }

    @Async
    public void sendPasswordResetEmail(String to, String customerName, String resetUrl) {
        Map<String, Object> variables = Map.of(
                "customerName", customerName,
                "resetUrl", resetUrl
        );
        sendEmail(to, "Reset Your Password", "password-reset", variables);
    }
}
