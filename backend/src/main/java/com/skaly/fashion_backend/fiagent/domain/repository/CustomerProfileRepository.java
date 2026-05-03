package com.skaly.fashion_backend.fiagent.domain.repository;

import com.skaly.fashion_backend.fiagent.domain.model.CustomerProfile;
import java.util.UUID;

public interface CustomerProfileRepository {
    CustomerProfile findById(UUID id);
    CustomerProfile findByCustomerId(UUID customerId);
    CustomerProfile save(CustomerProfile customerProfile);
    void deleteById(UUID id);
}
