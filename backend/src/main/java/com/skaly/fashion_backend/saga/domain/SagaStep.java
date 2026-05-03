package com.skaly.fashion_backend.saga.domain;

public interface SagaStep<T> {
    String getName();
    void execute(T context);
    void compensate(T context);
    boolean canCompensate(T context);
}
