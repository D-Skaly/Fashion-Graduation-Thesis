package com.skaly.fashion_backend.ai.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evaluator to check if AI response is relevant to the user query.
 * Required by docs/data-privacy.md - MUST implement RelevancyEvaluator.
 * Calculates Jaccard similarity between query and response keywords.
 */
@Slf4j
@Component
public class RelevancyEvaluator {
    
    /**
     * Evaluate the relevancy of AI response to the user query.
     * @param query The original user query
     * @param response The AI's response
     * @return Relevancy score between 0.0 (not relevant) and 1.0 (highly relevant)
     */
    public double evaluateRelevancy(String query, String response) {
        if (query == null || response == null) {
            return 0.0;
        }
        
        Set<String> queryKeywords = extractKeywords(query.toLowerCase());
        Set<String> responseKeywords = extractKeywords(response.toLowerCase());
        
        if (queryKeywords.isEmpty() || responseKeywords.isEmpty()) {
            return 0.0;
        }
        
        // Jaccard similarity: intersection / union
        Set<String> intersection = queryKeywords.stream()
                .filter(responseKeywords::contains)
                .collect(Collectors.toSet());
        
        Set<String> union = new java.util.HashSet<>(queryKeywords);
        union.addAll(responseKeywords);
        
        double similarity = (double) intersection.size() / union.size();
        log.debug("Relevancy score: {} for query: {}", similarity, query);
        
        return similarity;
    }
    
    /**
     * Extract meaningful keywords from text.
     * Removes common stop words and keeps words longer than 3 characters.
     */
    private Set<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return Set.of();
        }
        
        // Simple keyword extraction - split by whitespace and punctuation
        String[] words = text.split("[\\s\\p{Punct}]+");
        
        return Set.of(words).stream()
                .filter(w -> w.length() > 3)
                .filter(w -> !isStopWord(w))
                .collect(Collectors.toSet());
    }
    
    /**
     * Check if a word is a common stop word.
     */
    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
            "this", "that", "with", "from", "have", "what", "which", 
            "when", "where", "will", "would", "could", "should",
            "the", "and", "for", "are", "was", "but", "not", "you",
            "all", "can", "had", "her", "was", "one", "our", "out",
            "day", "get", "has", "him", "his", "how", "its", "may",
            "new", "now", "old", "see", "two", "way", "who", "did",
            "does", "let", "say", "she", "too", "use"
        );
        return stopWords.contains(word.toLowerCase());
    }
}
