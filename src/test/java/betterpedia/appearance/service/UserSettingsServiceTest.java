package betterpedia.appearance.service;

import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.repository.UserSettingsRepository;
import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
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
class UserSettingsServiceTest {

    @Mock
    private UserSettingsRepository repository;

    @Mock
    private UserRepository userRepository;

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
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        UserSettings settings = new UserSettings();
        settings.setDarkMode(true);
        settings.setFontSize("large");
        settings.setDateFormat("YYYY-MM-DD");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.existsByUserId(userId)).thenReturn(false);
        when(repository.save(any(UserSettings.class))).thenReturn(settings);

        // When
        UserSettings saved = service.saveUserSettings(userId, settings);

        // Then
        assertNotNull(saved);
        assertTrue(saved.getDarkMode());
        assertEquals("large", saved.getFontSize());
        verify(repository, times(1)).save(any(UserSettings.class));
    }

    @Test
    @DisplayName("Get user settings - Success")
    void testGetUserSettings_Success() {
        // Given
        Long userId = 1L;
        UserSettings settings = new UserSettings();
        settings.setDarkMode(false);
        settings.setFontSize("medium");

        when(repository.findByUserId(userId)).thenReturn(Optional.of(settings));

        // When
        UserSettings found = service.getUserSettings(userId);

        // Then
        assertNotNull(found);
        assertEquals("medium", found.getFontSize());
        assertFalse(found.getDarkMode());
    }

    @Test
    @DisplayName("Get user settings - Not found, returns default")
    void testGetUserSettings_NotFound_ReturnsDefault() {
        // Given
        Long userId = 999L;

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        UserSettings found = service.getUserSettings(userId);

        // Then
        assertNotNull(found);
        assertFalse(found.getDarkMode()); // default is false
        assertEquals("medium", found.getFontSize()); // default is medium
        verify(repository, never()).save(any()); // DB에 저장하지 않음
    }

    @Test
    @DisplayName("Validate settings - Invalid font size")
    void testValidateSettings_InvalidFontSize() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        UserSettings settings = new UserSettings();
        settings.setFontSize("invalid-size");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.saveUserSettings(userId, settings);
        });
    }

    @Test
    @DisplayName("Update existing settings - Success")
    void testUpdateExistingSettings_Success() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        UserSettings existing = new UserSettings();
        existing.setDarkMode(false);
        existing.setFontSize("medium");

        UserSettings updated = new UserSettings();
        updated.setDarkMode(true);
        updated.setFontSize("large");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.existsByUserId(userId)).thenReturn(true);
        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(repository.save(any(UserSettings.class))).thenReturn(existing);

        // When
        UserSettings result = service.saveUserSettings(userId, updated);

        // Then
        assertTrue(result.getDarkMode());
        assertEquals("large", result.getFontSize());
        verify(repository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Save settings - User not found")
    void testSaveUserSettings_UserNotFound() {
        // Given
        Long userId = 999L;
        UserSettings settings = new UserSettings();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.saveUserSettings(userId, settings);
        });
    }
}