package com.example.phoebe.dto.request;

import com.example.phoebe.validation.SafeHtml;
import com.example.phoebe.validation.ValidJsonArray;
import com.example.phoebe.validation.ValidUrl;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating channel settings.
 */
public record ChannelSettingsUpdateDto(
        @Size(max = 255, message = "Site title must not exceed 255 characters")
        String siteTitle,

        @Size(max = 500, message = "Meta description must not exceed 500 characters")
        String metaDescription,

        @Size(max = 500, message = "Meta keywords must not exceed 500 characters")
        String metaKeywords,

        @SafeHtml
        String headerHtml,

        @Size(max = 500, message = "Logo URL must not exceed 500 characters")
        String logoUrl,

        @SafeHtml
        String footerHtml,

        @ValidJsonArray(message = "Main menu term IDs must be a valid JSON array")
        String mainMenuTermIds,

        @Size(max = 255, message = "Site URL must not exceed 255 characters")
        @ValidUrl(message = "Site URL must be a valid HTTP/HTTPS URL")
        String siteUrl
) {}