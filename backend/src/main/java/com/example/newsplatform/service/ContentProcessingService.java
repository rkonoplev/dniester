package com.example.newsplatform.service;

import com.example.newsplatform.validation.SafeHtmlValidator;
import org.springframework.stereotype.Service;

/**
 * Service for processing and sanitizing content.
 * Handles YouTube link conversion and HTML sanitization.
 */
@Service
public class ContentProcessingService {

    /**
     * Processes content by converting YouTube links to embed code
     * and ensuring HTML safety.
     */
    public String processContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return content;
        }
        
        return SafeHtmlValidator.convertYouTubeLinks(content);
    }
}