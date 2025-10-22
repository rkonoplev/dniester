package com.example.phoebe.service.impl;

import com.example.phoebe.entity.Term;
import com.example.phoebe.exception.ResourceNotFoundException;
import com.example.phoebe.repository.TermRepository;
import com.example.phoebe.service.TermService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TermServiceImpl implements TermService {

    private final TermRepository termRepository;

    public TermServiceImpl(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Term> findAll(Pageable pageable) {
        return termRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Term findById(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Term", "id", id));
    }

    @Override
    @Transactional
    public Term save(Term term) {
        return termRepository.save(term);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!termRepository.existsById(id)) {
            throw new ResourceNotFoundException("Term", "id", id);
        }
        termRepository.deleteById(id);
    }
}
