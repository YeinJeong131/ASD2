package betterpedia.discussion.controller;

import betterpedia.discussion.entity.Comment;
import betterpedia.discussion.service.CommentService;
import betterpedia.badge.service.BadgeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BadgeService badgeService;

    /**
     * Create a new comment
     * U142a: User submits a comment through a form
     */
    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestParam Long articleId,
            @RequestParam String content,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            Comment comment = commentService.createComment(userId, articleId, content);
            
            // Increment badge contribution count
            badgeService.incrementContribution(userId);
            
            return ResponseEntity.ok(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to create comment: " + e.getMessage());
        }
    }

    /**
     * Create a reply to a comment
     * U143: User replies to another user's comment
     */
    @PostMapping("/reply")
    public ResponseEntity<?> replyToComment(
            @RequestParam Long articleId,
            @RequestParam Long parentCommentId,
            @RequestParam String content,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            Comment reply = commentService.replyToComment(userId, articleId, parentCommentId, content);
            
            // Increment badge contribution count
            badgeService.incrementContribution(userId);
            
            return ResponseEntity.ok(reply);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to create reply: " + e.getMessage());
        }
    }

    /**
     * Get all comments for an article
     * U142b: User sees comments saved and displayed
     */
    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getCommentsByArticle(@PathVariable Long articleId) {
        try {
            List<Comment> comments = commentService.getCommentsByArticle(articleId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get comments: " + e.getMessage());
        }
    }

    /**
     * Get top-level comments for an article
     */
    @GetMapping("/article/{articleId}/top-level")
    public ResponseEntity<?> getTopLevelComments(@PathVariable Long articleId) {
        try {
            List<Comment> comments = commentService.getTopLevelComments(articleId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get comments: " + e.getMessage());
        }
    }

    /**
     * Get replies to a specific comment
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getReplies(@PathVariable Long commentId) {
        try {
            List<Comment> replies = commentService.getReplies(commentId);
            return ResponseEntity.ok(replies);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get replies: " + e.getMessage());
        }
    }

    /**
     * Get all comments by current user
     */
    @GetMapping("/my-comments")
    public ResponseEntity<?> getMyComments(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            List<Comment> comments = commentService.getCommentsByUser(userId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get comments: " + e.getMessage());
        }
    }

    /**
     * Update a comment
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestParam String content,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            Comment updated = commentService.updateComment(userId, commentId, content);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to update comment: " + e.getMessage());
        }
    }

    /**
     * Delete a comment
     * U103: Admin deletes inappropriate comments
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        // Check if user is admin
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null) {
            isAdmin = false;
        }

        try {
            boolean deleted = commentService.deleteComment(userId, commentId, isAdmin);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to delete comment: " + e.getMessage());
        }
    }

    /**
     * Get comment count for an article
     */
    @GetMapping("/article/{articleId}/count")
    public ResponseEntity<?> getCommentCount(@PathVariable Long articleId) {
        try {
            long count = commentService.getCommentCount(articleId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get comment count: " + e.getMessage());
        }
    }
}

