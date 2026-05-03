package com.skaly.fashion_backend.saga.application;

import com.skaly.fashion_backend.saga.domain.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SagaOrchestrator<T> {

    private final List<SagaStep<T>> steps = new ArrayList<>();
    private int currentStepIndex = 0;

    public SagaOrchestrator<T> addStep(SagaStep<T> step) {
        steps.add(step);
        return this;
    }

    public boolean execute(T context) {
        currentStepIndex = 0;
        
        try {
            // Execute all steps in order
            for (SagaStep<T> step : steps) {
                log.info("Executing saga step: {}", step.getName());
                step.execute(context);
                currentStepIndex++;
            }
            
            log.info("Saga execution completed successfully");
            return true;
        } catch (Exception e) {
            log.error("Saga execution failed at step: {}, compensating...", steps.get(currentStepIndex).getName(), e);
            compensate(context);
            return false;
        }
    }

    public void compensate(T context) {
        // Compensate in reverse order
        for (int i = currentStepIndex - 1; i >= 0; i--) {
            SagaStep<T> step = steps.get(i);
            try {
                if (step.canCompensate(context)) {
                    log.info("Compensating saga step: {}", step.getName());
                    step.compensate(context);
                }
            } catch (Exception e) {
                log.error("Failed to compensate step: {}", step.getName(), e);
                // Continue compensating other steps even if one fails
            }
        }
        
        log.info("Saga compensation completed");
    }

    public void reset() {
        steps.clear();
        currentStepIndex = 0;
    }
}
