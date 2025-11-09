package com.example.phoebe.service.impl;

import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.entity.ChannelSettings;
import com.example.phoebe.mapper.ChannelSettingsMapper;
import com.example.phoebe.repository.ChannelSettingsRepository;
import com.example.phoebe.service.ChannelSettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ChannelSettingsService.
 */
@Service
public class ChannelSettingsServiceImpl implements ChannelSettingsService {

    private final ChannelSettingsRepository repository;
    private final ChannelSettingsMapper mapper;

    public ChannelSettingsServiceImpl(ChannelSettingsRepository repository, ChannelSettingsMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelSettingsDto getSettings() {
        ChannelSettings settings = repository.findSingletonSettings()
                .orElseGet(this::createDefaultSettings);
        return mapper.toDto(settings);
    }

    @Override
    @Transactional
    public ChannelSettingsDto updateSettings(ChannelSettingsUpdateDto updateDto) {
        ChannelSettings settings = repository.findSingletonSettings()
                .orElseGet(this::createDefaultSettings);
        
        mapper.updateEntity(settings, updateDto);
        settings = repository.save(settings);
        
        return mapper.toDto(settings);
    }

    private ChannelSettings createDefaultSettings() {
        ChannelSettings settings = new ChannelSettings();
        settings.setSiteTitle("Phoebe CMS");
        settings.setMetaDescription("Modern headless CMS built with Spring Boot");
        settings.setMetaKeywords("cms, headless, spring boot, java");
        settings.setLogoUrl("/logo.png");
        settings.setMainMenuTermIds("[]");
        return repository.save(settings);
    }
}