package com.skaly.fashion_backend.user.address;

import com.skaly.fashion_backend.common.domain.ApiResponse;
import com.skaly.fashion_backend.user.application.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        List<Address> addresses = addressService.getUserAddresses(userId);
        List<AddressResponse> response = addresses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Address address = addressService.getAddressById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(address)));
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<AddressResponse>> getDefaultAddress(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Address address = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(address)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Address saved = addressService.createAddress(userId, toServiceRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(mapToResponse(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Address updated = addressService.updateAddress(id, userId, toServiceRequest(request));
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        addressService.deleteAddress(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Address updated = addressService.setDefaultAddress(id, userId);
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

    private AddressService.AddressRequest toServiceRequest(AddressRequest request) {
        return new AddressService.AddressRequest(
                request.fullName(),
                request.phone(),
                request.address(),
                request.address2(),
                request.city(),
                request.province(),
                request.postalCode(),
                request.country(),
                request.isDefault(),
                request.type()
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
}
