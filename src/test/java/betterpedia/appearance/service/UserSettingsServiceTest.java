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

@DisplayName("UserSetting Service Tests")
class UserSettingServiceTest {

    @Mock
    private UserSettingsRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSettingService service;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Get user settings - Existing settings found")
    void testGetUserSettings_ExistingSettings() {
        // Given
        Long userId = 1L;
        UserSettings existingSettings = new UserSettings();
        existingSettings.setDarkMode(true);
        existingSettings.setFontSize("large");
        existingSettings.setDateFormat("YYYY-MM-DD");
        existingSettings.setUser(testUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(repository.findByUser(testUser)).thenReturn(Optional.of(existingSettings));

        // When
        UserSettings result = service.getUserSettings(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.getDarkMode());
        assertEquals("large", result.getFontSize());
        assertEquals("YYYY-MM-DD", result.getDateFormat());
        verify(userRepository, times(1)).findById(userId);
        verify(repository, times(1)).findByUser(testUser);
    }

    @Test
    @DisplayName("Get user settings - No existing settings, returns default")
    void testGetUserSettings_NoExistingSettings_ReturnsDefault() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(repository.findByUser(testUser)).thenReturn(Optional.empty());

        // When
        UserSettings result = service.getUserSettings(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertFalse(result.getDarkMode()); // default value
        assertEquals("medium", result.getFontSize()); // default value
        assertEquals("DD/MM/YYYY", result.getDateFormat()); // default value
        verify(userRepository, times(1)).findById(userId);
        verify(repository, times(1)).findByUser(testUser);
    }

    @Test
    @DisplayName("Get user settings - User not found throws exception")
    void testGetUserSettings_UserNotFound() {
        // Given
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.getUserSettings(userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(repository, never()).findByUser(any());
    }

    @Test
    @DisplayName("Save user settings - New settings creation")
    void testSaveUserSettings_NewSettings() {
        // Given
        Long userId = 1L;
        UserSettings newSettings = new UserSettings();
        newSettings.setDarkMode(true);
        newSettings.setFontSize("large");
        newSettings.setDateFormat("YYYY-MM-DD");

        UserSettings savedSettings = new UserSettings();
        savedSettings.setUser(testUser);
        savedSettings.setDarkMode(true);
        savedSettings.setFontSize("large");
        savedSettings.setDateFormat("YYYY-MM-DD");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(repository.findByUser(testUser)).thenReturn(Optional.empty());
        when(repository.save(any(UserSettings.class))).thenReturn(savedSettings);

        // When
        UserSettings result = service.saveUserSettings(userId, newSettings);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertTrue(result.getDarkMode());
        assertEquals("large", result.getFontSize());
        verify(repository, times(1)).save(any(UserSettings.class));
    }

    @Test
    @DisplayName("Save user settings - Update existing settings")
    void testSaveUserSettings_UpdateExistingSettings() {
        // Given
        Long userId = 1L;

        UserSettings existingSettings = new UserSettings();
        existingSettings.setUser(testUser);
        existingSettings.setDarkMode(false);
        existingSettings.setFontSize("medium");
        existingSettings.setDateFormat("DD/MM/YYYY");

        UserSettings newSettings = new UserSettings();
        newSettings.setDarkMode(true);
        newSettings.setFontSize("large");
        newSettings.setDateFormat("YYYY-MM-DD");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(repository.findByUser(testUser)).thenReturn(Optional.of(existingSettings));
        when(repository.save(existingSettings)).thenReturn(existingSettings);

        // When
        UserSettings result = service.saveUserSettings(userId, newSettings);

        // Then
        assertNotNull(result);
        assertTrue(result.getDarkMode()); // updated
        assertEquals("large", result.getFontSize()); // updated
        assertEquals("YYYY-MM-DD", result.getDateFormat()); // updated
        verify(repository, times(1)).save(existingSettings);
    }

    @Test
    @DisplayName("Save user settings - Partial update with null values ignored")
    void testSaveUserSettings_PartialUpdate() {
        // Given
        Long userId = 1L;

        UserSettings existingSettings = new UserSettings();
        existingSettings.setUser(testUser);
        existingSettings.setDarkMode(false);
        existingSettings.setFontSize("medium");
        existingSettings.setDateFormat("DD/MM/YYYY");
        existingSettings.setPageWidth("fixed");

        UserSettings partialUpdate = new UserSettings();
        partialUpdate.setDarkMode(true); // only this field is updated
        // other fields are null

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(repository.findByUser(testUser)).thenReturn(Optional.of(existingSettings));
        when(repository.save(existingSettings)).thenReturn(existingSettings);

        // When
        UserSettings result = service.saveUserSettings(userId, partialUpdate);

        // Then
        assertNotNull(result);
        assertTrue(result.getDarkMode()); // updated
        assertEquals("medium", result.getFontSize()); // preserved
        assertEquals("DD/MM/YYYY", result.getDateFormat()); // preserved
        assertEquals("fixed", result.getPageWidth()); // preserved
    }

    @Test
    @DisplayName("Save user settings - User not found throws exception")
    void testSaveUserSettings_UserNotFound() {
        // Given
        Long userId = 999L;
        UserSettings settings = new UserSettings();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            service.saveUserSettings(userId, settings);
        });

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Create default settings - All default values set correctly")
    void testCreateDefaultSettings() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(repository.findByUser(testUser)).thenReturn(Optional.empty());

        // When
        UserSettings result = service.getUserSettings(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertFalse(result.getDarkMode());
        assertEquals("medium", result.getFontSize());
        assertEquals("DD/MM/YYYY", result.getDateFormat());
        assertEquals(0, result.getTimeOffset());
        assertEquals("fixed", result.getPageWidth());
        assertEquals("arial", result.getFontStyle());
        assertEquals("normal", result.getLineSpacing());
    }

    @Test
    @DisplayName("Update settings - All fields updated correctly")
    void testSaveUserSettings_AllFields() {
        // Given
        Long userId = 1L;

        UserSettings existingSettings = new UserSettings(testUser);
        UserSettings updateSettings = new UserSettings();
        updateSettings.setDarkMode(true);
        updateSettings.setFontSize("extra-large");
        updateSettings.setDateFormat("MM/DD/YYYY");
        updateSettings.setTimeOffset(9);
        updateSettings.setPageWidth("wide");
        updateSettings.setFontStyle("serif");
        updateSettings.setLineSpacing("relaxed");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(repository.findByUser(testUser)).thenReturn(Optional.of(existingSettings));
        when(repository.save(existingSettings)).thenReturn(existingSettings);

        // When
        UserSettings result = service.saveUserSettings(userId, updateSettings);

        // Then
        assertNotNull(result);
        assertTrue(result.getDarkMode());
        assertEquals("extra-large", result.getFontSize());
        assertEquals("MM/DD/YYYY", result.getDateFormat());
        assertEquals(9, result.getTimeOffset());
        assertEquals("wide", result.getPageWidth());
        assertEquals("serif", result.getFontStyle());
        assertEquals("relaxed", result.getLineSpacing());
    }
}