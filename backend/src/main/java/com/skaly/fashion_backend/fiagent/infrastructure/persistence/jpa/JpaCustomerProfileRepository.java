package com.skaly.fashion_backend.fiagent.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCustomerProfileRepository extends JpaRepository<CustomerProfileEntity, UUID> {
    Optional<CustomerProfileEntity> findByCustomerId(UUID customerId);
}
