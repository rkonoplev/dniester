package com.example.newsplatform.controller;

import com.example.newsplatform.dto.TermCreateRequestDto;
import com.example.newsplatform.dto.TermResponseDto;
import com.example.newsplatform.dto.TermUpdateRequestDto;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.mapper.TermMapper;
import com.example.newsplatform.service.TermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for term management operations.
 * Requires ADMIN role authentication.
 */
@RestController
@RequestMapping("/api/admin/terms")
@Tag(name = "Admin Terms", description = "Term management operations (Admin only)")
public class AdminTermController {

    private final TermService termService;
    private final TermMapper termMapper;

    @Autowired
    public AdminTermController(TermService termService, TermMapper termMapper) {
        this.termService = termService;
        this.termMapper = termMapper;
    }

    @GetMapping
    @Operation(summary = "Get all terms with pagination")
    public ResponseEntity<Page<TermResponseDto>> getAllTerms(Pageable pageable) {
        Page<Term> terms = termService.findAll(pageable);
        Page<TermResponseDto> response = terms.map(termMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get term by ID")
    public ResponseEntity<TermResponseDto> getTermById(@PathVariable Long id) {
        Term term = termService.findById(id);
        return ResponseEntity.ok(termMapper.toResponse(term));
    }

    @PostMapping
    @Operation(summary = "Create new term")
    public ResponseEntity<TermResponseDto> createTerm(@Valid @RequestBody TermCreateRequestDto request) {
        Term term = termMapper.toEntity(request);
        Term savedTerm = termService.save(term);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(termMapper.toResponse(savedTerm));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing term")
    public ResponseEntity<TermResponseDto> updateTerm(
            @PathVariable Long id,
            @Valid @RequestBody TermUpdateRequestDto request) {
        Term existingTerm = termService.findById(id);
        termMapper.updateEntity(existingTerm, request);
        Term updatedTerm = termService.save(existingTerm);
        return ResponseEntity.ok(termMapper.toResponse(updatedTerm));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete term by ID")
    public ResponseEntity<Void> deleteTerm(@PathVariable Long id) {
        termService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}