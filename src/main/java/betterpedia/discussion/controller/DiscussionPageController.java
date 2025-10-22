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
        
        Long userId = (Long) session.getAttribute("userId");
        String userEmail = (String) session.getAttribute("userEmail");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        // Add user info to model
        model.addAttribute("userId", userId);
        model.addAttribute("userEmail", userEmail);
        model.addAttribute("isLoggedIn", userId != null);
        model.addAttribute("isAdmin", isAdmin != null && isAdmin);
        
        // Add article ID
        model.addAttribute("articleId", articleId);
        
        // Get comment count
        long commentCount = commentService.getCommentCount(articleId);
        model.addAttribute("commentCount", commentCount);
        
        return "discussion/discussion";
    }
}

