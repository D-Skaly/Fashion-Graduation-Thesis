package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderPricingService {

    public BigDecimal calculateUnitPrice(ProductVariantInternalResponse variant) {
        return variant.price();
    }

    public BigDecimal calculateLineTotal(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

