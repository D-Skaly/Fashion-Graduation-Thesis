package com.skaly.fashion_backend.payment.gateway;

import com.skaly.fashion_backend.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class MomoService implements PaymentGateway {

    @Value("${payment.momo.partner-code:}")
    private String partnerCode;

    @Value("${payment.momo.access-key:}")
    private String accessKey;

    @Value("${payment.momo.secret-key:}")
    private String secretKey;

    @Value("${payment.momo.endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String endpoint;

    @Override
    public PaymentResponse createPayment(Payment payment, String returnUrl, String ipAddress) {
        try {
            String orderId = UUID.randomUUID().toString();
            String requestId = UUID.randomUUID().toString();
            String orderInfo = "Payment for order " + payment.getOrder().getId();
            String amount = payment.getAmount().toString();

            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" +
                    "&ipnUrl=" + returnUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + returnUrl +
                    "&requestId=" + requestId +
                    "&requestType=captureWallet";

            String signature = hmacSHA256(secretKey, rawHash);

            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("partnerName", "Fashion Store");
            requestBody.put("storeId", "FashionStore001");
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", returnUrl);
            requestBody.put("ipnUrl", returnUrl);
            requestBody.put("lang", "vi");
            requestBody.put("extraData", "");
            requestBody.put("requestType", "captureWallet");
            requestBody.put("signature", signature);

            // In a real implementation, you would make an HTTP POST to Momo's API
            // For now, return a mock response
            String paymentUrl = endpoint + "?" + buildQueryString(requestBody);

            return new PaymentResponse(true, paymentUrl, orderId, "Payment URL created successfully");

        } catch (Exception e) {
            log.error("Error creating Momo payment", e);
            return new PaymentResponse(false, null, null, "Error: " + e.getMessage());
        }
    }

    @Override
    public CallbackResult processCallback(Map<String, String> params) {
        try {
            String signature = params.get("signature");
            String orderId = params.get("orderId");
            String requestId = params.get("requestId");
            String amount = params.get("amount");
            String orderInfo = params.get("orderInfo");
            String orderType = params.get("orderType");
            String transId = params.get("transId");
            String resultCode = params.get("resultCode");
            String message = params.get("message");
            String payType = params.get("payType");
            String responseTime = params.get("responseTime");
            String extraData = params.get("extraData");

            // Verify signature
            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&message=" + message +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&orderType=" + orderType +
                    "&partnerCode=" + partnerCode +
                    "&payType=" + payType +
                    "&requestId=" + requestId +
                    "&responseTime=" + responseTime +
                    "&resultCode=" + resultCode +
                    "&transId=" + transId;

            String calculatedSignature = hmacSHA256(secretKey, rawHash);

            if (!calculatedSignature.equals(signature)) {
                log.error("Invalid signature");
                return new CallbackResult(false, null, null, null, null, "Invalid signature");
            }

            boolean success = "0".equals(resultCode);
            BigDecimal paymentAmount = new BigDecimal(amount);

            return new CallbackResult(
                    success,
                    null,
                    transId,
                    paymentAmount,
                    params.toString(),
                    success ? "Payment successful" : "Payment failed: " + message
            );

        } catch (Exception e) {
            log.error("Error processing Momo callback", e);
            return new CallbackResult(false, null, null, null, null, "Error: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatusResponse queryPaymentStatus(String transactionId) {
        // Implementation for querying payment status via Momo API
        return new PaymentStatusResponse(true, "PENDING", "Query not implemented");
    }

    @Override
    public RefundResponse refundPayment(String transactionId, BigDecimal amount, String reason) {
        // Implementation for refund via Momo API
        return new RefundResponse(false, null, "Refund not implemented");
    }

    @Override
    public String getPaymentMethod() {
        return "MOMO";
    }

    private String hmacSHA256(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }

    private String buildQueryString(Map<String, Object> params) {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (query.length() > 0) {
                query.append("&");
            }
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return query.toString();
    }
}
