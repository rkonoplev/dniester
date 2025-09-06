package com.example.newsplatform.repository;

import com.example.newsplatform.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository for accessing News (content table).
 */
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Search all news (published or not) by text and optional category.
     */
    @Query("""
        SELECT DISTINCT n FROM News n
        LEFT JOIN n.terms t
        WHERE (:category IS NULL OR LOWER(t.name) = LOWER(:category))
          AND (:search IS NULL
               OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(n.body) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<News> searchAll(String search, String category, Pageable pageable);

    /**
     * Search only published news by text and category.
     */
    @Query("""
        SELECT DISTINCT n FROM News n
        LEFT JOIN n.terms t
        WHERE n.published = true
          AND (:category IS NULL OR LOWER(t.name) = LOWER(:category))
          AND (:search IS NULL
               OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(n.body) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<News> searchPublished(String search, String category, Pageable pageable);

    /**
     * Find by id only if published.
     */
    Optional<News> findByIdAndPublishedTrue(Long id);

    /**
     * Find published news by specific term ID with pagination.
     */
    @Query("""
        SELECT DISTINCT n FROM News n
        JOIN n.terms t
        WHERE n.published = true AND t.id = :termId
        ORDER BY n.publicationDate DESC
        """)
    Page<News> findPublishedByTermId(Long termId, Pageable pageable);

    /**
     * Find published news by multiple term IDs with pagination.
     */
    @Query("""
        SELECT DISTINCT n FROM News n
        JOIN n.terms t
        WHERE n.published = true AND t.id IN :termIds
        ORDER BY n.publicationDate DESC
        """)
    Page<News> findPublishedByTermIds(java.util.List<Long> termIds, Pageable pageable);
}