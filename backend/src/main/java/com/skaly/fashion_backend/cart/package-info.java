@ApplicationModule(
    displayName = "cart",
    
    allowedDependencies = {"user", "product", "coupon", "order"}
)
package com.skaly.fashion_backend.cart;

import org.springframework.modulith.ApplicationModule;
