package com.example.phoebe.mapper;

import com.example.phoebe.entity.Permission;
import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.Term;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base mapper containing common mapping methods to reduce code duplication.
 * This interface provides reusable mapping utilities for collections and entity relationships.
 */
public interface BaseMapper {

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

    /**
     * Converts a Set of Role entities to a Set of their names.
     * Returns default EDITOR role if input is null or empty for safety.
     */
    @Named("rolesToNames")
    default Set<String> rolesToNames(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of("EDITOR");
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Converts a Set of Permission entities to a Set of their names.
     * Returns empty set if input is null.
     */
    @Named("permissionsToNames")
    default Set<String> permissionsToNames(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}