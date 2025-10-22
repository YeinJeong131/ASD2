package betterpedia.badge.controller;

import betterpedia.badge.entity.Badge;
import betterpedia.badge.service.BadgeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    /**
     * Get badge for current user
     * U104: User views badges on user profile
     */
    @GetMapping("/my-badge")
    public ResponseEntity<?> getMyBadge(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        try {
            Optional<Badge> badge = badgeService.getBadgeByUser(userId);
            if (badge.isPresent()) {
                return ResponseEntity.ok(badge.get());
            } else {
                // Initialize badge if it doesn't exist
                Badge newBadge = badgeService.initializeBadge(userId);
                return ResponseEntity.ok(newBadge);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get badge: " + e.getMessage());
        }
    }

    /**
     * Get badge for a specific user
     * U104: User views badges on user profile
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserBadge(@PathVariable Long userId) {
        try {
            Optional<Badge> badge = badgeService.getBadgeByUser(userId);
            if (badge.isPresent()) {
                return ResponseEntity.ok(badge.get());
            } else {
                return ResponseEntity.ok(Map.of("message", "No badge found for this user"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get badge: " + e.getMessage());
        }
    }

    /**
     * Get all badges
     * U104: User views badges on user profile
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllBadges() {
        try {
            List<Badge> badges = badgeService.getAllBadges();
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get badges: " + e.getMessage());
        }
    }

    /**
     * Get badges by level
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<?> getBadgesByLevel(@PathVariable String level) {
        try {
            List<Badge> badges = badgeService.getBadgesByLevel(level.toUpperCase());
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get badges: " + e.getMessage());
        }
    }

    /**
     * Get badge statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getBadgeStats() {
        try {
            BadgeService.BadgeStats stats = badgeService.getBadgeStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get badge stats: " + e.getMessage());
        }
    }

    /**
     * Manually set badge level (admin only)
     * U106: Admin assigns or revokes badges manually
     */
    @PutMapping("/admin/set-level")
    public ResponseEntity<?> setBadgeLevel(
            @RequestParam Long targetUserId,
            @RequestParam String level,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        try {
            Badge badge = badgeService.setBadgeLevel(targetUserId, level.toUpperCase());
            return ResponseEntity.ok(badge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to set badge level: " + e.getMessage());
        }
    }

    /**
     * Manually set contribution count (admin only)
     * U106: Admin assigns or revokes badges manually
     */
    @PutMapping("/admin/set-contributions")
    public ResponseEntity<?> setContributionCount(
            @RequestParam Long targetUserId,
            @RequestParam int count,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        try {
            Badge badge = badgeService.setContributionCount(targetUserId, count);
            return ResponseEntity.ok(badge);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to set contribution count: " + e.getMessage());
        }
    }

    /**
     * Delete a badge (admin only)
     * U106: Admin assigns or revokes badges manually
     */
    @DeleteMapping("/admin/{targetUserId}")
    public ResponseEntity<?> deleteBadge(
            @PathVariable Long targetUserId,
            HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        if (isAdmin == null || !isAdmin) {
            return ResponseEntity.status(403).body("Admin access required");
        }

        try {
            boolean deleted = badgeService.deleteBadge(targetUserId);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Badge deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to delete badge: " + e.getMessage());
        }
    }
}

