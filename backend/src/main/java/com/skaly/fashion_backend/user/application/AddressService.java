package com.skaly.fashion_backend.user.application;

import com.skaly.fashion_backend.user.address.Address;
import com.skaly.fashion_backend.user.address.AddressRepository;
import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final JpaUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Address> getUserAddresses(UUID userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Address getAddressById(UUID addressId, UUID userId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
    }

    @Transactional(readOnly = true)
    public Address getDefaultAddress(UUID userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new AddressNotFoundException("No default address found"));
    }

    @Transactional
    public Address createAddress(UUID userId, AddressRequest request) {
        // Check max addresses per user
        long addressCount = addressRepository.countByUserId(userId);
        if (addressCount >= 10) {
            throw new IllegalStateException("Maximum 10 addresses allowed per user");
        }

        // If this is the first address or isDefault=true, set as default
        boolean shouldBeDefault = addressCount == 0 || Boolean.TRUE.equals(request.isDefault());

        // If setting as default, unset other defaults of same type
        if (shouldBeDefault && request.type() != null) {
            addressRepository.unsetDefaultAddresses(userId, request.type());
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Address address = Address.builder()
                .user(user)
                .fullName(request.fullName())
                .phone(request.phone())
                .address(request.address())
                .address2(request.address2())
                .city(request.city())
                .province(request.province())
                .postalCode(request.postalCode())
                .country(request.country())
                .isDefault(shouldBeDefault)
                .type(request.type() != null ? request.type() : Address.AddressType.SHIPPING)
                .build();

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(UUID addressId, UUID userId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        // If setting as default, unset other defaults
        if (Boolean.TRUE.equals(request.isDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.unsetDefaultAddresses(userId, request.type());
        }

        address.setFullName(request.fullName());
        address.setPhone(request.phone());
        address.setAddress(request.address());
        address.setAddress2(request.address2());
        address.setCity(request.city());
        address.setProvince(request.province());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setIsDefault(request.isDefault());
        if (request.type() != null) {
            address.setType(request.type());
        }

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(UUID addressId, UUID userId) {
        addressRepository.deleteByIdAndUserId(addressId, userId);
    }

    @Transactional
    public Address setDefaultAddress(UUID addressId, UUID userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        // Unset current default of same type
        addressRepository.unsetDefaultAddresses(userId, address.getType());

        address.setIsDefault(true);
        return addressRepository.save(address);
    }

    public record AddressRequest(
            String fullName,
            String phone,
            String address,
            String address2,
            String city,
            String province,
            String postalCode,
            String country,
            Boolean isDefault,
            Address.AddressType type
    ) {}

    public static class AddressNotFoundException extends RuntimeException {
        public AddressNotFoundException(String message) {
            super(message);
        }
    }
}
