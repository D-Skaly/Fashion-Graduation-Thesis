package com.skaly.fashion_backend.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BatchInsertUtil {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public <T> void batchInsert(List<T> entities, int batchSize) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));
            
            if (i % batchSize == 0 && i > 0) {
                // Flush and clear the EntityManager to release memory
                entityManager.flush();
                entityManager.clear();
            }
        }
        
        // Flush remaining entities
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    public <T> void batchUpdate(List<T> entities, int batchSize) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.merge(entities.get(i));
            
            if (i % batchSize == 0 && i > 0) {
                // Flush and clear the EntityManager to release memory
                entityManager.flush();
                entityManager.clear();
            }
        }
        
        // Flush remaining entities
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    public <T> void batchDelete(List<T> entities, int batchSize) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.remove(entities.get(i));
            
            if (i % batchSize == 0 && i > 0) {
                // Flush and clear the EntityManager to release memory
                entityManager.flush();
                entityManager.clear();
            }
        }
        
        // Flush remaining entities
        entityManager.flush();
        entityManager.clear();
    }
}
