package betterpedia.wiki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class documentService {

    @Autowired
    private documentRepository repository;

    public List<documentEntity> getAllDocuments() {
        return repository.findAll();
    }

    public Optional<documentEntity> getBySlug(String slug) {
        return repository.findBySlug(slug);
    }

    public documentEntity getBySlugOrDefault(String slug) {
        return repository.findBySlug(slug)
                .orElse(createDefaultDocument(slug));
    }

    private documentEntity createDefaultDocument(String slug) {
        documentEntity doc = new documentEntity();
        doc.setId(0L); // 임시 ID
        doc.setSlug(slug);
        doc.setTitle(slug.replace("-", " ").toUpperCase());
        doc.setCategory("General");
        doc.setContent("<h2>Document Not Found</h2><p>This document doesn't exist yet. You can create it!</p>");
        return doc;
    }

    // 초기 데이터 생성 (개발용)
    public void createSampleData() {
        if (repository.count() == 0) {
            repository.save(new documentEntity(
                    "Spring Framework",
                    "Framework",
                    "<h2>Overview</h2><p>Spring Framework is a comprehensive programming and configuration model for modern Java-based enterprise applications.</p>",
                    "spring-framework"
            ));

            repository.save(new documentEntity(
                    "Java Programming",
                    "Programming Language",
                    "<h2>What is Java?</h2><p>Java is a high-level, object-oriented programming language designed for portability and security.</p>",
                    "java-programming"
            ));

            repository.save(new documentEntity(
                    "Spring Boot",
                    "Framework",
                    "<h2>Introduction to Spring Boot</h2><p>Spring Boot makes it easy to create stand-alone, production-grade Spring applications.</p>",
                    "spring-boot"
            ));

            repository.save(new documentEntity(
                    "Database Design",
                    "Database",
                    "<h2>Principles of Database Design</h2><p>Good database design is essential for efficient and scalable applications.</p>",
                    "database-design"
            ));
        }
    }
}