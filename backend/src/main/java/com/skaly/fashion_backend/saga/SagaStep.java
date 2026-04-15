package com.skaly.fashion_backend.saga;

public interface SagaStep<T> {
    String getName();
    void execute(T context);
    void compensate(T context);
    boolean canCompensate(T context);
}
