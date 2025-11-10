package com.example.phoebe.dto.response;

/**
 * DTO for channel settings API responses.
 */
public record ChannelSettingsDto(
        String siteTitle,
        String metaDescription,
        String metaKeywords,
        String headerHtml,
        String logoUrl,
        String footerHtml,
        String mainMenuTermIds,
        String siteUrl
) {}