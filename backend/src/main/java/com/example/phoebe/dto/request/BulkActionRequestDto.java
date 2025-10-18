package com.example.phoebe.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * DTO for performing bulk operations on news articles.
 * Allows actions like DELETE or UNPUBLISH on a set of articles
 * selected by various filters.
 */
public class BulkActionRequestDto {

    public enum ActionType {
        DELETE,
        UNPUBLISH
    }

    public enum FilterType {
        BY_IDS,
        BY_TERM,
        BY_AUTHOR,
        ALL
    }

    @NotNull(message = "Action type must be specified")
    private ActionType action;

    @NotNull(message = "Filter type must be specified")
    private FilterType filterType;

    // Used when filterType is BY_IDS
    private Set<Long> itemIds;

    // Used when filterType is BY_TERM
    private Long termId;

    // Used when filterType is BY_AUTHOR
    private Long authorId;

    @AssertTrue(message = "Bulk operation must be confirmed")
    private boolean confirmed;

    // Getters and Setters

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public Set<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * Represents the result of a bulk action, containing the number of affected items.
     */
    public static class BulkActionResult {
        private final int affectedCount;

        public BulkActionResult(int affectedCount) {
            this.affectedCount = affectedCount;
        }

        public int getAffectedCount() {
            return affectedCount;
        }
    }
}