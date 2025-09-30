package com.ylcnfrht.blockchain.domain.common.repositories;

import java.util.List;

/**
 * Write-only repository interface for command operations.
 * Separates write operations from read operations following CQRS pattern.
 * 
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface WriteRepository<T, ID> {
    
    /**
     * Save an entity
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);
    
    /**
     * Save multiple entities
     * @param entities the entities to save
     * @return List of saved entities
     */
    List<T> saveAll(List<T> entities);
    
    /**
     * Delete entity by ID
     * @param id the ID of the entity to delete
     */
    void deleteById(ID id);
    
    /**
     * Delete an entity
     * @param entity the entity to delete
     */
    void delete(T entity);
    
    /**
     * Delete multiple entities
     * @param entities the entities to delete
     */
    void deleteAll(List<T> entities);
    
    /**
     * Save entity and flush immediately
     * @param entity the entity to save
     * @return the saved entity
     */
    T saveAndFlush(T entity);
    
    /**
     * Flush pending changes to database
     */
    void flush();
}
