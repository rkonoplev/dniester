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
                entity.getDescription(),
                entity.getType(),
                entity.getWeight()
        );
    }

    public static Term fromCreateRequest(TermCreateRequest request) {
        if (request == null) return null;
        
        Term entity = new Term();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setType(request.getType());
        entity.setWeight(request.getWeight() != null ? request.getWeight() : 0);
        return entity;
    }

    public static void updateEntity(Term entity, TermCreateRequest request) {
        if (entity == null || request == null) return;

        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getType() != null) entity.setType(request.getType());
        if (request.getWeight() != null) entity.setWeight(request.getWeight());
    }
}