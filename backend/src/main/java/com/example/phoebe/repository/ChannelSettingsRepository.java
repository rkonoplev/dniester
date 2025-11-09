package com.example.phoebe.repository;

import com.example.phoebe.entity.ChannelSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ChannelSettings entity.
 * Since this is a singleton entity, we provide methods to get the single instance.
 */
@Repository
public interface ChannelSettingsRepository extends JpaRepository<ChannelSettings, Long> {

    /**
     * Get the singleton channel settings instance.
     * @return Optional containing the settings if they exist
     */
    @Query("SELECT cs FROM ChannelSettings cs ORDER BY cs.id ASC LIMIT 1")
    Optional<ChannelSettings> findSingletonSettings();
}