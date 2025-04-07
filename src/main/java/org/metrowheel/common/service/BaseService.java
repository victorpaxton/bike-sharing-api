package org.metrowheel.common.service;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.transaction.Transactional;
import org.metrowheel.common.model.PagedResponse;
import org.metrowheel.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Base service class providing common CRUD operations.
 *
 * @param <T> Entity type
 * @param <ID> ID type
 */
public abstract class BaseService<T, ID> {
    
    /**
     * Get the repository for this service
     */
    protected abstract PanacheRepository<T> getRepository();
    
    /**
     * Find entity by ID, converting ID to appropriate type if needed
     */
    protected abstract Optional<T> findEntityById(ID id);
    
    /**
     * Get the entity class name for error messages
     */
    protected abstract String getEntityName();
    
    /**
     * Find all entities with pagination
     */
    public PagedResponse<T> findAll(int page, int size) {
        List<T> content = getRepository().findAll().page(page, size).list();
        long count = getRepository().count();
        return new PagedResponse<>(content, page, size, count);
    }
    
    /**
     * Find entity by ID
     */
    public T findById(ID id) {
        return findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), id.toString()));
    }
    
    /**
     * Create new entity
     */
    @Transactional
    public T create(T entity) {
        getRepository().persist(entity);
        return entity;
    }
    
    /**
     * Update entity if it exists
     */
    @Transactional
    public T update(ID id, T entity) {
        // Implementation would vary based on entity type
        // This is a placeholder method that should be implemented by subclasses
        throw new UnsupportedOperationException("Update operation not implemented");
    }
    
    /**
     * Delete entity by ID
     */
    @Transactional
    public boolean delete(ID id) {
        T entity = findById(id);
        getRepository().delete(entity);
        return true;
    }
}
