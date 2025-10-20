package com.example.phoebe.service;

import com.example.phoebe.entity.Term;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing taxonomy terms.
 */
public interface TermService {

    /**
     * Retrieves a paginated list of all terms.
     *
     * @param pageable Pagination configuration.
     * @return A {@link Page} of {@link Term}.
     */
    Page<Term> findAll(Pageable pageable);

    /**
     * Finds a term by its ID.
     *
     * @param id Term ID.
     * @return The found {@link Term}.
     */
    Term findById(Long id);

    /**
     * Saves a new or existing term.
     *
     * @param term The {@link Term} entity to persist.
     * @return The saved {@link Term}.
     */
    Term save(Term term);

    /**
     * Deletes a term by ID.
     *
     * @param id The ID of the term to delete.
     */
    void deleteById(Long id);
}
