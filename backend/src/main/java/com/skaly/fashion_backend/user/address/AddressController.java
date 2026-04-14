package com.skaly.fashion_backend.user.address;

import com.skaly.fashion_backend.common.ApiResponse;
import com.skaly.fashion_backend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses(
            @AuthenticationPrincipal User user) {
        List<Address> addresses = addressRepository.findByUserId(user.getId());
        List<AddressResponse> response = addresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(address)));
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress(
            @AuthenticationPrincipal User user) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .orElseThrow(() -> new AddressNotFoundException("No default address found"));
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(address)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user) {
        // Check max addresses per user
        long addressCount = addressRepository.countByUserId(user.getId());
        if (addressCount >= 10) {
            throw new IllegalStateException("Maximum 10 addresses allowed per user");
        }

        // If this is the first address or isDefault=true, set as default
        boolean shouldBeDefault = addressCount == 0 || Boolean.TRUE.equals(request.isDefault());

        // If setting as default, unset other defaults of same type
        if (shouldBeDefault && request.type() != null) {
            addressRepository.unsetDefaultAddresses(user.getId(), request.type());
        }

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

        Address saved = addressRepository.save(address);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(mapToResponse(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal User user) {
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        // If setting as default, unset other defaults
        if (Boolean.TRUE.equals(request.isDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.unsetDefaultAddresses(user.getId(), request.type());
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

        Address updated = addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        addressRepository.deleteByIdAndUserId(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        // Unset current default of same type
        addressRepository.unsetDefaultAddresses(user.getId(), address.getType());

        address.setIsDefault(true);
        Address updated = addressRepository.save(address);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(updated)));
    }

    private AddressResponse mapToResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getAddress(),
                address.getAddress2(),
                address.getCity(),
                address.getProvince(),
                address.getPostalCode(),
                address.getCountry(),
                address.getIsDefault(),
                address.getType()
        );
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

    public record AddressResponse(
            UUID id,
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
