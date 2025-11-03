package com.example.phoebe.service;

import com.example.phoebe.entity.Term;
import com.example.phoebe.exception.ResourceNotFoundException;
import com.example.phoebe.repository.TermRepository;
import com.example.phoebe.service.impl.TermServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TermServiceTest {

    @Mock
    private TermRepository termRepository;

    @InjectMocks
    private TermServiceImpl termService;

    @Test
    void shouldSaveTerm() {
        // Given
        Term term = new Term("Technology", "category");
        Term savedTerm = new Term("Technology", "category");
        savedTerm.setId(1L);

        when(termRepository.save(term)).thenReturn(savedTerm);

        // When
        Term result = termService.save(term);

        // Then
        assertNotNull(result);
        assertEquals("Technology", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldFindTermById() {
        // Given
        Long termId = 1L;
        Term existingTerm = new Term("Sports", "category");
        existingTerm.setId(termId);

        when(termRepository.findById(termId)).thenReturn(Optional.of(existingTerm));

        // When
        Term result = termService.findById(termId);

        // Then
        assertNotNull(result);
        assertEquals("Sports", result.getName());
        assertEquals(termId, result.getId());
    }

    @Test
    void shouldThrowWhenFindingNonExistentTerm() {
        // Given
        Long termId = 99L;
        when(termRepository.findById(termId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> termService.findById(termId));
    }

    @Test
    void shouldFindAllTerms() {
        // Given
        Term term = new Term("Politics", "category");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Term> termPage = new PageImpl<>(List.of(term));
        when(termRepository.findAll(pageable)).thenReturn(termPage);

        // When
        Page<Term> result = termService.findAll(pageable);

        // Then
        verify(termRepository).findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Politics", result.getContent().get(0).getName());
    }
}
