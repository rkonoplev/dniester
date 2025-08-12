package com.example.newsplatform.repository;

import com.example.newsplatform.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing News entities.
 * Provides methods for searching and retrieving news with pagination.
 */
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Search news by keyword in title, teaser or content.
     *
     * @param keyword  the search term to look for
     * @param pageable pagination information
     * @return Page of News matching the keyword
     */
    @Query("""
        SELECT n FROM News n
        WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(n.teaser) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR n.content LIKE CONCAT('%', :keyword, '%')
        """)
    Page<News> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Find news by category.
     *
     * @param category the category to filter by
     * @param pageable pagination information
     * @return Page of News in the specified category
     */
    @Query("""
        SELECT n FROM News n
        WHERE n.category = :category
        """)
    Page<News> findByCategory(@Param("category") String category, Pageable pageable);

    /**
     * Search news by keyword and category.
     * If keyword is null or empty, only category filter is applied.
     *
     * @param keyword  the search term to look for (can be null or empty)
     * @param category the category to filter by
     * @param pageable pagination information
     * @return Page of News matching both keyword and category
     */
    @Query("""
        SELECT n FROM News n
        WHERE n.category = :category
          AND (:keyword IS NULL OR :keyword = '' OR (
               LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(n.teaser) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR n.content LIKE CONCAT('%', :keyword, '%')
          ))
        """)
    Page<News> searchByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable
    );
}