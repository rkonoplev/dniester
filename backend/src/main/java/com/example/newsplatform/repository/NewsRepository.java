package com.example.newsplatform.repository;

import com.example.newsplatform.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link News} entities.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // === Read Operations ===

    Page<News> findByPublished(boolean published, Pageable pageable);

    Optional<News> findByIdAndPublished(Long id, boolean published);

    /**
     * Find news by term ID and publication status.
     */
    Page<News> findByTermsIdAndPublished(Long termId, boolean published, Pageable pageable);

    /**
     * Find news by multiple term IDs and publication status.
     */
    Page<News> findByTermsIdInAndPublished(List<Long> termIds, boolean published, Pageable pageable);

    Page<News> findByAuthorId(Long authorId, Pageable pageable);

    boolean existsByIdAndAuthorId(Long id, Long authorId);


    // === Bulk Operation Helpers ===

    @Query("SELECT n.id FROM News n")
    List<Long> findAllIds();

    @Query("SELECT DISTINCT n.id FROM News n JOIN n.terms t WHERE t.id = :termId")
    List<Long> findIdsByTermId(@Param("termId") Long termId);

    @Query("SELECT n.id FROM News n WHERE n.author.id = :authorId")
    List<Long> findIdsByAuthorId(@Param("authorId") Long authorId);

    @Modifying
    @Transactional
    @Query("UPDATE News n SET n.published = false WHERE n.id IN :ids")
    void unpublishByIds(@Param("ids") List<Long> ids);
}