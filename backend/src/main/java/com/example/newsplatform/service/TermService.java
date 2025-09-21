package com.example.newsplatform.service;

import com.example.newsplatform.entity.Term;
import com.example.newsplatform.exception.ResourceNotFoundException;
import com.example.newsplatform.repository.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for term management operations.
 */
@Service
@Transactional
public class TermService {

    private final TermRepository termRepository;

    @Autowired
    public TermService(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    public Page<Term> findAll(Pageable pageable) {
        return termRepository.findAll(pageable);
    }

    /**
     * Find term by ID with caching.
     * Terms are cached for 1 hour since they rarely change.
     */
    @Cacheable(value = "terms", key = "#id")
    public Term findById(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + id));
    }

    /**
     * Save term and evict cache to ensure consistency.
     */
    @CacheEvict(value = "terms", key = "#term.id")
    public Term save(Term term) {
        return termRepository.save(term);
    }

    /**
     * Delete term and evict from cache.
     */
    @CacheEvict(value = "terms", key = "#id")
    public void deleteById(Long id) {
        if (!termRepository.existsById(id)) {
            throw new ResourceNotFoundException("Term not found with id: " + id);
        }
        termRepository.deleteById(id);
    }
}