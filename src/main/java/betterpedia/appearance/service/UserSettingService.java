package betterpedia.appearance.service;

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

    // bring user settings (if there's no setting generate default settings)
    public UserSettings getUserSettings(Long userId) {
        //check if there is settings
        return repository.findByUserId(userId).orElse(createDefaultSettings(userId));
    }

    // store user settings
    public UserSettings saveUserSettings(UserSettings settings) throws IllegalArgumentException{

        // before saving user settings, check validation first
        validateSettings(settings);

        // if there's original setting, update. if no generate new settings
        if (repository.existsByUserId(settings.getUserId())) {
            UserSettings existing = repository.findByUserId(settings.getUserId()).get();
            updateExistingSettings(existing, settings);
            return repository.save(existing);
        } else {
            return repository.save(settings);
        }
    }

    // generate default settings
    private UserSettings createDefaultSettings(Long userId) {
        UserSettings defaultSettings = new UserSettings(userId);
        return repository.save(defaultSettings);
    }

    // update original settings
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

    // for enhanced service with validation
    // - valid values for validation
    private static final List<String> VALID_FONT_SIZES = Arrays.asList("small", "medium", "large", "extra-large");
    private static final List<String> VALID_DATE_FORMATS = Arrays.asList("DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD");
    private static final List<String> VALID_PAGE_WIDTHS = Arrays.asList("fixed", "wide");
    private static final List<String> VALID_FONT_STYLES = Arrays.asList("arial", "serif", "sans-serif", "monospace");
    private static final List<String> VALID_LINE_SPACINGS = Arrays.asList("compact", "normal", "relaxed");

    private void validateSettings(UserSettings settings) throws IllegalArgumentException {
        if (settings.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (settings.getFontSize() != null && !VALID_FONT_SIZES.contains(settings.getFontSize())) {
            throw new IllegalArgumentException("Font size must be one of " + VALID_FONT_SIZES + "\n" + "Invalid font size: " + settings.getFontSize());
        }
        if (settings.getDateFormat() != null && !VALID_DATE_FORMATS.contains(settings.getDateFormat())) {
            throw new IllegalArgumentException("DateFormat must be one of " + VALID_DATE_FORMATS + "\n" + "Invalid date format: " + settings.getDateFormat());
        }
        if (settings.getPageWidth() != null && !VALID_PAGE_WIDTHS.contains(settings.getPageWidth())) {
            throw new IllegalArgumentException("Page widths must be one of " + VALID_PAGE_WIDTHS + "\n" + "Invalid page width: " + settings.getPageWidth());
        }
        if (settings.getFontStyle() != null && !VALID_FONT_STYLES.contains(settings.getFontStyle())) {
            throw new IllegalArgumentException("Font styles must be one of" + VALID_FONT_STYLES + "\n" + "Invalid font style: " + settings.getFontStyle());
        }
        if (settings.getLineSpacing() != null && !VALID_LINE_SPACINGS.contains(settings.getLineSpacing())) {
            throw new IllegalArgumentException("line spacing must be one of " + VALID_LINE_SPACINGS + "\n" + "Invalid line spacing: " + settings.getLineSpacing());
        }
        if (settings.getTimeOffset() != null &&  (settings.getTimeOffset() < -12 || settings.getTimeOffset() > 14)) {
            throw new IllegalArgumentException("Time offset (hours) must be between -12 and +14. Invalid: " + settings.getTimeOffset());
        }
    }
}
