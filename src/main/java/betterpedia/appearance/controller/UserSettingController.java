package betterpedia.appearance.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.service.UserSettingService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// for API
@RestController // returns JSON data
@RequestMapping("/api/settings")
public class UserSettingController {

    @Autowired
    private UserSettingService service;

    // security - yein (add Map for Rate limiting)
    private Map<Long, LocalDateTime> lastRequestTime = new ConcurrentHashMap<>();
    private static final int REQUEST_COOLDOWN_SECONDS = 2; // 2 second cool down

//    // GET /api/settings/{userId} - bring user's settings
//    @GetMapping("/{userId}")
//    public ResponseEntity<?> getUserSettings(@PathVariable Long userId) {
//        try {
//            if (userId <= 0) {
//                return ResponseEntity.badRequest().body("Invalid user ID: userId must be greater than 0");
//            }
//            UserSettings settings = service.getUserSettings(userId); // help us get a user's settings
//            return ResponseEntity.ok(settings);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Failed to get user settings: " + e.getMessage());
//        }
//    } not using anymore

    @GetMapping("")
    public ResponseEntity<?> getUserSettings(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            UserSettings settings = service.getUserSettings(userId);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get user settings: " + e.getMessage());
        }
    }


//    // POST /api/settings - store user settings
//    @PostMapping
//    public ResponseEntity<?> saveUserSettings(@RequestBody UserSettings settings) {
//        try {
//            UserSettings savedSettings = service.saveUserSettings(settings);
//            return ResponseEntity.ok(savedSettings);
//        } catch (IllegalArgumentException e){
//            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Failed to save user settings: " + e.getMessage());
//        }
//    } not using anymore

    @PostMapping
    public ResponseEntity<?> saveUserSettings(@RequestBody UserSettings settings, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        // security - yein (add Rate Limiting)
        if (!isRequestAllowed(userId)) {
            return ResponseEntity.status(429).body("Too many requests. Please wait.");
        }

        try {
            // security - yein (input validation)
            validateUserSettingsInput(settings);

            UserSettings savedSettings = service.saveUserSettings(userId, settings);
            return ResponseEntity.ok(savedSettings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to save user settings: " + e.getMessage());
        }
    }

    // security - yein (checking Rate Limiting)
    private boolean isRequestAllowed(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastRequest = lastRequestTime.get(userId);

        if (lastRequest == null || lastRequest.plusSeconds(REQUEST_COOLDOWN_SECONDS).isBefore(now)) {
            lastRequestTime.put(userId, now);
            return true;
        }

        return false;
    }

    // security - yein (additional validation method)
    private void validateUserSettingsInput(UserSettings settings) {
        // null 체크
        if (settings == null) {
            throw new IllegalArgumentException("Settings cannot be null");
        }

        // 문자열 길이 체크
        if (settings.getFontSize() != null && settings.getFontSize().length() > 20) {
            throw new IllegalArgumentException("Font size value too long");
        }

        if (settings.getDateFormat() != null && settings.getDateFormat().length() > 20) {
            throw new IllegalArgumentException("Date format value too long");
        }

        if (settings.getPageWidth() != null && settings.getPageWidth().length() > 20) {
            throw new IllegalArgumentException("Page width value too long");
        }
    }

    // bring logged in user info
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String email = (String) session.getAttribute("email");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Map<String, Object> user = Map.of(
                "id", userId,
                "email", email != null ? email : "unknown"
        );
        return ResponseEntity.ok(user);
    }
}
