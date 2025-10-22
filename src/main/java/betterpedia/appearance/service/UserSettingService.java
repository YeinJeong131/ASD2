package betterpedia.appearance.service;

import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.repository.UserSettingsRepository;

import java.util.Optional;

@Service
public class UserSettingService {

    @Autowired
    private UserSettingsRepository repository;

    @Autowired
    private UserRepository userRepository;

    public UserSettings getUserSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return repository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
    }

    public UserSettings saveUserSettings(Long userId, UserSettings settings) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<UserSettings> existing = repository.findByUser(user);

        if (existing.isPresent()) {
            UserSettings existingSettings = existing.get();
            updateSettings(existingSettings, settings);
            return repository.save(existingSettings);
        } else {
            settings.setUser(user);
            return repository.save(settings);
        }
    }

    private UserSettings createDefaultSettings(User user) {
        UserSettings defaultSettings = new UserSettings();
        defaultSettings.setUser(user);
        return defaultSettings;
    }

    private void updateSettings(UserSettings existing, UserSettings newSettings) {
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
}