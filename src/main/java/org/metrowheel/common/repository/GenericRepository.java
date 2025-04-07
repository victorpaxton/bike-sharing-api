package org.metrowheel.common.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;

/**
 * Generic repository with common database operations.
 * Extends Quarkus Panache Repository for simplified database access.
 *
 * @param <T> The entity type this repository manages
 */
public interface GenericRepository<T> extends PanacheRepository<T> {
    
    /**
     * Get the entity manager
     */
    EntityManager getEntityManager();
    

    
    /**
     * Get the entity class
     */
    Class<T> getEntityClass();
    
    
    /**
     * Find entities by key-value pairs
     * 
     * @param attributes Map of attribute names to values to search for
     * @param pageIndex Zero-based page index
     * @param pageSize Size of each page
     * @return List of entities matching the criteria
     */
    List<T> findByAttributes(Map<String, Object> attributes, int pageIndex, int pageSize);
    
    /**
     * Count entities matching attributes
     * 
     * @param attributes Map of attribute names to values to search for
     * @return Count of matching entities
     */
    long countByAttributes(Map<String, Object> attributes);
}
