package com.example.phoebe.repository;

import com.example.phoebe.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Term entities in the database.
 * Provides CRUD operations and custom query methods via Spring Data JPA.
 *
 * Term represents a taxonomy category (e.g., "Technology", "Sports") used to classify news articles.
 * This repository allows searching, creating, updating, and deleting terms.
 *
 * Usage:
 * - Used in NewsServiceImpl to resolve category IDs during news creation/update
 * - Ensures data integrity by validating category existence
 */
@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
    // Inherited methods:
    // - findById(Long id)
    // - save(entity)
    // - delete(entity)
    // - findAll(), etc.

    // Custom methods can be added here later, e.g.:
    // Optional<Term> findByName(String name);
}