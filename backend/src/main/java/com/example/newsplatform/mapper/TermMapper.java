package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.TermCreateRequestDto;
import com.example.newsplatform.dto.response.TermResponseDto;
import com.example.newsplatform.dto.request.TermUpdateRequestDto;
import com.example.newsplatform.entity.Term;
import org.springframework.stereotype.Component;

/**
 * Mapper for Term entity and DTOs.
 */
@Component
public class TermMapper {

    public TermResponseDto toResponse(Term term) {
        return new TermResponseDto(
                term.getId(),
                term.getName(),
                term.getVocabulary()
        );
    }

    public Term toEntity(TermCreateRequestDto request) {
        Term term = new Term();
        term.setName(request.name());
        term.setVocabulary(request.vocabulary());
        return term;
    }

    public void updateEntity(Term term, TermUpdateRequestDto request) {
        term.setName(request.name());
        term.setVocabulary(request.vocabulary());
    }
}