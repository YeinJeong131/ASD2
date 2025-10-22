package betterpedia.badge.repository;

import betterpedia.badge.entity.Badge;
import betterpedia.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    // Find badge by user
    Optional<Badge> findByUser(User user);
    
    // Find all badges by level
    List<Badge> findByLevelOrderByContributionCountDesc(String level);
    
    // Find all badges ordered by contribution count
    List<Badge> findAllByOrderByContributionCountDesc();
    
    // Check if user has a badge
    boolean existsByUser(User user);
}

