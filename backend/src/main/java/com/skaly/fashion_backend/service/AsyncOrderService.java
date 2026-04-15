package com.skaly.fashion_backend.service;

import com.skaly.fashion_backend.email.EmailService;
import com.skaly.fashion_backend.order.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncOrderService {

    private final EmailService emailService;

    @Async("taskExecutor")
    public void processOrderAsync(Order order) {
        try {
            log.info("Processing order asynchronously: {}", order.getId());
            
            // Simulate heavy processing
            Thread.sleep(1000);
            
            log.info("Order processed successfully: {}", order.getId());
        } catch (InterruptedException e) {
            log.error("Error processing order: {}", order.getId(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Async("emailExecutor")
    public void sendOrderConfirmationEmailAsync(String to, String orderNumber, String customerName, 
                                                   double totalAmount, String orderDetailsUrl) {
        try {
            log.info("Sending order confirmation email asynchronously for order: {}", orderNumber);
            emailService.sendOrderConfirmationEmail(to, orderNumber, customerName, totalAmount, orderDetailsUrl);
            log.info("Order confirmation email sent successfully for order: {}", orderNumber);
        } catch (Exception e) {
            log.error("Error sending order confirmation email for order: {}", orderNumber, e);
        }
    }

    @Async("reportExecutor")
    public void generateOrderReportAsync() {
        try {
            log.info("Generating order report asynchronously");
            
            // Simulate heavy report generation
            Thread.sleep(5000);
            
            log.info("Order report generated successfully");
        } catch (InterruptedException e) {
            log.error("Error generating order report", e);
            Thread.currentThread().interrupt();
        }
    }
}
