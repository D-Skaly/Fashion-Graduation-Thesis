package com.skaly.fashion_backend.user.interfaces;

import com.skaly.fashion_backend.common.application.ApiResponse;
import com.skaly.fashion_backend.user.api.dto.BodyProfileDto;
import com.skaly.fashion_backend.user.api.dto.SizeRecommendation;
import com.skaly.fashion_backend.user.application.BodyProfileService;
import com.skaly.fashion_backend.user.BodyProfile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/body-profile")
@RequiredArgsConstructor
public class BodyProfileController {

    private final BodyProfileService bodyProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<BodyProfile>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(bodyProfileService.getByUserId(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BodyProfile>> saveMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BodyProfileDto dto) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(bodyProfileService.saveOrUpdate(userId, dto)));
    }

    @PostMapping("/recommend-size")
    public ResponseEntity<ApiResponse<SizeRecommendation>> recommendSize(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam UUID productId) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(bodyProfileService.recommendSize(userId, productId)));
    }
}

