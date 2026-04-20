package com.skaly.fashion_backend.user.api;

import com.skaly.fashion_backend.common.ApiResponse;
import com.skaly.fashion_backend.user.api.dto.BodyProfileDto;
import com.skaly.fashion_backend.user.api.dto.SizeRecommendation;
import com.skaly.fashion_backend.user.application.BodyProfileService;
import com.skaly.fashion_backend.user.BodyProfile;
import com.skaly.fashion_backend.user.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/body-profile")
@RequiredArgsConstructor
public class BodyProfileController {

    private final BodyProfileService bodyProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<BodyProfile>> getMyProfile(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(ApiResponse.success(bodyProfileService.getByUserId(user.getId())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BodyProfile>> saveMyProfile(
            @AuthenticationPrincipal UserEntity user,
            @Valid @RequestBody BodyProfileDto dto) {
        return ResponseEntity.ok(ApiResponse.success(bodyProfileService.saveOrUpdate(user.getId(), dto)));
    }

    @PostMapping("/recommend-size")
    public ResponseEntity<ApiResponse<SizeRecommendation>> recommendSize(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(bodyProfileService.recommendSize(user.getId(), productId)));
    }
}

