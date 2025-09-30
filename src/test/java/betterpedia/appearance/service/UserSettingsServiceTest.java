package betterpedia.appearance.service;

import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserSettings Service Tests")
class UserSettingServiceTest {

    @Mock
    private UserSettingsRepository repository;

    @InjectMocks
    private UserSettingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save user settings - Success")
    void testSaveUserSettings_Success() {
        // Given
        UserSettings settings = new UserSettings();
        settings.setUserId(1L);
        settings.setDarkMode(true);
        settings.setFontSize("large");
        settings.setDateFormat("YYYY-MM-DD");

        when(repository.existsByUserId(1L)).thenReturn(false);
        when(repository.save(any(UserSettings.class))).thenReturn(settings);

        // When
        UserSettings saved = service.saveUserSettings(settings);

        // Then
        assertNotNull(saved);
        assertEquals(1L, saved.getUserId());
        assertTrue(saved.getDarkMode());
        assertEquals("large", saved.getFontSize());
        verify(repository, times(1)).save(settings);
    }

    @Test
    @DisplayName("Get user settings - Success")
    void testGetUserSettings_Success() {
        // Given
        Long userId = 1L;
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        settings.setDarkMode(false);
        settings.setFontSize("medium");

        when(repository.findByUserId(userId)).thenReturn(Optional.of(settings));

        // When
        UserSettings found = service.getUserSettings(userId);

        // Then
        assertNotNull(found);
        assertEquals(userId, found.getUserId());
        assertEquals("medium", found.getFontSize());
        assertFalse(found.getDarkMode());
    }

    @Test
    @DisplayName("Get user settings - Not found, returns default")
    void testGetUserSettings_NotFound_ReturnsDefault() {
        // Given
        Long userId = 999L;
        UserSettings defaultSettings = new UserSettings(userId);

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        when(repository.save(any(UserSettings.class))).thenReturn(defaultSettings);

        // When
        UserSettings found = service.getUserSettings(userId);

        // Then
        assertNotNull(found);
        assertEquals(userId, found.getUserId());
        assertFalse(found.getDarkMode()); // default is false
        assertEquals("medium", found.getFontSize()); // default is medium
    }

    @Test
    @DisplayName("Validate settings - Invalid font size")
    void testValidateSettings_InvalidFontSize() {
        // Given
        UserSettings settings = new UserSettings();
        settings.setUserId(1L);
        settings.setFontSize("invalid-size");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.saveUserSettings(settings);
        });
    }

    @Test
    @DisplayName("Update existing settings - Success")
    void testUpdateExistingSettings_Success() {
        // Given
        Long userId = 1L;
        UserSettings existing = new UserSettings();
        existing.setUserId(userId);
        existing.setDarkMode(false);
        existing.setFontSize("medium");

        UserSettings updated = new UserSettings();
        updated.setUserId(userId);
        updated.setDarkMode(true);
        updated.setFontSize("large");

        when(repository.existsByUserId(userId)).thenReturn(true);
        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(repository.save(any(UserSettings.class))).thenReturn(existing);

        // When
        UserSettings result = service.saveUserSettings(updated);

        // Then
        assertTrue(result.getDarkMode());
        assertEquals("large", result.getFontSize());
        verify(repository, times(1)).save(existing);
    }
}