package com.skaly.fashion_backend.ai.application;

import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluator to check if AI response contains claims about non-existent products.
 * Required by docs/data-privacy.md - MUST implement FactCheckingEvaluator.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FactCheckingEvaluator {
    
    private final ProductRepository productRepository;
    
    /**
     * Evaluate if the AI response contains valid product claims.
     * @param userQuery The original user query
     * @param aiResponse The AI's response to evaluate
     * @return EvaluationResult indicating pass/fail and reason
     */
    public EvaluationResult evaluate(String userQuery, String aiResponse) {
        if (aiResponse == null || aiResponse.isEmpty()) {
            return new EvaluationResult(true, null);
        }
        
        // Extract potential product names from AI response
        List<String> claimedProducts = extractProductClaims(aiResponse);
        
        if (claimedProducts.isEmpty()) {
            return new EvaluationResult(true, null);
        }
        
        for (String productName : claimedProducts) {
            boolean exists = productRepository.findByName(productName).isPresent();
            if (!exists) {
                log.warn("Fact check failed: Product not found: {}", productName);
                return new EvaluationResult(false, 
                        "Product not found: " + productName);
            }
        }
        
        return new EvaluationResult(true, null);
    }
    
    /**
     * Extract potential product names from AI response.
     * Looks for capitalized words that might be product names.
     */
    private List<String> extractProductClaims(String response) {
        // Simple extraction - look for quoted strings or product-like patterns
        Pattern pattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(response);
        
        java.util.List<String> products = new java.util.ArrayList<>();
        while (matcher.find()) {
            String candidate = matcher.group(1);
            // Filter for likely product names (not too short, not too long)
            if (candidate.length() > 3 && candidate.length() < 100) {
                products.add(candidate);
            }
        }
        
        return products;
    }
    
    /**
     * Record representing evaluation result.
     * @param passed Whether the evaluation passed
     * @param reason Reason for failure (null if passed)
     */
    public record EvaluationResult(boolean passed, String reason) {}
}
