package com.example.phoebe.entity;

import com.example.phoebe.validation.SafeHtml;
import com.example.phoebe.validation.ValidJsonArray;
import com.example.phoebe.validation.ValidUrl;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * Channel settings entity - singleton configuration for the CMS.
 * Contains site-wide settings like title, meta tags, and HTML snippets.
 */
@Entity
@Table(name = "channel_settings")
public class ChannelSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255, message = "Site title must not exceed 255 characters")
    @Column(name = "site_title", length = 255)
    private String siteTitle;

    @Size(max = 500, message = "Meta description must not exceed 500 characters")
    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Size(max = 500, message = "Meta keywords must not exceed 500 characters")
    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    @SafeHtml
    @Column(name = "header_html", columnDefinition = "TEXT")
    private String headerHtml;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @SafeHtml
    @Column(name = "footer_html", columnDefinition = "TEXT")
    private String footerHtml;

    @ValidJsonArray(message = "Main menu term IDs must be a valid JSON array")
    @Column(name = "main_menu_term_ids", columnDefinition = "TEXT")
    private String mainMenuTermIds;

    @Size(max = 255, message = "Site URL must not exceed 255 characters")
    @ValidUrl(message = "Site URL must be a valid HTTP/HTTPS URL")
    @Column(name = "site_url", length = 255)
    private String siteUrl;

    // Constructors
    public ChannelSettings() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteTitle() {
        return siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public String getHeaderHtml() {
        return headerHtml;
    }

    public void setHeaderHtml(String headerHtml) {
        this.headerHtml = headerHtml;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getFooterHtml() {
        return footerHtml;
    }

    public void setFooterHtml(String footerHtml) {
        this.footerHtml = footerHtml;
    }

    public String getMainMenuTermIds() {
        return mainMenuTermIds;
    }

    public void setMainMenuTermIds(String mainMenuTermIds) {
        this.mainMenuTermIds = mainMenuTermIds;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    @Override
    public String toString() {
        return "ChannelSettings{" +
                "id=" + id +
                ", siteTitle='" + siteTitle + '\'' +
                ", metaDescription='" + metaDescription + '\'' +
                '}';
    }
}