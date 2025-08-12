package com.example.newsplatform.repository;

import com.example.newsplatform.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("""
        SELECT n FROM News n
        WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(n.teaser) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<News> searchByKeyword(String keyword, Pageable pageable);

    @Query("""
        SELECT n FROM News n
        WHERE n.category = :category
    """)
    Page<News> findByCategory(String category, Pageable pageable);

    @Query("""
        SELECT n FROM News n
        WHERE n.category = :category
          AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(n.teaser) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<News> searchByKeywordAndCategory(String keyword, String category, Pageable pageable);
}