@ApplicationModule(
    displayName = "ai",
    
    allowedDependencies = {"user", "recommendation", "product"}
)
package com.skaly.fashion_backend.ai;

import org.springframework.modulith.ApplicationModule;
