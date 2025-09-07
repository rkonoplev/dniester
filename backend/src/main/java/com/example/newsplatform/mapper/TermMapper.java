package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.TermCreateRequest;
import com.example.newsplatform.dto.TermDto;
import com.example.newsplatform.entity.Term;

/**
 * Utility class to map between Term entity and various DTOs.
 */
public class TermMapper {

    public static TermDto toDto(Term entity) {
        if (entity == null) return null;
        
        return new TermDto(
                entity.getId(),
                entity.getName(),
                entity.getVocabulary(),
                entity.getNewsArticles() != null ? entity.getNewsArticles().size() : 0
        );
    }

    public static Term fromCreateRequest(TermCreateRequest request) {
        if (request == null) return null;
        
        Term entity = new Term();
        entity.setName(request.getName());
        entity.setVocabulary(request.getVocabulary());
        return entity;
    }

    public static void updateEntity(Term entity, TermCreateRequest request) {
        if (entity == null || request == null) return;

        if (request.getName() != null) entity.setName(request.getName());
        if (request.getVocabulary() != null) entity.setVocabulary(request.getVocabulary());
    }
}