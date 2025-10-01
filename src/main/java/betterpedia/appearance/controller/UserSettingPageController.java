package betterpedia.appearance.controller;

import betterpedia.appearance.entity.UserSettings;
import betterpedia.appearance.service.UserSettingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // returns html page
public class UserSettingPageController {

    @Autowired
    private UserSettingService userSettingService;

    // GET /settings
    @GetMapping("/settings")
    public String settingsPage(HttpSession session) {
        // check - logged in
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
            // templates/settings_page.html
            return "settings_page";
    }
}