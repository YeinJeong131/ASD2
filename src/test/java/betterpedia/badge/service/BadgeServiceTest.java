package betterpedia.badge.service;

import betterpedia.badge.entity.Badge;
import betterpedia.badge.repository.BadgeRepository;
import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Badge Service Tests")
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BadgeService badgeService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Initialize badge - Success")
    void testInitializeBadge_Success() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "BRONZE", 0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(badgeRepository.save(any(Badge.class))).thenReturn(badge);

        // When
        Badge created = badgeService.initializeBadge(userId);

        // Then
        assertNotNull(created);
        assertEquals("BRONZE", created.getLevel());
        assertEquals(0, created.getContributionCount());
        verify(badgeRepository, times(1)).save(any(Badge.class));
    }

    @Test
    @DisplayName("Initialize badge - Already exists")
    void testInitializeBadge_AlreadyExists() {
        // Given
        Long userId = 1L;
        Badge existingBadge = new Badge(testUser, "SILVER", 25);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(existingBadge));

        // When
        Badge badge = badgeService.initializeBadge(userId);

        // Then
        assertNotNull(badge);
        assertEquals("SILVER", badge.getLevel());
        assertEquals(25, badge.getContributionCount());
        verify(badgeRepository, never()).save(any(Badge.class));
    }

    @Test
    @DisplayName("Increment contribution - Success")
    void testIncrementContribution_Success() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "BRONZE", 5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));
        when(badgeRepository.save(any(Badge.class))).thenReturn(badge);

        // When
        Badge updated = badgeService.incrementContribution(userId);

        // Then
        assertNotNull(updated);
        assertEquals(6, updated.getContributionCount());
        verify(badgeRepository, times(1)).save(badge);
    }

    @Test
    @DisplayName("Increment contribution - Level upgrade to Silver")
    void testIncrementContribution_LevelUpgradeToSilver() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "BRONZE", 19);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));
        when(badgeRepository.save(any(Badge.class))).thenAnswer(invocation -> {
            Badge savedBadge = invocation.getArgument(0);
            savedBadge.updateBadgeLevel();
            return savedBadge;
        });

        // When
        Badge updated = badgeService.incrementContribution(userId);

        // Then
        assertNotNull(updated);
        assertEquals(20, updated.getContributionCount());
        assertEquals("SILVER", updated.getLevel());
    }

    @Test
    @DisplayName("Increment contribution - Level upgrade to Gold")
    void testIncrementContribution_LevelUpgradeToGold() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "SILVER", 49);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));
        when(badgeRepository.save(any(Badge.class))).thenAnswer(invocation -> {
            Badge savedBadge = invocation.getArgument(0);
            savedBadge.updateBadgeLevel();
            return savedBadge;
        });

        // When
        Badge updated = badgeService.incrementContribution(userId);

        // Then
        assertNotNull(updated);
        assertEquals(50, updated.getContributionCount());
        assertEquals("GOLD", updated.getLevel());
    }

    @Test
    @DisplayName("Increment contribution - Level upgrade to Platinum")
    void testIncrementContribution_LevelUpgradeToPlatinum() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "GOLD", 99);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));
        when(badgeRepository.save(any(Badge.class))).thenAnswer(invocation -> {
            Badge savedBadge = invocation.getArgument(0);
            savedBadge.updateBadgeLevel();
            return savedBadge;
        });

        // When
        Badge updated = badgeService.incrementContribution(userId);

        // Then
        assertNotNull(updated);
        assertEquals(100, updated.getContributionCount());
        assertEquals("PLATINUM", updated.getLevel());
    }

    @Test
    @DisplayName("Get badge by user - Success")
    void testGetBadgeByUser_Success() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "GOLD", 60);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));

        // When
        Optional<Badge> found = badgeService.getBadgeByUser(userId);

        // Then
        assertTrue(found.isPresent());
        assertEquals("GOLD", found.get().getLevel());
        assertEquals(60, found.get().getContributionCount());
    }

    @Test
    @DisplayName("Get badge by user - Not found")
    void testGetBadgeByUser_NotFound() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // When
        Optional<Badge> found = badgeService.getBadgeByUser(userId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Get all badges - Success")
    void testGetAllBadges_Success() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        Badge badge1 = new Badge(testUser, "GOLD", 60);
        Badge badge2 = new Badge(user2, "SILVER", 30);

        List<Badge> badges = Arrays.asList(badge1, badge2);

        when(badgeRepository.findAllByOrderByContributionCountDesc()).thenReturn(badges);

        // When
        List<Badge> found = badgeService.getAllBadges();

        // Then
        assertNotNull(found);
        assertEquals(2, found.size());
    }

    @Test
    @DisplayName("Get badges by level - Success")
    void testGetBadgesByLevel_Success() {
        // Given
        Badge badge1 = new Badge(testUser, "GOLD", 60);
        List<Badge> goldBadges = Arrays.asList(badge1);

        when(badgeRepository.findByLevelOrderByContributionCountDesc("GOLD"))
                .thenReturn(goldBadges);

        // When
        List<Badge> found = badgeService.getBadgesByLevel("GOLD");

        // Then
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals("GOLD", found.get(0).getLevel());
    }

    @Test
    @DisplayName("Set badge level - Success")
    void testSetBadgeLevel_Success() {
        // Given
        Long userId = 1L;
        String newLevel = "PLATINUM";
        Badge badge = new Badge(testUser, "GOLD", 60);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));
        when(badgeRepository.save(any(Badge.class))).thenReturn(badge);

        // When
        Badge updated = badgeService.setBadgeLevel(userId, newLevel);

        // Then
        assertNotNull(updated);
        assertEquals("PLATINUM", updated.getLevel());
        verify(badgeRepository, times(1)).save(badge);
    }

    @Test
    @DisplayName("Set badge level - Invalid level")
    void testSetBadgeLevel_InvalidLevel() {
        // Given
        Long userId = 1L;
        String invalidLevel = "DIAMOND";
        Badge badge = new Badge(testUser, "GOLD", 60);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.setBadgeLevel(userId, invalidLevel);
        });
    }

    @Test
    @DisplayName("Set contribution count - Success")
    void testSetContributionCount_Success() {
        // Given
        Long userId = 1L;
        int newCount = 75;
        Badge badge = new Badge(testUser, "SILVER", 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));
        when(badgeRepository.save(any(Badge.class))).thenReturn(badge);

        // When
        Badge updated = badgeService.setContributionCount(userId, newCount);

        // Then
        assertNotNull(updated);
        assertEquals(75, updated.getContributionCount());
        verify(badgeRepository, times(1)).save(badge);
    }

    @Test
    @DisplayName("Delete badge - Success")
    void testDeleteBadge_Success() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "BRONZE", 5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.of(badge));
        doNothing().when(badgeRepository).delete(badge);

        // When
        boolean deleted = badgeService.deleteBadge(userId);

        // Then
        assertTrue(deleted);
        verify(badgeRepository, times(1)).delete(badge);
    }

    @Test
    @DisplayName("Delete badge - Not found")
    void testDeleteBadge_NotFound() {
        // Given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(badgeRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // When
        boolean deleted = badgeService.deleteBadge(userId);

        // Then
        assertFalse(deleted);
        verify(badgeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Get badge statistics - Success")
    void testGetBadgeStats_Success() {
        // Given
        Badge bronze1 = new Badge(testUser, "BRONZE", 5);
        Badge silver1 = new Badge(testUser, "SILVER", 25);
        Badge gold1 = new Badge(testUser, "GOLD", 60);
        Badge platinum1 = new Badge(testUser, "PLATINUM", 110);

        List<Badge> allBadges = Arrays.asList(bronze1, silver1, gold1, platinum1);

        when(badgeRepository.findAll()).thenReturn(allBadges);

        // When
        BadgeService.BadgeStats stats = badgeService.getBadgeStats();

        // Then
        assertNotNull(stats);
        assertEquals(1, stats.getBronzeCount());
        assertEquals(1, stats.getSilverCount());
        assertEquals(1, stats.getGoldCount());
        assertEquals(1, stats.getPlatinumCount());
    }

    @Test
    @DisplayName("Badge level update logic - Bronze to Silver")
    void testBadgeLevelUpdate_BronzeToSilver() {
        // Given
        Badge badge = new Badge(testUser, "BRONZE", 0);
        badge.setContributionCount(20);

        // When
        badge.updateBadgeLevel();

        // Then
        assertEquals("SILVER", badge.getLevel());
    }

    @Test
    @DisplayName("Badge level update logic - Silver to Gold")
    void testBadgeLevelUpdate_SilverToGold() {
        // Given
        Badge badge = new Badge(testUser, "SILVER", 20);
        badge.setContributionCount(50);

        // When
        badge.updateBadgeLevel();

        // Then
        assertEquals("GOLD", badge.getLevel());
    }

    @Test
    @DisplayName("Badge level update logic - Gold to Platinum")
    void testBadgeLevelUpdate_GoldToPlatinum() {
        // Given
        Badge badge = new Badge(testUser, "GOLD", 50);
        badge.setContributionCount(100);

        // When
        badge.updateBadgeLevel();

        // Then
        assertEquals("PLATINUM", badge.getLevel());
    }

    @Test
    @DisplayName("Initialize badge - User not found")
    void testInitializeBadge_UserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            badgeService.initializeBadge(userId);
        });
    }
}

