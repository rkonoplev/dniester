package com.example.newsplatform.service;

import com.example.newsplatform.entity.Term;
import com.example.newsplatform.exception.ResourceNotFoundException;
import com.example.newsplatform.repository.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Term findById(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with id: " + id));
    }

    public Term save(Term term) {
        return termRepository.save(term);
    }

    public void deleteById(Long id) {
        if (!termRepository.existsById(id)) {
            throw new ResourceNotFoundException("Term not found with id: " + id);
        }
        termRepository.deleteById(id);
    }
}