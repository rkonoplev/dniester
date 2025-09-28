package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing news articles.
 * Defines the contract for all news-related business operations, including
 * public-facing queries and administrative actions.
 */
public interface NewsService {

    // === Public, Read-Only Methods ===

    /**
     * Finds a paginated list of all published news articles.
     *
     * @param pageable Pagination information.
     * @return A page of published {@link NewsDto}.
     */
    Page<NewsDto> findAllPublished(Pageable pageable);

    /**
     * Finds a single published news article by its ID.
     *
     * @param id The ID of the news article to find.
     * @return The found {@link NewsDto}.
     * @throws com.example.newsplatform.exception.ResourceNotFoundException if the article is not found or not published
     */
    NewsDto findPublishedById(Long id);

    /**
     * Finds all published news articles associated with a single term ID.
     *
     * @param termId   The ID of the term.
     * @param pageable Pagination information.
     * @return A page of published {@link NewsDto}.
     */
    Page<NewsDto> findByTermId(Long termId, Pageable pageable);

    /**
     * Finds all published news articles associated with a list of term IDs.
     *
     * @param termIds  A list of term IDs.
     * @param pageable Pagination information.
     * @return A page of published {@link NewsDto}.
     */
    Page<NewsDto> findByTermIds(List<Long> termIds, Pageable pageable);


    // === Admin/Authenticated Methods ===

    /**
     * Finds a paginated list of news articles based on the user's role.
     * - ADMINs see all articles.
     * - EDITORs see only their own articles.
     *
     * @param pageable       Pagination information.
     * @param authentication The current user's authentication object for
     *                       role-based filtering.
     * @return A page of {@link NewsDto}.
     */
    Page<NewsDto> findAllForUser(Pageable pageable, Authentication authentication);

    /**
     * Finds a single news article by ID, respecting ownership rules.
     *
     * @param id             The ID of the news article.
     * @param authentication The current user's authentication object.
     * @return The found {@link NewsDto}.
     */
    NewsDto findById(Long id, Authentication authentication);

    /**
     * Creates a new news article.
     *
     * @param request        The DTO containing the data for the new article.
     * @param authentication The current user's authentication object, used to set the author.
     * @return The created {@link NewsDto}.
     */
    NewsDto create(NewsCreateRequestDto request, Authentication authentication);

    /**
     * Updates an existing news article.
     *
     * @param id             The ID of the article to update.
     * @param request        The DTO with the updated data.
     * @param authentication The current user's authentication object for authorization.
     * @return The updated {@link NewsDto}.
     */
    NewsDto update(Long id, NewsUpdateRequestDto request, Authentication authentication);

    /**
     * Deletes a news article.
     *
     * @param id             The ID of the article to delete.
     * @param authentication The current user's authentication object for authorization.
     */
    void delete(Long id, Authentication authentication);


    // === Bulk Operations ===

    /**
     * Performs a bulk operation (e.g., delete, unpublish) on a set of news articles.
     *
     * @param request        The DTO defining the action and target articles.
     * @param authentication The current user's authentication object (must be ADMIN).
     * @return A result object indicating the number of affected items
     */
    BulkActionRequestDto.BulkActionResult performBulkAction(BulkActionRequestDto request, Authentication authentication);


    // === Authorization Helper Methods (for internal use in @PreAuthorize) ===

    boolean canAccessNews(Long newsId, Authentication authentication);

    boolean isAuthor(Long newsId, Authentication authentication);

    boolean hasAdminRole(Authentication authentication);

    boolean hasEditorRole(Authentication authentication);
}