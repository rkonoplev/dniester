package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * DTO for bulk operations in admin panel.
 */
public class BulkActionRequestDto {

    public enum ActionType {
        DELETE, UNPUBLISH
    }

    public enum FilterType {
        ALL, BY_TERM, BY_AUTHOR, BY_IDS
    }

    @NotNull(message = "Action type is required")
    private ActionType action;

    @NotNull(message = "Filter type is required")
    private FilterType filterType;

    private Long termId;
    private Long authorId;
    private Set<Long> itemIds;

    private boolean confirmed = false;

    // Getters and Setters
    public ActionType getAction() { return action; }
    public void setAction(ActionType action) { this.action = action; }

    public FilterType getFilterType() { return filterType; }
    public void setFilterType(FilterType filterType) { this.filterType = filterType; }

    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Set<Long> getItemIds() { return itemIds; }
    public void setItemIds(Set<Long> itemIds) { this.itemIds = itemIds; }

    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}