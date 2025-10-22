package betterpedia.nimaFeatures.services;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import betterpedia.nimaFeatures.model.Article;

import betterpedia.nimaFeatures.repository.ArticleRepository;
import org.springframework.stereotype.Service;


@Service
public class DownloadService {

    private final ArticleRepository articleRepository;
    public DownloadService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public byte[] make_txt_file(Long id){
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) return null;
        String title = article.getTitle();
        String author = article.getAuthor();
        LocalDate date = article.getPublishdate();
        String body = article.getBody();

        String result =
                "Title: " + title + "\n" + "Author: " + author + "\n" + "Date: " + date + "\n" + "Body: " + body + "\n"+
                "Downloaded from BetterPedia. Do not forget to reference it. Cheers :)";
        return result.getBytes();
    }

}
