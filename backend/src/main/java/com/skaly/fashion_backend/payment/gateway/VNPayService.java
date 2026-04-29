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
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class VNPayService implements PaymentGateway {

    @Value("${payment.vnpay.tmn-code:}")
    private String vnpTmnCode;

    @Value("${payment.vnpay.hash-secret:}")
    private String vnpHashSecret;

    @Value("${payment.vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpPayUrl;

    @Value("${payment.vnpay.api-url:https://sandbox.vnpayment.vn/merchant_webapi/api/transaction}")
    private String vnpApiUrl;

    @Override
    public PaymentResponse createPayment(Payment payment, String returnUrl, String ipAddress) {
        try {
            String vnpTxnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            String vnpIpAddr = ipAddress != null ? ipAddress : "127.0.0.1";

            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue()));
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", vnpTxnRef);
            vnpParams.put("vnp_OrderInfo", "Payment for order " + payment.getOrderId());
            vnpParams.put("vnp_OrderType", "250000"); // Fashion/Clothing
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", vnpIpAddr);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            Date now = new Date();
            vnpParams.put("vnp_CreateDate", formatter.format(now));

            // Build query string
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = vnpParams.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                hashData.append(entry.getKey()).append('=').append(entry.getValue());
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                if (iterator.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }

            // Create secure hash
            String secureHash = hmacSHA512(vnpHashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            String paymentUrl = vnpPayUrl + "?" + query;

            return new PaymentResponse(true, paymentUrl, vnpTxnRef, "Payment URL created successfully");

        } catch (Exception e) {
            log.error("Error creating VNPay payment", e);
            return new PaymentResponse(false, null, null, "Error: " + e.getMessage());
        }
    }

    @Override
    public CallbackResult processCallback(Map<String, String> params) {
        try {
            String secureHash = params.remove("vnp_SecureHash");
            String hashData = buildHashData(params);
            String calculatedHash = hmacSHA512(vnpHashSecret, hashData);

            if (!calculatedHash.equals(secureHash)) {
                log.error("Invalid secure hash");
                return new CallbackResult(false, null, null, null, null, "Invalid secure hash");
            }

            String responseCode = params.get("vnp_ResponseCode");
            boolean success = "00".equals(responseCode);
            String transactionId = params.get("vnp_TransactionNo");
            String txnRef = params.get("vnp_TxnRef");
            BigDecimal amount = new BigDecimal(params.get("vnp_Amount")).divide(BigDecimal.valueOf(100));

            return new CallbackResult(
                    success,
                    null, // Will be set by caller
                    transactionId,
                    amount,
                    params.toString(),
                    success ? "Payment successful" : "Payment failed: " + responseCode
            );

        } catch (Exception e) {
            log.error("Error processing VNPay callback", e);
            return new CallbackResult(false, null, null, null, null, "Error: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatusResponse queryPaymentStatus(String transactionId) {
        // Implementation for querying payment status via VNPay API
        return new PaymentStatusResponse(true, "PENDING", "Query not implemented");
    }

    @Override
    public RefundResponse refundPayment(String transactionId, BigDecimal amount, String reason) {
        // Implementation for refund via VNPay API
        return new RefundResponse(false, null, "Refund not implemented");
    }

    @Override
    public String getPaymentMethod() {
        return "VNPAY";
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
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

    private String buildHashData(Map<String, String> params) {
        StringBuilder hashData = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = new TreeMap<>(params).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey()).append('=').append(entry.getValue());
                if (iterator.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        return hashData.toString();
    }
}
