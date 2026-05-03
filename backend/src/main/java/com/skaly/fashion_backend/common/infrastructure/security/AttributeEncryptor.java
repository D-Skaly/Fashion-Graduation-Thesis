package com.skaly.fashion_backend.common.infrastructure.security;

import com.skaly.fashion_backend.common.infrastructure.config.EncryptionProperties;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
@RequiredArgsConstructor
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private final EncryptionProperties encryptionProperties;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || encryptionProperties.getSecretKey() == null) {
            return attribute;
        }
        try {
            return EncryptionUtil.encrypt(attribute, encryptionProperties.getSecretKey());
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt attribute", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || encryptionProperties.getSecretKey() == null) {
            return dbData;
        }
        try {
            return EncryptionUtil.decrypt(dbData, encryptionProperties.getSecretKey());
        } catch (Exception e) {
            // If decryption fails, return the original value (might be legacy data)
            return dbData;
        }
    }
}