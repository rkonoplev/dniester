package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.model.News;
import org.springframework.stereotype.Component;

@Component
public class NewsMapper {

    public NewsDto toDto(News entity) {
        return new NewsDto(
                entity.getId(),
                entity.getTitle(),
                entity.getTeaser(),
                entity.getContent(),
                entity.getPublishedAt()
        );
    }

    public News fromCreateRequest(NewsCreateRequest request) {
        News news = new News();
        news.setTitle(request.getTitle());
        news.setTeaser(request.getTeaser());
        news.setContent(request.getContent());
        news.setPublishedAt(request.getPublishedAt());
        return news;
    }

    public void updateEntity(News news, NewsUpdateRequest request) {
        news.setTitle(request.getTitle());
        news.setTeaser(request.getTeaser());
        news.setContent(request.getContent());
        news.setPublishedAt(request.getPublishedAt());
    }
}
