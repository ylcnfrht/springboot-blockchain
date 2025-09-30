package com.ylcnfrht.blockchain.domain.common.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Read-only repository interface for query operations.
 * Separates read operations from write operations following CQRS pattern.
 * 
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface ReadRepository<T, ID> {
    
    /**
     * Find entity by ID
     * @param id the ID to search for
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Check if entity exists by ID
     * @param id the ID to check
     * @return true if entity exists, false otherwise
     */
    boolean existsById(ID id);
    
    /**
     * Count total number of entities
     * @return total count
     */
    long count();
    
    /**
     * Find all active entities
     * @return List of active entities
     */
    List<T> findByActiveTrue();
}
