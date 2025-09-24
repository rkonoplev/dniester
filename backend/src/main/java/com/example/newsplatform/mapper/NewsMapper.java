package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName")
    @Mapping(source = "terms", target = "termNames", qualifiedByName = "termsToNames")
    NewsDto toDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "terms", ignore = true)
    News toEntity(NewsCreateRequestDto createRequest);

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