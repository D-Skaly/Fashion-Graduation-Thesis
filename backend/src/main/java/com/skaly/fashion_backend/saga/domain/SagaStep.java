package com.skaly.fashion_backend.saga.domain;

/**
 * Interface for a saga step in the Saga pattern implementation.
 * Each step represents a single operation in a distributed transaction.
 *
 * @param <T> the type of saga context
 */
public interface SagaStep<T> {

    /**
     * Execute the saga step.
     * @param context the saga context containing shared data
     * @throws Exception if execution fails
     */
    void execute(T context) throws Exception;

    /**
     * Compensate (rollback) the saga step in case of failure.
     * @param context the saga context containing shared data
     */
    void compensate(T context);

    /**
     * Check if this step can be compensated.
     * @param context the saga context containing shared data
     * @return true if compensation is possible
     */
    default boolean canCompensate(T context) {
        return true;
    }

    /**
     * Get the name of this step for logging/monitoring.
     * @return step name
     */
    default String getStepName() {
        return this.getClass().getSimpleName();
    }
}
