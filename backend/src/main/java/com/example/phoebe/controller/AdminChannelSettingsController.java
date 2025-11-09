package com.example.phoebe.controller;

import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.service.ChannelSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller for channel settings management.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Channel Settings", description = "Channel settings management (Admin only)")
public class AdminChannelSettingsController {

    private final ChannelSettingsService channelSettingsService;

    public AdminChannelSettingsController(ChannelSettingsService channelSettingsService) {
        this.channelSettingsService = channelSettingsService;
    }

    @GetMapping("/channel-settings")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get channel settings for admin", 
               description = "Returns current channel settings for administrative editing")
    public ResponseEntity<ChannelSettingsDto> getChannelSettings() {
        ChannelSettingsDto settings = channelSettingsService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/channel-settings")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update channel settings", 
               description = "Updates site-wide configuration (Admin only)")
    public ResponseEntity<ChannelSettingsDto> updateChannelSettings(
            @Valid @RequestBody ChannelSettingsUpdateDto updateDto) {
        ChannelSettingsDto updatedSettings = channelSettingsService.updateSettings(updateDto);
        return ResponseEntity.ok(updatedSettings);
    }
}