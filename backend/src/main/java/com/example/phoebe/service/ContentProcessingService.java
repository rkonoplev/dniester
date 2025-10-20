package com.example.phoebe.service;

import com.example.phoebe.validation.SafeHtmlValidator;
import org.springframework.stereotype.Service;

/**
 * Service for processing and sanitizing content.
 * Handles YouTube link conversion and HTML sanitization.
 * This is a utility service and does not interact with the database, so it is not transactional.
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
