package com.skaly.fashion_backend.saga.domain;

/**
 * Interface for Saga pattern steps.
 * Each step represents a transaction operation in a distributed transaction.
 */
public interface SagaStep<T> {
    
    /**
     * Execute the step logic.
     * @param context The saga context containing transaction data
     */
    void execute(T context);
    
    /**
     * Compensating action (rollback) if the step fails.
     * @param context The saga context containing transaction data
     */
    void compensate(T context);
    
    /**
     * Get the name of this step.
     */
    String getName();
}
