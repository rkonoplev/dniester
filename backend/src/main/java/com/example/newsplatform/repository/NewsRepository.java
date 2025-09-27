package com.example.newsplatform.repository;

import com.example.newsplatform.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {

    // Public queries (published only)
    Page<News> findByPublishedTrue(Pageable pageable);
    Optional<News> findByIdAndPublishedTrue(Long id);

    @Query("SELECT n FROM News n JOIN n.terms t WHERE t.id = :termId AND n.published = true")
    Page<News> findByTermIdAndPublishedTrue(@Param("termId") Long termId, Pageable pageable);

    @Query("SELECT n FROM News n JOIN n.terms t WHERE t.id IN :termIds AND n.published = true")
    Page<News> findByTermIdsAndPublishedTrue(@Param("termIds") List<Long> termIds, Pageable pageable);

    // Default implementation for string termIds (comma-separated)
    default Page<News> findByTermIdsAndPublishedTrue(String termIds, Pageable pageable) {
        List<Long> termIdList = List.of(termIds.split(",")).stream()
                .map(String::trim)
                .map(Long::valueOf)
                .toList();
        return findByTermIdsAndPublishedTrue(termIdList, pageable);
    }

    // Admin queries (all news, with author filtering)
    Page<News> findByAuthorId(Long authorId, Pageable pageable);

    // Authorization queries
    boolean existsByIdAndAuthorId(Long id, Long authorId);

    // Bulk operations
    @Query("SELECT n.id FROM News n")
    List<Long> findAllIds();

    @Query("SELECT n.id FROM News n WHERE n.author.id = :authorId")
    List<Long> findIdsByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT n.id FROM News n JOIN n.terms t WHERE t.id = :termId")
    List<Long> findIdsByTermId(@Param("termId") Long termId);

    @Modifying
    @Query("UPDATE News n SET n.published = false WHERE n.id IN :ids")
    void unpublishByIds(@Param("ids") List<Long> ids);
}