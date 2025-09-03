package betterpedia.notes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_notes")
public class UserNote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String pageUrl;
    private String elementId;
    private Integer startOffset;
    private Integer endOffset;

    @Column(length = 1000)
    private String selectedText;
    @Column(length = 2000)
    private String noteText;
    private String color;

    private LocalDateTime createdAt = LocalDateTime.now();

    // need to write getters/setters ...
}