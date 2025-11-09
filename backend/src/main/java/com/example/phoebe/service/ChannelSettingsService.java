package com.example.phoebe.service;

import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.dto.response.ChannelSettingsDto;

/**
 * Service interface for managing channel settings.
 */
public interface ChannelSettingsService {

    /**
     * Get current channel settings.
     * @return Channel settings DTO
     */
    ChannelSettingsDto getSettings();

    /**
     * Update channel settings.
     * @param updateDto Update data
     * @return Updated channel settings DTO
     */
    ChannelSettingsDto updateSettings(ChannelSettingsUpdateDto updateDto);
}