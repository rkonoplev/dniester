package com.example.newsplatform.mapper;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.dto.NewsDto;

/**
 * Utility class for converting between News (entity) and NewsDto (DTO).
 */
public class NewsMapper {

    /**
     * Converts a News JPA entity to a NewsDto.
     * @param entity News entity from database
     * @return NewsDto instance for API
     */
    public static NewsDto toDto(News entity) {
        if (entity == null) {
            return null;
        }
        NewsDto dto = new NewsDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        // Uncomment if your entity has teaser
        // dto.setTeaser(entity.getTeaser());
        dto.setContent(entity.getContent());
        // Adjust field if you map publishedAt to createdAt
        dto.setPublishedAt(entity.getCreatedAt());
        return dto;
    }

    /**
     * Converts a NewsDto to a News JPA entity.
     * @param dto NewsDto instance from API/user
     * @return News entity to store in database
     */
    public static News toEntity(NewsDto dto) {
        if (dto == null) {
            return null;
        }
        News entity = new News();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        // Uncomment if you have teaser in entity
        // entity.setTeaser(dto.getTeaser());
        entity.setContent(dto.getContent());
        // Adjust as needed for published, category, etc
        // entity.setCategory(...);
        // entity.setPublished(...);
        //entity.setCreatedAt(dto.getPublishedAt());
        return entity;
    }
}