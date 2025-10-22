package betterpedia.nimaFeatures.repository;

import betterpedia.nimaFeatures.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("""
    select a.title from Article a where (:authorName is null or trim(:authorName) = '' or lower(a.author) like lower(concat('%', :authorName, '%')))
      and (:body       is null or trim(:body)       = '' or lower(a.body)   like lower(concat('%', :body, '%')))
      and (:date       is null or a.publish_date = :date) order by a.publish_date desc
    """)
    List<String> searchTitles(@Param("authorName") String authorName, @Param("body") String body, @Param("date") java.time.LocalDate date);
}
