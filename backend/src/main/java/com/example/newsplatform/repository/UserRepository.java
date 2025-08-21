package com.example.newsplatform.repository;

import com.example.newsplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing User entities.
 * Provides CRUD operations and integration with JPA/Hibernate.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
