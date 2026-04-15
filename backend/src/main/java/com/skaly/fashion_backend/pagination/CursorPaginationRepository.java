package com.skaly.fashion_backend.pagination;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CursorPaginationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Execute cursor-based pagination query
     */
    public <T> CursorPage<T> findWithCursorPagination(
            String entityName,
            String sortField,
            String sortDirection,
            String cursor,
            int limit,
            Map<String, Object> filters) {
        
        // Build base query
        StringBuilder queryBuilder = new StringBuilder();
        StringBuilder countQueryBuilder = new StringBuilder();
        
        queryBuilder.append("SELECT e FROM ").append(entityName).append(" e WHERE e.deletedAt IS NULL ");
        countQueryBuilder.append("SELECT COUNT(e) FROM ").append(entityName).append(" e WHERE e.deletedAt IS NULL ");
        
        // Add filters
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String condition = " AND e." + entry.getKey() + " = :" + entry.getKey();
                queryBuilder.append(condition);
                countQueryBuilder.append(condition);
            }
        }
        
        // Add cursor condition
        if (cursor != null && !cursor.isEmpty()) {
            CursorUtils.CursorData cursorData = CursorUtils.decodeCursor(cursor);
            if (cursorData != null) {
                String cursorCondition = " AND (e." + sortField + " " + 
                    ("DESC".equals(sortDirection) ? "<" : ">") + " :cursorTimestamp OR " +
                    "(e." + sortField + " = :cursorTimestamp AND e.id " + 
                    ("DESC".equals(sortDirection) ? "<" : ">") + " :cursorId))";
                queryBuilder.append(cursorCondition);
            }
        }
        
        // Add order by
        queryBuilder.append(" ORDER BY e.").append(sortField).append(" ").append(sortDirection);
        queryBuilder.append(", e.id ").append(sortDirection);
        
        // Execute main query
        Query query = entityManager.createQuery(queryBuilder.toString());
        
        // Set parameters
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        
        if (cursor != null && !cursor.isEmpty()) {
            CursorUtils.CursorData cursorData = CursorUtils.decodeCursor(cursor);
            if (cursorData != null) {
                query.setParameter("cursorTimestamp", cursorData.getTimestamp());
                query.setParameter("cursorId", cursorData.getId());
            }
        }
        
        query.setMaxResults(limit + 1); // Fetch one extra to determine if there's a next page
        
        @SuppressWarnings("unchecked")
        List<T> results = query.getResultList();
        
        // Determine pagination info
        boolean hasNext = results.size() > limit;
        if (hasNext) {
            results.remove(results.size() - 1); // Remove the extra item
        }
        
        String nextCursor = null;
        String previousCursor = null;
        
        if (hasNext && !results.isEmpty()) {
            // This is simplified - in production, you'd need to extract actual timestamp and ID
            nextCursor = CursorUtils.encodeCursor(System.currentTimeMillis(), results.get(results.size() - 1).toString());
        }
        
        return new CursorPage<>(results, nextCursor, previousCursor, hasNext, cursor != null, limit);
    }
}
