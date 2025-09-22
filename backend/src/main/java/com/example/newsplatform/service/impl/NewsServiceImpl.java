package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.exception.ResourceNotFoundException;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {

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

    @Override
    public void performBulkAction(BulkActionRequestDto request, Authentication auth) {
        // 1. Validate request
        if (request == null) {
            throw new IllegalArgumentException("Bulk action request cannot be null");
        }
        if (request.getAction() == null) {
            throw new IllegalArgumentException("Action type is required");
        }
        if (request.getFilterType() == null) {
            throw new IllegalArgumentException("Filter type is required");
        }
        if (!request.isConfirmed()) {
            throw new IllegalArgumentException("Bulk operation must be confirmed");
        }

        // 2. Check role - only ADMIN can perform bulk operations
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (!isAdmin) {
            throw new AccessDeniedException("Bulk operations are restricted to ADMIN role only.");
        }

        // 3. Determine target article IDs based on filter type
        List<Long> targetIds = new ArrayList<>();

        switch (request.getFilterType()) {
            case ALL:
                targetIds = newsRepository.findAllIds();
                break;
            case BY_TERM:
                if (request.getTermId() == null) {
                    throw new IllegalArgumentException("Term ID is required for BY_TERM filter");
                }
                targetIds = newsRepository.findIdsByTermId(request.getTermId());
                break;
            case BY_AUTHOR:
                if (request.getAuthorId() == null) {
                    throw new IllegalArgumentException("Author ID is required for BY_AUTHOR filter");
                }
                targetIds = newsRepository.findIdsByAuthorId(request.getAuthorId());
                break;
            case BY_IDS:
                if (request.getItemIds() == null || request.getItemIds().isEmpty()) {
                    return; // If no IDs provided, do nothing
                }
                targetIds = new ArrayList<>(request.getItemIds());
                break;
            default:
                throw new IllegalArgumentException("Unsupported filter type: " + request.getFilterType());
        }

        // 4. Execute action
        if (targetIds.isEmpty()) {
            return; // Nothing to do
        }

        switch (request.getAction()) {
            case DELETE:
                newsRepository.deleteAllById(targetIds);
                break;
            case UNPUBLISH:
                newsRepository.unpublishByIds(targetIds);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bulk action: " + request.getAction());
        }
    }
}
