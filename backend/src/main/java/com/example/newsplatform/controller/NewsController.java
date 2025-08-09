package com.example.newsplatform.controller;

import org.springframework.web.bind.annotation.*;
import com.example.newsplatform.model.News;
import com.example.newsplatform.repository.NewsRepository;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsRepository repository;

    public NewsController(NewsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<News> getAllNews() {
        return repository.findAll();
    }

    @PostMapping
    public News createNews(@RequestBody News news) {
        return repository.save(news);
    }
}
