package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;

import java.util.Optional;

/**
 * Utility class to map between News entity and various DTOs.
 * Ensures clean separation between API, service, and persistence layers.
 *
 * Note: Does not handle author ID directly — author is managed via User entity in service layer.
 */
public class NewsMapper {

    /**
     * Converts a News JPA entity to a NewsDto for API responses.
     *
     * @param entity the database entity
     * @return a populated NewsDto, or null if entity is null
     */
    public static NewsDto toDto(News entity) {
        if (entity == null) return null;

        NewsDto dto = new NewsDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setTeaser(entity.getTeaser());
        dto.setBody(entity.getBody());

        // Extract category name and ID from first Term
        if (entity.getTerms() != null && !entity.getTerms().isEmpty()) {
            Optional<Term> firstTerm = entity.getTerms().stream().findFirst();
            firstTerm.ifPresent(term -> {
                dto.setCategory(term.getName());
                dto.setCategoryId(term.getId());
            });
        }

        // Set author ID if author is present
        dto.setAuthorId(entity.getAuthor() != null ? entity.getAuthor().getId() : null);

        dto.setPublicationDate(entity.getPublicationDate());
        dto.setPublished(entity.isPublished());
        return dto;
    }

    /**
     * Maps NewsDto to NewsCreateRequest for service layer consumption.
     *
     * @param dto the input DTO from controller
     * @return a new NewsCreateRequest with copied fields
     */
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

    /**
     * Maps NewsDto to NewsUpdateRequest for service layer consumption.
     *
     * @param dto the input DTO from controller
     * @return a new NewsUpdateRequest with copied fields
     */
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

    /**
     * Converts NewsCreateRequest to a new News entity.
     * Author will be set in service layer.
     *
     * @param request the creation command
     * @return a new News entity, or null if request is null
     */
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
     * Updates an existing News entity with values from NewsUpdateRequest.
     * Author and terms are updated in service layer.
     *
     * @param entity  the existing entity to update
     * @param request the update command
     */
    public static void updateEntity(News entity, NewsUpdateRequest request) {
        if (entity == null || request == null) return;

        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getTeaser() != null) entity.setTeaser(request.getTeaser());
        if (request.getBody() != null) entity.setBody(request.getBody());
        if (request.getPublicationDate() != null) entity.setPublicationDate(request.getPublicationDate());
        if (request.isPublished() != null) entity.setPublished(request.isPublished());
    }
}