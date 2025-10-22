package betterpedia.nimaFeatures.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "article")
public class Article{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "longtext") private String body;
    private String author;
    private String tags;
    @JsonProperty("publishDate")
    @Column(name = "publish_date")
    private LocalDate publish_date;

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    public String getBody(){return body;}
    public void setBody(String body){this.body = body;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author = author;}
    public String getTags(){return tags;}
    public void setTags(String tags){this.tags = tags;}
    public LocalDate getPublishdate(){return publish_date;}
    public void setPublishdate(LocalDate publish_date){this.publish_date = publish_date;}



}
