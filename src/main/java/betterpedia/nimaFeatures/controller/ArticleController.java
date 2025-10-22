package betterpedia.nimaFeatures.controller;

import betterpedia.nimaFeatures.model.Article;

import betterpedia.nimaFeatures.repository.ArticleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
@CrossOrigin(origins = {"http://localhost:5500","http://127.0.0.1:5500","http://localhost:3000","http://localhost:8080","*"})
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        return articleRepository.save(article);
    }

    @GetMapping("/{id}")
    public Article getOne(@PathVariable Long id) {
        return articleRepository.findById(id).orElseThrow();
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam(required = false) String authorName, @RequestParam(required = false) String body, @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        authorName = (authorName == null || authorName.isBlank()) ? null : authorName.trim();
        body       = (body       == null || body.isBlank())       ? null : body.trim();
        return articleRepository.searchTitles(authorName, body, date);
    }
}
