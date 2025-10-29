package com.example.phoebe.mapper;

import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.request.NewsUpdateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Term;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between News entity and its DTOs using MapStruct.
 * Handles mapping for responses, creation, and updates.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {

    /**
     * Maps a News entity to a NewsDto for API responses.
     * It correctly maps 'body' and 'teaser' as separate fields.
     */
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName")
    @Mapping(source = "terms", target = "termNames", qualifiedByName = "termsToNames")
    NewsDto toDto(News news);

    /**
     * Maps a NewsCreateRequestDto to a new News entity.
     * The 'content' from the DTO is mapped to the 'body' of the entity.
     * Auto-generated fields like id, timestamps, and author are ignored.
     */
    @Mapping(target = "body", source = "content")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teaser", source = "teaser")
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "terms", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    News toEntity(NewsCreateRequestDto createRequest);

    /**
     * Updates an existing News entity from a NewsUpdateRequestDto.
     * This allows for partial updates (PATCH-like behavior).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "body", source = "content")
    @Mapping(source = "isPublished", target = "published")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "terms", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(NewsUpdateRequestDto dto, @MappingTarget News entity);

    /**
     * Converts a Set of Term entities to a Set of their names.
     * Returns empty set if input is null.
     */
    @Named("termsToNames")
    default Set<String> termsToNames(Set<Term> terms) {
        if (terms == null) {
            return Set.of();
        }
        return terms.stream()
                .map(Term::getName)
                .collect(Collectors.toSet());
    }
}