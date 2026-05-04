package com.skaly.fashion_backend.saga.domain;

import com.skaly.fashion_backend.payment.domain.Payment;
import com.skaly.fashion_backend.product.domain.model.ProductVariant;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context object for order-related saga operations.
 * Holds state shared across saga steps.
 */
public class OrderSagaContext {

    private UUID orderId;
    private UUID customerId;
    private String orderNumber;
    private String orderStatus;
    private final Map<UUID, Integer> productQuantities = new ConcurrentHashMap<>();
    private Payment payment;
    private String failureReason;
    private boolean compensating = false;
    private final Map<String, Object> customData = new ConcurrentHashMap<>();
    private boolean compensationRequired = false;

    public OrderSagaContext() {
    }

    public OrderSagaContext(UUID orderId, UUID customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public void addProduct(UUID productVariantId, int quantity) {
        productQuantities.put(productVariantId, quantity);
    }

    public void removeProduct(UUID productVariantId) {
        productQuantities.remove(productVariantId);
    }

    public Map<UUID, Integer> getProductQuantities() {
        return new ConcurrentHashMap<>(productQuantities);
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public boolean isCompensating() {
        return compensating;
    }

    public void setCompensating(boolean compensating) {
        this.compensating = compensating;
    }

    public boolean isCompensationRequired() {
        return compensationRequired;
    }

    public void setCompensationRequired(boolean compensationRequired) {
        this.compensationRequired = compensationRequired;
    }

    public void setCustomData(String key, Object value) {
        customData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getCustomData(String key) {
        return (T) customData.get(key);
    }

    public boolean hasCustomData(String key) {
        return customData.containsKey(key);
    }

    public void clearCustomData() {
        customData.clear();
    }
}
