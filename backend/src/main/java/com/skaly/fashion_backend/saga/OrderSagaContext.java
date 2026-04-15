package com.skaly.fashion_backend.saga;

import com.skaly.fashion_backend.payment.Payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class OrderSagaContext {
    private UUID orderId;
    private UUID userId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Payment payment;
    private Map<UUID, Integer> productVariantsWithQuantity; // variantId -> quantity
    private Map<UUID, Integer> originalStockQuantities; // variantId -> original stock
    private String errorMessage;
    private boolean compensationRequired;

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Map<UUID, Integer> getProductVariantsWithQuantity() {
        return productVariantsWithQuantity;
    }

    public void setProductVariantsWithQuantity(Map<UUID, Integer> productVariantsWithQuantity) {
        this.productVariantsWithQuantity = productVariantsWithQuantity;
    }

    public Map<UUID, Integer> getOriginalStockQuantities() {
        return originalStockQuantities;
    }

    public void setOriginalStockQuantities(Map<UUID, Integer> originalStockQuantities) {
        this.originalStockQuantities = originalStockQuantities;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isCompensationRequired() {
        return compensationRequired;
    }

    public void setCompensationRequired(boolean compensationRequired) {
        this.compensationRequired = compensationRequired;
    }
}
