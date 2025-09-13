package betterpedia.appearance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // returns html page
public class UserSettingPageController {

    // GET /settings
    @GetMapping("/settings")
    public String settingsPage() {
        // templates/settings_page.html
        return "settings_page";
    }
}