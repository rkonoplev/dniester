package com.example.newsplatform.service;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.exception.NotFoundException;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.model.News;
import com.example.newsplatform.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    // Get all published news
    public List<NewsDto> getAllPublishedNews() {
        List<News> newsList = newsRepository.findAll()
                .stream()
                .filter(News::isPublished) // only published news
                .collect(Collectors.toList());

        return newsList.stream()
                .map(NewsMapper::toDto)
                .collect(Collectors.toList());
    }

    // Get news by id, only if published
    public NewsDto getPublishedNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));
        if (!news.isPublished()) {
            throw new NotFoundException("News not published with id " + id);
        }
        return NewsMapper.toDto(news);
    }

    // Additional methods (for admin) can be added later

    // Get all news (published and unpublished)
    public List<NewsDto> getAllNews() {
        List<News> newsList = newsRepository.findAll();
        return newsList.stream()
                .map(NewsMapper::toDto)
                .collect(Collectors.toList());
    }

    // Get news by id (any status)
    public NewsDto getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));
        return NewsMapper.toDto(news);
    }

    // Create new news
    public NewsDto createNews(NewsCreateRequest request) {
        News news = NewsMapper.fromCreateRequest(request);
        News saved = newsRepository.save(news);
        return NewsMapper.toDto(saved);
    }

    // Update existing news
    public NewsDto updateNews(Long id, NewsUpdateRequest request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));
        NewsMapper.updateFromRequest(request, news);
        News updated = newsRepository.save(news);
        return NewsMapper.toDto(updated);
    }

    // Delete news by id
    public void deleteNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));
        newsRepository.delete(news);
    }

}
