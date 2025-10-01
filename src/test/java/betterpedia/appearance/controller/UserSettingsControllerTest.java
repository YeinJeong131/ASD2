package betterpedia.appearance.controller;

import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.service.UserSettingService;
import org.junit.jupiter.api.Test;
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
class UserSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSettingService service;

    @Test
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
    }

    @Test
    void testGetUserSettings_NotLoggedIn() throws Exception {
        // Given - 세션 없음

        // When & Then
        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
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
                .andExpect(status().isOk());

        verify(service, times(1)).saveUserSettings(eq(userId), any(UserSettings.class));
    }

    @Test
    void testSaveUserSettings_NotLoggedIn() throws Exception {
        // Given - 세션 없음

        // When & Then
        mockMvc.perform(post("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"darkMode\":false,\"fontSize\":\"medium\"}"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void testSaveUserSettings_ValidationError() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);

        when(service.saveUserSettings(eq(userId), any(UserSettings.class)))
                .thenThrow(new IllegalArgumentException("Invalid font size"));

        // When & Then
        mockMvc.perform(post("/api/settings")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"darkMode\":false,\"fontSize\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);
        session.setAttribute("email", "test@example.com");

        // When & Then
        mockMvc.perform(get("/api/settings/current-user")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}