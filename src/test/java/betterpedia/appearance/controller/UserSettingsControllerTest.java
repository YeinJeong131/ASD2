package betterpedia.appearance.controller;

import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.service.UserSettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSettingController.class)
class UserSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSettingService service;

    @Test
    void testGetUserSettings_Success() throws Exception {
        // Given
        Long userId = 1L;
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        settings.setDarkMode(true);
        settings.setFontSize("large");

        when(service.getUserSettings(userId)).thenReturn(settings);

        // When & Then
        mockMvc.perform(get("/api/settings/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.darkMode").value(true))
                .andExpect(jsonPath("$.fontSize").value("large"));
    }

    @Test
    void testGetUserSettings_InvalidUserId() throws Exception {
        // Given
        Long invalidUserId = 0L;

        // When & Then
        mockMvc.perform(get("/api/settings/{userId}", invalidUserId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveUserSettings_Success() throws Exception {
        // Given
        UserSettings settings = new UserSettings();
        settings.setUserId(1L);
        settings.setDarkMode(false);

        when(service.saveUserSettings(any(UserSettings.class))).thenReturn(settings);

        // When & Then
        mockMvc.perform(post("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"darkMode\":false,\"fontSize\":\"medium\"}"))
                .andExpect(status().isOk());

        verify(service, times(1)).saveUserSettings(any(UserSettings.class));
    }
}