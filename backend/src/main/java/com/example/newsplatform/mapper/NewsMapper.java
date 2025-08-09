package com.example.newsplatform.mapper;

import com.example.newsplatform.model.News;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsUpdateRequest;

public class NewsMapper {

    // Convert News entity to NewsDto
    public static NewsDto toDto(News news) {
        if (news == null) {
            return null;
        }
        NewsDto dto = new NewsDto();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setTeaser(news.getTeaser());
        dto.setContent(news.getContent());
        dto.setCategory(news.getCategory());
        dto.setPublishedAt(news.getPublishedAt());
        dto.setPublished(news.isPublished());
        return dto;
    }

    // Convert NewsCreateRequest to News entity
    public static News fromCreateRequest(NewsCreateRequest request) {
        if (request == null) {
            return null;
        }
        News news = new News();
        news.setTitle(request.getTitle());
        news.setTeaser(request.getTeaser());
        news.setContent(request.getContent());
        news.setCategory(request.getCategory());
        news.setPublishedAt(request.getPublishedAt());
        news.setPublished(request.isPublished());
        return news;
    }

    // Update existing News entity from NewsUpdateRequest
    public static void updateFromRequest(NewsUpdateRequest request, News news) {
        if (request == null || news == null) {
            return;
        }
        news.setTitle(request.getTitle());
        news.setTeaser(request.getTeaser());
        news.setContent(request.getContent());
        news.setCategory(request.getCategory());
        news.setPublishedAt(request.getPublishedAt());
        news.setPublished(request.isPublished());
    }
}
