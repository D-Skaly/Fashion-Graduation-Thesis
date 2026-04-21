package com.skaly.fashion_backend.user.application;

import com.skaly.fashion_backend.user.api.dto.BodyProfileDto;
import com.skaly.fashion_backend.user.api.dto.SizeRecommendation;
import com.skaly.fashion_backend.user.BodyProfile;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.BodyProfileRepository;
import com.skaly.fashion_backend.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BodyProfileService {

    private final BodyProfileRepository bodyProfileRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.ai.service.url:http://localhost:8001}")
    private String aiServiceUrl;

    @Transactional
    public BodyProfile saveOrUpdate(UUID userId, BodyProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BodyProfile profile = bodyProfileRepository.findByUserId(userId)
                .orElseGet(() -> BodyProfile.builder().userId(userId).build());

        profile.setHeight(dto.height());
        profile.setWeight(dto.weight());
        profile.setChest(dto.chest());
        profile.setWaist(dto.waist());
        profile.setHips(dto.hips());

        return bodyProfileRepository.save(profile);
    }

    public BodyProfile getByUserId(UUID userId) {
        return bodyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Body profile not found"));
    }

    public SizeRecommendation recommendSize(UUID userId, UUID productId) {
        BodyProfile profile = bodyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Body profile not found"));
        
        // Try to get recommendation from FastAPI AI Service
        try {
            BodyProfileDto profileDto = new BodyProfileDto(
                    profile.getHeight(),
                    profile.getWeight(),
                    profile.getChest(),
                    profile.getWaist(),
                    profile.getHips()
            );
            return restTemplate.postForObject(aiServiceUrl + "/size/recommend", profileDto, SizeRecommendation.class);
        } catch (Exception e) {
            // Fallback to simple local logic if AI service is down
            String size = calculateSize(profile);
            return new SizeRecommendation(size, 0.70);
        }
    }

    private String calculateSize(BodyProfile profile) {
        if (profile.getChest() == null) return "M";
        if (profile.getChest() < 90) return "S";
        if (profile.getChest() < 100) return "M";
        if (profile.getChest() < 110) return "L";
        return "XL";
    }
}
