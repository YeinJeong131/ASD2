package betterpedia.appearance.controller;

import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.service.UserSettingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSettingController.class)
@DisplayName("UserSetting Controller Tests")
class UserSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSettingService service;

    @Test
    @DisplayName("GET /api/settings - Success with logged in user")
    void testGetUserSettings_Success() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        UserSettings settings = new UserSettings();
        settings.setDarkMode(true);
        settings.setFontSize("large");

        when(service.getUserSettings(userId)).thenReturn(settings);

        // When & Then
        mockMvc.perform(get("/api/settings")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.darkMode").value(true))
                .andExpect(jsonPath("$.fontSize").value("large"));

        verify(service, times(1)).getUserSettings(userId);
    }

    @Test
    @DisplayName("GET /api/settings - Unauthorized when not logged in")
    void testGetUserSettings_NotLoggedIn() throws Exception {
        // Given - no session

        // When & Then
        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Not logged in"));
    }

    @Test
    @DisplayName("POST /api/settings - Success with valid data")
    void testSaveUserSettings_Success() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        UserSettings settings = new UserSettings();
        settings.setDarkMode(false);
        settings.setFontSize("medium");

        when(service.saveUserSettings(eq(userId), any(UserSettings.class))).thenReturn(settings);

        // When & Then
        mockMvc.perform(post("/api/settings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"darkMode\":false,\"fontSize\":\"medium\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.darkMode").value(false))
                .andExpect(jsonPath("$.fontSize").value("medium"));

        verify(service, times(1)).saveUserSettings(eq(userId), any(UserSettings.class));
    }

    @Test
    @DisplayName("POST /api/settings - Unauthorized when not logged in")
    void testSaveUserSettings_NotLoggedIn() throws Exception {
        // Given - no session

        // When & Then
        mockMvc.perform(post("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"darkMode\":false,\"fontSize\":\"medium\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Not logged in"));
    }

    @Test
    @DisplayName("POST /api/settings - Bad request with validation error")
    void testSaveUserSettings_ValidationError() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        when(service.saveUserSettings(eq(userId), any(UserSettings.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        // When & Then
        mockMvc.perform(post("/api/settings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"darkMode\":false,\"fontSize\":\"invalid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation error: User not found"));
    }

    @Test
    @DisplayName("POST /api/settings - Rate limiting returns 429")
    void testSaveUserSettings_RateLimited() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        UserSettings settings = new UserSettings();
        when(service.saveUserSettings(eq(userId), any(UserSettings.class))).thenReturn(settings);

        // First request - should succeed
        mockMvc.perform(post("/api/settings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"darkMode\":false,\"fontSize\":\"medium\"}"))
                .andExpect(status().isOk());

        // Second immediate request - should be rate limited
        mockMvc.perform(post("/api/settings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"darkMode\":true,\"fontSize\":\"large\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("Too many requests. Please wait."));
    }

    @Test
    @DisplayName("GET /api/settings/current-user - Success with logged in user")
    void testGetCurrentUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);
        session.setAttribute("userEmail", "test@example.com");

        // When & Then
        mockMvc.perform(get("/api/settings/current-user")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("GET /api/settings/current-user - Unauthorized when not logged in")
    void testGetCurrentUser_NotLoggedIn() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/settings/current-user"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Not logged in"));
    }

    @Test
    @DisplayName("POST /api/settings - Bad request with input validation error")
    void testSaveUserSettings_InputValidationError() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        // When & Then - Test with font size too long
        mockMvc.perform(post("/api/settings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fontSize\":\"ThisFontSizeIsTooLongToBeValid\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation error: Font size value too long"));
    }
}