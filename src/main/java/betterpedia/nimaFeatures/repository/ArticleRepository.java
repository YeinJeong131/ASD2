package betterpedia.nimaFeatures.repository;

import betterpedia.nimaFeatures.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("select a.title from Article a where lower(a.author) like lower(concat('%', :authorName, '%'))")
    List<String> findTitleByAuth(@Param("authorName") String authorName);
}
