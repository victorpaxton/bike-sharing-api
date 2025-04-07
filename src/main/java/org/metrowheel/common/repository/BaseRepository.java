package org.metrowheel.common.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Map;

/**
 * Base implementation of GenericRepository interface.
 * Provides common database operations for entity repositories.
 *
 * @param <T> The entity type
 */
public abstract class BaseRepository<T> implements GenericRepository<T> {
    
    private final EntityManager em;
    private final Class<T> entityClass;
    
    protected BaseRepository(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }
    
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }
    
    @Override
    public List<T> findByAttributes(Map<String, Object> attributes, int pageIndex, int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        
        // Add predicates for each attribute
        if (attributes != null && !attributes.isEmpty()) {
            List<Predicate> predicates = attributes.entrySet().stream()
                    .map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                    .toList();
            
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        TypedQuery<T> query = em.createQuery(cq);
        
        // Apply pagination
        if (pageSize > 0) {
            query.setFirstResult(pageIndex * pageSize);
            query.setMaxResults(pageSize);
        }
        
        return query.getResultList();
    }
    
    @Override
    public long countByAttributes(Map<String, Object> attributes) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        
        cq.select(cb.count(root));
        
        // Add predicates for each attribute
        if (attributes != null && !attributes.isEmpty()) {
            List<Predicate> predicates = attributes.entrySet().stream()
                    .map(entry -> cb.equal(root.get(entry.getKey()), entry.getValue()))
                    .toList();
            
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        
        return em.createQuery(cq).getSingleResult();
    }
}
