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
        
        // Safely get session attributes (handles null session)
        Long userId = session != null ? (Long) session.getAttribute("userId") : null;
        String userEmail = session != null ? (String) session.getAttribute("userEmail") : null;
        Boolean isAdmin = session != null ? (Boolean) session.getAttribute("isAdmin") : null;
        
        // Add user info to model (with defaults for non-logged-in users)
        model.addAttribute("userId", userId != null ? userId : 0L);
        model.addAttribute("userEmail", userEmail != null ? userEmail : "Guest");
        model.addAttribute("isLoggedIn", userId != null);
        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
        
        // Get badge statistics
        try {
            BadgeService.BadgeStats stats = badgeService.getBadgeStats();
            model.addAttribute("badgeStats", stats);
        } catch (Exception e) {
            // If badges not initialized yet, provide empty stats
            model.addAttribute("badgeStats", new BadgeService.BadgeStats(0, 0, 0, 0));
        }
        
        return "badges/badges";
    }
}

