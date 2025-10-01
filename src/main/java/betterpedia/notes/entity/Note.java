package betterpedia.notes.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import betterpedia.user.entity.User;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String pageUrl;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String highlightedText;

    @Column(columnDefinition = "TEXT")
    private String noteContent;

    @Column(columnDefinition = "TEXT") // position data stored as JSON string
    private String position;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime updatedDate;

    // highlight colour (not sure if I use this)
    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'yellow'")
    private String highlightColour;

    //--
    public Note() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.highlightColour = "yellow";
    }

    public Note(User user, String pageUrl, String highlightedText, String noteContent, String position) {
        this();
        this.user = user;
        this.pageUrl = pageUrl;
        this.highlightedText = highlightedText;
        this.noteContent = noteContent;
        this.position = position;
    }

    // --


    public Long getNoteId() {
        return noteId;
    }

    public User getUser() {
        return user;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getHighlightedText() {
        return highlightedText;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public String getPosition() {
        return position;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public String getHighlightColour() {
        return highlightColour;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public void setHighlightedText(String highlightedText) {
        this.highlightedText = highlightedText;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setHighlightColour(String highlightColour) {
        this.highlightColour = highlightColour;
    }

    // helper method to update timestamp
    @PreUpdate
    public void updateTimestamp() {
        this.updatedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", user=" + (user != null ? user.getId() : "null") +
                ", pageUrl='" + pageUrl + '\'' +
                ", highlightedText='" + highlightedText + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", highlightColour=" + highlightColour + '\'' +
                '}';
    }

}
