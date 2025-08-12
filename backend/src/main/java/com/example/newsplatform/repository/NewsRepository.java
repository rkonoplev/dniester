package com.example.newsplatform.repository;

import com.example.newsplatform.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("""
        SELECT n FROM News n
        WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(n.teaser) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR n.content LIKE CONCAT('%', :keyword, '%')
    """)
    Page<News> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
        SELECT n FROM News n
        WHERE n.category = :category
    """)
    Page<News> findByCategory(@Param("category") String category, Pageable pageable);

    @Query("""
        SELECT n FROM News n
        WHERE n.category = :category
          AND (:keyword IS NULL OR :keyword = '' OR (
               LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(n.teaser) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR n.content LIKE CONCAT('%', :keyword, '%')
          ))
    """)
    Page<News> searchByKeywordAndCategory(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);
}