package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.exception.ResourceNotFoundException;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NewsServiceImpl implements com.example.newsplatform.service.NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper newsMapper;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, UserRepository userRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.newsMapper = newsMapper;
    }

    @Override
    public Page<NewsDto> searchAll(String filter, Pageable pageable) {
        return newsRepository.findAll(pageable).map(newsMapper::toDto);
    }

    @Override
    public Page<NewsDto> searchPublished(String filter, Pageable pageable) {
        return newsRepository.findByPublished(true, pageable).map(newsMapper::toDto);
    }

    @Override
    public NewsDto getPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublished(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        return newsMapper.toDto(news);
    }

    @Override
    public Page<NewsDto> getByTermId(Long termId, Pageable pageable) {
        return newsRepository.findByTerms_IdAndPublished(termId, true, pageable).map(newsMapper::toDto);
    }

    @Override
    public Page<NewsDto> getByTermIds(Long[] termIds, Pageable pageable) {
        return newsRepository.findByTerms_IdInAndPublished(termIds, true, pageable).map(newsMapper::toDto);
    }

    @Override
    public NewsDto create(NewsCreateRequestDto createRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        News news = newsMapper.fromCreateRequest(createRequest);
        news.setAuthor(author);

        return newsMapper.toDto(newsRepository.save(news));
    }

    @Override
    public NewsDto update(Long id, NewsUpdateRequestDto updateRequest) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));

        newsMapper.updateEntity(updateRequest, news);
        return newsMapper.toDto(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new ResourceNotFoundException("News", "id", id);
        }
        newsRepository.deleteById(id);
    }
}
