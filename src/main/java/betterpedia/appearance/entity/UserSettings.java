package betterpedia.appearance.entity;

import jakarta.persistence.*;
import betterpedia.user.entity.User;


@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "dark_mode")
    private boolean darkMode = false;

    @Column(name = "font_size")
    private String fontSize = "medium";

    @Column(name = "date_format")
    private String dateFormat = "DD/MM/YYYY";

    @Column(name = "time_offset")
    private Integer timeOffset = 0;

    @Column(name = "page_width")
    private String pageWidth = "fixed";

    @Column(name = "font_style")
    private String fontStyle = "arial";

    @Column(name = "line_spacing")
    private String lineSpacing = "normal";

    public UserSettings() {}

    public UserSettings(User user) {
        this.user = user;
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public String getFontSize() {
        return fontSize;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public Integer getTimeOffset() {
        return timeOffset;
    }

    public String getPageWidth() {
        return pageWidth;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getLineSpacing() {
        return lineSpacing;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setTimeOffset(Integer timeOffset) {
        this.timeOffset = timeOffset;
    }

    public void setPageWidth(String pageWidth) {
        this.pageWidth = pageWidth;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public void setLineSpacing(String lineSpacing) {
        this.lineSpacing = lineSpacing;
    }
}
