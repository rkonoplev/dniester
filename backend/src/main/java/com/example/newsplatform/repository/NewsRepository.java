package com.example.newsplatform.repository;

import com.example.newsplatform.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for News entity with custom search queries.
 */
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Search all news (published or not) by text and optional category.
     */
    @Query("""
            SELECT n FROM News n LEFT JOIN n.terms t WHERE
            (:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%'))) AND
            (:category IS NULL OR t.name = :category)
            """)
    Page<News> searchAll(@Param("search") String search, @Param("category") String category, Pageable pageable);

    /**
     * Search only published news by text and optional category.
     */
    @Query("""
            SELECT n FROM News n LEFT JOIN n.terms t WHERE n.published = true AND
            (:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :search, '%'))) AND
            (:category IS NULL OR t.name = :category)
            """)
    Page<News> searchPublished(@Param("search") String search, @Param("category") String category, Pageable pageable);

    /**
     * Find by id only if published.
     */
    @EntityGraph(attributePaths = {"author", "terms"})
    Optional<News> findByIdAndPublished(Long id, boolean published);

    /**
     * Find published news by specific term ID with pagination.
     */
    @EntityGraph(attributePaths = {"author", "terms"})
    Page<News> findByTerms_IdAndPublished(Long termId, boolean published, Pageable pageable);

    /**
     * Find published news by multiple term IDs with pagination.
     */
    @EntityGraph(attributePaths = {"author", "terms"})
    Page<News> findByTerms_IdInAndPublished(List<Long> termIds, boolean published, Pageable pageable);

    /**
     * Find news by author ID (for EDITOR role).
     */
    Page<News> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * Get all article IDs for bulk operations.
     */
    @Query("SELECT n.id FROM News n")
    List<Long> findAllIds();

    /**
     * Get article IDs by term ID for bulk operations.
     */
    @Query("SELECT DISTINCT n.id FROM News n JOIN n.terms t WHERE t.id = :termId")
    List<Long> findIdsByTermId(@Param("termId") Long termId);

    /**
     * Get article IDs by author ID for bulk operations.
     */
    @Query("SELECT n.id FROM News n WHERE n.author.id = :authorId")
    List<Long> findIdsByAuthorId(@Param("authorId") Long authorId);

    /**
     * Bulk unpublish articles by IDs.
     */
    @Modifying
    @Query("UPDATE News n SET n.published = false WHERE n.id IN :ids")
    void unpublishByIds(@Param("ids") List<Long> ids);
}