package com.skaly.fashion_backend.ai.interfaces;

import com.skaly.fashion_backend.common.domain.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/ai")
@RequiredArgsConstructor
public class AdminAiController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${application.ai.orchestrator.url:http://localhost:3001}")
    private String orchestratorUrl;

    @PostMapping("/plan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> createPlan(@RequestBody Map<String, String> request) {
        String url = orchestratorUrl + "/strategist/plan";
        Object result = restTemplate.postForObject(url, request, Object.class);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> approvePlan(@RequestBody Map<String, Object> request) {
        String url = orchestratorUrl + "/strategist/insights/review";
        request.put("decision", "APPROVE");
        Object result = restTemplate.postForObject(url, request, Object.class);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
