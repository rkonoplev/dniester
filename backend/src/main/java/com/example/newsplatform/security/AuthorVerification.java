package com.example.newsplatform.security;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.NewsRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * A component responsible for verifying authorship and access rights for news articles.
 * This logic is used to enforce security rules, ensuring that users (like EDITORS)
 * can only modify their own content, while ADMINs have unrestricted access.
 */
@Component
public class AuthorVerification {

    private final NewsRepository newsRepository;

    public AuthorVerification(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * Checks if a user has access to a specific news article.
     * Admins have access to all articles. Other users (e.g., EDITORS) only have
     * access if they are the author.
     *
     * @param user   The user attempting to access the content.
     * @param newsId The ID of the news article.
     * @return true if the user has access, false otherwise.
     */
    @Transactional(readOnly = true)
    public boolean hasAccessToNews(User user, Long newsId) {
        if (user == null || newsId == null) {
            return false;
        }

        // Admins can access any news
        if (user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
            return true;
        }

        // Other users can only access their own news
        News news = newsRepository.findById(newsId).orElse(null);
        return isAuthor(user, news);
    }

    /**
     * A simple utility method to check if a given user is the author of a news article.
     *
     * @param user The user to check.
     * @param news The news article.
     * @return true if the user is the author, false otherwise.
     */
    public boolean isAuthor(User user, News news) {
        if (user == null || news == null || news.getAuthor() == null || user.getId() == null) {
            return false;
        }
        return user.getId().equals(news.getAuthor().getId());
    }
}
