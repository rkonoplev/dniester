package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
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

        // Extract category and terms
        String category = null;
        Long categoryId = null;
        if (entity.getTerms() != null && !entity.getTerms().isEmpty()) {
            Term firstTerm = entity.getTerms().iterator().next();
            category = firstTerm.getName();
            categoryId = firstTerm.getId();
        }

        return new NewsDto(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                entity.getTeaser(),
                entity.getPublicationDate(),
                entity.isPublished(),
                entity.getAuthor() != null ? entity.getAuthor().getUsername() : null,
                entity.getTerms() != null ? 
                    entity.getTerms().stream().map(Term::getName).collect(java.util.stream.Collectors.toSet()) : 
                    java.util.Set.of(),
                category,
                categoryId,
                entity.getAuthor() != null ? entity.getAuthor().getId() : null
        );
    }

    /** Maps NewsDto to NewsCreateRequest for service layer. */
    public static NewsCreateRequestDto newsDtoToCreateRequest(NewsDto dto) {
        if (dto == null) return null;
        NewsCreateRequestDto request = new NewsCreateRequestDto();
        request.setTitle(dto.title());
        request.setTeaser(dto.teaser());
        request.setBody(dto.body());
        request.setCategoryId(dto.categoryId());
        request.setAuthorId(dto.authorId());
        request.setPublicationDate(dto.publicationDate());
        request.setPublished(dto.published());
        return request;
    }

    /** Maps NewsDto to NewsUpdateRequest for service layer. */
    public static NewsUpdateRequestDto newsDtoToUpdateRequest(NewsDto dto) {
        if (dto == null) return null;
        NewsUpdateRequestDto request = new NewsUpdateRequestDto();
        request.setTitle(dto.title());
        request.setTeaser(dto.teaser());
        request.setBody(dto.body());
        request.setCategoryId(dto.categoryId());
        request.setAuthorId(dto.authorId());
        request.setPublicationDate(dto.publicationDate());
        request.setPublished(dto.published());
        return request;
    }

    /** Converts create request to News entity. */
    public static News fromCreateRequest(NewsCreateRequestDto request) {
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
    public static void updateEntity(News entity, NewsUpdateRequestDto request) {
        if (entity == null || request == null) return;

        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getTeaser() != null) entity.setTeaser(request.getTeaser());
        if (request.getBody() != null) entity.setBody(request.getBody());
        if (request.getPublicationDate() != null) entity.setPublicationDate(request.getPublicationDate());
        if (request.getPublished() != null) entity.setPublished(request.getPublished());
    }
}