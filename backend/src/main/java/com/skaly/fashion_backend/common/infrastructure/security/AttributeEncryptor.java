package com.skaly.fashion_backend.common.infrastructure.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class AttributeEncryptor implements AttributeConverter<String, String> {

    @Value("${application.security.encryption.key}")
    private String encryptionKey;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || encryptionKey == null) {
            return attribute;
        }
        try {
            return EncryptionUtil.encrypt(attribute, encryptionKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt attribute", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || encryptionKey == null) {
            return dbData;
        }
        try {
            return EncryptionUtil.decrypt(dbData, encryptionKey);
        } catch (Exception e) {
            // If decryption fails, return the original value (might be legacy data)
            return dbData;
        }
    }
}