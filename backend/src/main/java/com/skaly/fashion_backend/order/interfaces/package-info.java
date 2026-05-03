@ApplicationModule(
    displayName = "order",
    
    allowedDependencies = {"user", "product", "payment", "cart", "email"}
)
package com.skaly.fashion_backend.order.interfaces;

import org.springframework.modulith.ApplicationModule;
