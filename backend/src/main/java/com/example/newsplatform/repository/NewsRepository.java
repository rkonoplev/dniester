package com.example.newsplatform.repository;

import com.example.newsplatform.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for accessing news data.
 */
public interface NewsRepository extends JpaRepository<News, Long> {

    // Search all news by title OR content (case-insensitive)
    @Query("""
        SELECT n FROM News n
        WHERE (:category IS NULL OR LOWER(n.category) = LOWER(:category))
        AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<News> searchAll(String search, String category, Pageable pageable);

    // Search only published news
    @Query("""
        SELECT n FROM News n
        WHERE n.published = true
        AND (:category IS NULL OR LOWER(n.category) = LOWER(:category))
        AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<News> searchPublished(String search, String category, Pageable pageable);

    // Find by ID only if published
    java.util.Optional<News> findByIdAndPublishedTrue(Long id);
}
