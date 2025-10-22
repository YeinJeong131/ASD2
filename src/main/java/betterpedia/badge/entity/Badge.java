package betterpedia.badge.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import betterpedia.user.entity.User;

@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String level; // BRONZE, SILVER, GOLD, PLATINUM

    @Column(nullable = false)
    private int contributionCount = 0;

    @Column(nullable = false)
    private LocalDateTime awardedDate;

    @Column(nullable = false)
    private LocalDateTime updatedDate;

    // Constructors
    public Badge() {
        this.awardedDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
        this.level = "BRONZE";
    }

    public Badge(User user, String level, int contributionCount) {
        this();
        this.user = user;
        this.level = level;
        this.contributionCount = contributionCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getContributionCount() {
        return contributionCount;
    }

    public void setContributionCount(int contributionCount) {
        this.contributionCount = contributionCount;
    }

    public LocalDateTime getAwardedDate() {
        return awardedDate;
    }

    public void setAwardedDate(LocalDateTime awardedDate) {
        this.awardedDate = awardedDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    // Helper method to update timestamp
    @PreUpdate
    public void updateTimestamp() {
        this.updatedDate = LocalDateTime.now();
    }

    // Helper method to calculate badge level based on contributions
    public void updateBadgeLevel() {
        if (contributionCount >= 100) {
            this.level = "PLATINUM";
        } else if (contributionCount >= 50) {
            this.level = "GOLD";
        } else if (contributionCount >= 20) {
            this.level = "SILVER";
        } else {
            this.level = "BRONZE";
        }
        this.updatedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : "null") +
                ", level='" + level + '\'' +
                ", contributionCount=" + contributionCount +
                ", awardedDate=" + awardedDate +
                '}';
    }
}

