package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;

/**
 * Utility class to map between News entity and various DTOs.
 * Ensures clean separation between API, service, and persistence layers.
 *
 * Note:
 * - Does not handle User (author) or Term mapping fully,
 *   those are set at the service layer with repository access.
 */
public class NewsMapper {

    /**
     * Converts a News JPA entity to a NewsDto for API responses.
     */
    public static NewsDto toDto(News entity) {
        if (entity == null) return null;

        NewsDto dto = new NewsDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setTeaser(entity.getTeaser());
        dto.setBody(entity.getBody());

        // Extract category (first term only for API preview)
        if (entity.getTerms() != null && !entity.getTerms().isEmpty()) {
            Term firstTerm = entity.getTerms().iterator().next();
            dto.setCategory(firstTerm.getName());
            dto.setCategoryId(firstTerm.getId());
        }

        dto.setAuthorId(entity.getAuthor() != null ? entity.getAuthor().getId() : null);
        dto.setPublicationDate(entity.getPublicationDate());
        dto.setPublished(entity.isPublished());
        return dto;
    }

    /** Maps NewsDto to NewsCreateRequest for service layer. */
    public static NewsCreateRequest newsDtoToCreateRequest(NewsDto dto) {
        if (dto == null) return null;
        NewsCreateRequest request = new NewsCreateRequest();
        request.setTitle(dto.getTitle());
        request.setTeaser(dto.getTeaser());
        request.setBody(dto.getBody());
        request.setCategoryId(dto.getCategoryId());
        request.setAuthorId(dto.getAuthorId());
        request.setPublicationDate(dto.getPublicationDate());
        request.setPublished(dto.isPublished());
        return request;
    }

    /** Maps NewsDto to NewsUpdateRequest for service layer. */
    public static NewsUpdateRequest newsDtoToUpdateRequest(NewsDto dto) {
        if (dto == null) return null;
        NewsUpdateRequest request = new NewsUpdateRequest();
        request.setTitle(dto.getTitle());
        request.setTeaser(dto.getTeaser());
        request.setBody(dto.getBody());
        request.setCategoryId(dto.getCategoryId());
        request.setAuthorId(dto.getAuthorId());
        request.setPublicationDate(dto.getPublicationDate());
        request.setPublished(dto.isPublished());
        return request;
    }

    /** Converts create request to News entity. */
    public static News fromCreateRequest(NewsCreateRequest request) {
        if (request == null) return null;
        News entity = new News();
        entity.setTitle(request.getTitle());
        entity.setTeaser(request.getTeaser());
        entity.setBody(request.getBody());
        entity.setPublicationDate(request.getPublicationDate());
        entity.setPublished(request.isPublished());
        return entity;
    }

    /**
     * Updates entity fields from update request (null-safe).
     */
    public static void updateEntity(News entity, NewsUpdateRequest request) {
        if (entity == null || request == null) return;

        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getTeaser() != null) entity.setTeaser(request.getTeaser());
        if (request.getBody() != null) entity.setBody(request.getBody());
        if (request.getPublicationDate() != null) entity.setPublicationDate(request.getPublicationDate());
        if (request.getPublished() != null) entity.setPublished(request.getPublished());
    }
}