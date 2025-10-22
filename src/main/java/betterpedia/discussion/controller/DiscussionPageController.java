package betterpedia.discussion.controller;

import betterpedia.discussion.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DiscussionPageController {

    @Autowired
    private CommentService commentService;

    /**
     * Display discussion page for a specific article
     * U142a, U142b: User submits and sees comments
     */
    @GetMapping("/discussion/{articleId}")
    public String showDiscussionPage(
            @PathVariable Long articleId, 
            Model model,
            HttpSession session) {
        
        // Safely get session attributes (handles null session)
        Long userId = session != null ? (Long) session.getAttribute("userId") : null;
        String userEmail = session != null ? (String) session.getAttribute("userEmail") : null;
        Boolean isAdmin = session != null ? (Boolean) session.getAttribute("isAdmin") : null;
        
        // Add user info to model (with defaults for non-logged-in users)
        model.addAttribute("userId", userId != null ? userId : 0L);
        model.addAttribute("userEmail", userEmail != null ? userEmail : "Guest");
        model.addAttribute("isLoggedIn", userId != null);
        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
        
        // Add article ID
        model.addAttribute("articleId", articleId);
        
        // Get comment count
        try {
            long commentCount = commentService.getCommentCount(articleId);
            model.addAttribute("commentCount", commentCount);
        } catch (Exception e) {
            model.addAttribute("commentCount", 0L);
        }
        
        return "discussion/discussion";
    }
}

