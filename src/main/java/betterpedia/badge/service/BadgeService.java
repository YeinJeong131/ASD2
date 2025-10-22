package betterpedia.badge.service;

import betterpedia.badge.entity.Badge;
import betterpedia.badge.repository.BadgeRepository;
import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create or initialize a badge for a user
     * U105: User earns badges for editing or creating content
     */
    @Transactional
    public Badge initializeBadge(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if badge already exists
        Optional<Badge> existingBadge = badgeRepository.findByUser(user);
        if (existingBadge.isPresent()) {
            return existingBadge.get();
        }

        // Create new badge with BRONZE level
        Badge badge = new Badge(user, "BRONZE", 0);
        return badgeRepository.save(badge);
    }

    /**
     * Increment contribution count and update badge level
     * U105: User earns badges for editing or creating content
     */
    @Transactional
    public Badge incrementContribution(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Badge badge = badgeRepository.findByUser(user)
                .orElseGet(() -> initializeBadge(userId));

        badge.setContributionCount(badge.getContributionCount() + 1);
        badge.updateBadgeLevel(); // Automatically updates level based on count
        
        return badgeRepository.save(badge);
    }

    /**
     * Get badge for a specific user
     * U104: User views badges on user profile
     */
    public Optional<Badge> getBadgeByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return badgeRepository.findByUser(user);
    }

    /**
     * Get all badges ordered by contribution count
     * U104: User views badges on user profile
     */
    public List<Badge> getAllBadges() {
        return badgeRepository.findAllByOrderByContributionCountDesc();
    }

    /**
     * Get all badges of a specific level
     */
    public List<Badge> getBadgesByLevel(String level) {
        return badgeRepository.findByLevelOrderByContributionCountDesc(level);
    }

    /**
     * Manually set badge level (admin function)
     * U106: Admin assigns or revokes badges manually
     */
    @Transactional
    public Badge setBadgeLevel(Long userId, String level) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Badge badge = badgeRepository.findByUser(user)
                .orElseGet(() -> initializeBadge(userId));

        // Validate level
        if (!isValidLevel(level)) {
            throw new IllegalArgumentException("Invalid badge level. Must be BRONZE, SILVER, GOLD, or PLATINUM");
        }

        badge.setLevel(level);
        return badgeRepository.save(badge);
    }

    /**
     * Manually set contribution count (admin function)
     * U106: Admin assigns or revokes badges manually
     */
    @Transactional
    public Badge setContributionCount(Long userId, int count) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Badge badge = badgeRepository.findByUser(user)
                .orElseGet(() -> initializeBadge(userId));

        badge.setContributionCount(count);
        badge.updateBadgeLevel();
        
        return badgeRepository.save(badge);
    }

    /**
     * Delete a badge (admin function)
     */
    @Transactional
    public boolean deleteBadge(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Badge> badge = badgeRepository.findByUser(user);
        if (badge.isPresent()) {
            badgeRepository.delete(badge.get());
            return true;
        }
        return false;
    }

    /**
     * Get badge statistics
     */
    public BadgeStats getBadgeStats() {
        List<Badge> allBadges = badgeRepository.findAll();
        long bronzeCount = allBadges.stream().filter(b -> "BRONZE".equals(b.getLevel())).count();
        long silverCount = allBadges.stream().filter(b -> "SILVER".equals(b.getLevel())).count();
        long goldCount = allBadges.stream().filter(b -> "GOLD".equals(b.getLevel())).count();
        long platinumCount = allBadges.stream().filter(b -> "PLATINUM".equals(b.getLevel())).count();
        
        return new BadgeStats(bronzeCount, silverCount, goldCount, platinumCount);
    }

    /**
     * Helper method to validate badge level
     */
    private boolean isValidLevel(String level) {
        return "BRONZE".equals(level) || "SILVER".equals(level) || 
               "GOLD".equals(level) || "PLATINUM".equals(level);
    }

    /**
     * Inner class for badge statistics
     */
    public static class BadgeStats {
        private long bronzeCount;
        private long silverCount;
        private long goldCount;
        private long platinumCount;

        public BadgeStats(long bronzeCount, long silverCount, long goldCount, long platinumCount) {
            this.bronzeCount = bronzeCount;
            this.silverCount = silverCount;
            this.goldCount = goldCount;
            this.platinumCount = platinumCount;
        }

        // Getters
        public long getBronzeCount() { return bronzeCount; }
        public long getSilverCount() { return silverCount; }
        public long getGoldCount() { return goldCount; }
        public long getPlatinumCount() { return platinumCount; }
    }
}

