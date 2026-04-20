package com.skaly.fashion_backend.product.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.product.ProductVariantEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaProductVariantRepository extends JpaRepository<ProductVariantEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    @Query("SELECT pv FROM ProductVariantEntity pv WHERE pv.id = :id")
    Optional<ProductVariantEntity> findByIdForUpdate(@Param("id") UUID id);
}

