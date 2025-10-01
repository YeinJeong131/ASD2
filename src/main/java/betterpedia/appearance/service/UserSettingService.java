package betterpedia.appearance.service;

import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.repository.UserSettingsRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class UserSettingService {

    @Autowired
    private UserSettingsRepository repository;

    @Autowired
    private UserRepository userRepository;

    // 설정 조회 - 없으면 기본값만 반환 (DB 저장 안 함)
    public UserSettings getUserSettings(Long userId) {
        return repository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettingsObject(userId));
    }

    // 설정 저장
    public UserSettings saveUserSettings(Long userId, UserSettings settings) throws IllegalArgumentException {
        validateSettings(settings);

        // User 객체 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 기존 설정이 있으면 업데이트, 없으면 새로 생성
        if (repository.existsByUserId(userId)) {
            UserSettings existing = repository.findByUserId(userId).get();
            updateExistingSettings(existing, settings);
            return repository.save(existing);
        } else {
            // 새 설정에 User 객체 설정
            settings.setUser(user);
            return repository.save(settings);
        }
    }

    // 기본값 객체만 생성 (DB 저장 안 함)
    private UserSettings createDefaultSettingsObject(Long userId) {
        UserSettings defaultSettings = new UserSettings();
        // User 객체를 설정하지 않음 - DB에 저장 안 하므로
        return defaultSettings;
    }

    private void updateExistingSettings(UserSettings existing, UserSettings newSettings) {
        if (newSettings.getDarkMode() != null) {
            existing.setDarkMode(newSettings.getDarkMode());
        }
        if (newSettings.getFontSize() != null) {
            existing.setFontSize(newSettings.getFontSize());
        }
        if (newSettings.getDateFormat() != null) {
            existing.setDateFormat(newSettings.getDateFormat());
        }
        if (newSettings.getTimeOffset() != null) {
            existing.setTimeOffset(newSettings.getTimeOffset());
        }
        if (newSettings.getPageWidth() != null) {
            existing.setPageWidth(newSettings.getPageWidth());
        }
        if (newSettings.getFontStyle() != null) {
            existing.setFontStyle(newSettings.getFontStyle());
        }
        if (newSettings.getLineSpacing() != null) {
            existing.setLineSpacing(newSettings.getLineSpacing());
        }
    }

    // Validation
    private static final List<String> VALID_FONT_SIZES = Arrays.asList("small", "medium", "large", "extra-large");
    private static final List<String> VALID_DATE_FORMATS = Arrays.asList("DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD");
    private static final List<String> VALID_PAGE_WIDTHS = Arrays.asList("fixed", "wide");
    private static final List<String> VALID_FONT_STYLES = Arrays.asList("arial", "serif", "sans-serif", "monospace");
    private static final List<String> VALID_LINE_SPACINGS = Arrays.asList("compact", "normal", "relaxed");

    private void validateSettings(UserSettings settings) throws IllegalArgumentException {
        if (settings.getFontSize() != null && !VALID_FONT_SIZES.contains(settings.getFontSize())) {
            throw new IllegalArgumentException("Invalid font size");
        }
        if (settings.getDateFormat() != null && !VALID_DATE_FORMATS.contains(settings.getDateFormat())) {
            throw new IllegalArgumentException("Invalid date format");
        }
        if (settings.getPageWidth() != null && !VALID_PAGE_WIDTHS.contains(settings.getPageWidth())) {
            throw new IllegalArgumentException("Invalid page width");
        }
        if (settings.getFontStyle() != null && !VALID_FONT_STYLES.contains(settings.getFontStyle())) {
            throw new IllegalArgumentException("Invalid font style");
        }
        if (settings.getLineSpacing() != null && !VALID_LINE_SPACINGS.contains(settings.getLineSpacing())) {
            throw new IllegalArgumentException("Invalid line spacing");
        }
        if (settings.getTimeOffset() != null && (settings.getTimeOffset() < -12 || settings.getTimeOffset() > 14)) {
            throw new IllegalArgumentException("Time offset must be between -12 and +14");
        }
    }
}