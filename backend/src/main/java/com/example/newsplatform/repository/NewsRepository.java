package com.example.newsplatform.repository;

import com.example.newsplatform.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    // Here you can add custom queries if needed
}
