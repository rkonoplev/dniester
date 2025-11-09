package com.example.phoebe.controller;

import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.service.ChannelSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public controller for channel settings.
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Channel Settings", description = "Public access to channel configuration")
public class PublicChannelSettingsController {

    private final ChannelSettingsService channelSettingsService;

    public PublicChannelSettingsController(ChannelSettingsService channelSettingsService) {
        this.channelSettingsService = channelSettingsService;
    }

    @GetMapping("/channel-settings")
    @Operation(summary = "Get current channel settings", 
               description = "Returns site-wide configuration like title, meta tags, and HTML snippets")
    public ResponseEntity<ChannelSettingsDto> getChannelSettings() {
        ChannelSettingsDto settings = channelSettingsService.getSettings();
        return ResponseEntity.ok(settings);
    }
}