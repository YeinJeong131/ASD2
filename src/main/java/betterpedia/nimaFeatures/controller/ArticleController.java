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

    @GetMapping("/search/author")
    public List<String> searchByAuthor(@RequestParam("authorName") String authorName) {
        if (authorName == null || authorName.isEmpty()) return List.of();
        return articleRepository.findTitleByAuth(authorName);
    }
}
