package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.product.ProductVariant;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderPricingService {

    public BigDecimal calculateUnitPrice(ProductVariant variant) {
        BigDecimal price = variant.getProduct().getBasePrice();
        if (variant.getPriceAdjustment() != null) {
            price = price.add(variant.getPriceAdjustment());
        }
        return price;
    }

    public BigDecimal calculateLineTotal(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
