package betterpedia.badge.controller;

import betterpedia.badge.service.BadgeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BadgePageController {

    @Autowired
    private BadgeService badgeService;

    /**
     * Display badges page
     * U104: User views badges on user profile
     */
    @GetMapping("/badges")
    public String showBadgesPage(Model model, HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        String userEmail = (String) session.getAttribute("userEmail");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Add user info to model
        model.addAttribute("userId", userId);
        model.addAttribute("userEmail", userEmail);
        model.addAttribute("isLoggedIn", userId != null);
        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
        
        // Get badge statistics
        BadgeService.BadgeStats stats = badgeService.getBadgeStats();
        model.addAttribute("badgeStats", stats);
        
        return "badges/badges";
    }
}

