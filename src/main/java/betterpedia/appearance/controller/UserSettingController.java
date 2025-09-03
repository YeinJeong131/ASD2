package betterpedia.appearance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.service.UserSettingService;

@Controller
@RequestMapping("/api/settings")
public class UserSettingController {

    @Autowired
    private UserSettingService service;

    // GET /api/settings/{userId} - bring user's settings
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserSettings(@PathVariable Long userId) {
        try {
            if (userId <= 0) {
                return ResponseEntity.badRequest().body("Invalid user ID: userId must be greater than 0");
            }
            UserSettings settings = service.getUserSettings(userId);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to get user settings: " + e.getMessage());
        }
    }

    // POST /api/settings - store user settings
    @PostMapping
    public ResponseEntity<?> saveUserSettings(@RequestBody UserSettings settings) {
        try {
            UserSettings savedSettings = service.saveUserSettings(settings);
            return ResponseEntity.ok(savedSettings);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to save user settings: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    public String settingsPage() {
        return "settings"; // returns settings.html template
    }
}
