package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;

import java.util.Optional;

/**
 * Mapper utility for converting between News entity and DTOs.
 */
public class NewsMapper {

    /**
     * Converts a News JPA entity to a NewsDto for API responses.
     */
    public static NewsDto toDto(News entity) {
        if (entity == null) {
            return null;
        }
        NewsDto dto = new NewsDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setTeaser(entity.getTeaser());
        dto.setBody(entity.getBody());
        // Map category from first available Term (simplification)
        if (entity.getTerms() != null && !entity.getTerms().isEmpty()) {
            Optional<Term> firstTerm = entity.getTerms().stream().findFirst();
            firstTerm.ifPresent(term -> dto.setCategory(term.getName()));
        }
        dto.setPublicationDate(entity.getPublicationDate());
        dto.setPublished(entity.isPublished());
        return dto;
    }

    /**
     * Converts a CreateRequest DTO to a new News entity.
     * @param request DTO from API
     */
    public static News fromCreateRequest(NewsCreateRequest request) {
        if (request == null) {
            return null;
        }
        News entity = new News();
        entity.setTitle(request.getTitle());
        entity.setTeaser(request.getTeaser());
        entity.setBody(request.getBody());
        entity.setPublicationDate(request.getPublicationDate());
        entity.setPublished(request.isPublished());
        // ⚠ Category→Term mapping is handled in Service layer (not here).
        return entity;
    }

    /**
     * Updates an existing News entity with fields from UpdateRequest.
     * @param entity existing News entity
     * @param request update request
     */
    public static void updateEntity(News entity, NewsUpdateRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setTitle(request.getTitle());
        entity.setTeaser(request.getTeaser());
        entity.setBody(request.getBody());
        entity.setPublicationDate(request.getPublicationDate());
        entity.setPublished(request.isPublished());
        // ⚠ Category→Term mapping also handled in Service layer
    }
}