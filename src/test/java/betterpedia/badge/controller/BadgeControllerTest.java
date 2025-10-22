package betterpedia.badge.controller;

import betterpedia.badge.entity.Badge;
import betterpedia.badge.service.BadgeService;
import betterpedia.user.entity.User;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Badge Controller Tests")
class BadgeControllerTest {

    @Mock
    private BadgeService badgeService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private BadgeController badgeController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Get my badge - Success")
    void testGetMyBadge_Success() {
        // Given
        Long userId = 1L;
        Badge badge = new Badge(testUser, "GOLD", 60);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(badgeService.getBadgeByUser(userId)).thenReturn(Optional.of(badge));

        // When
        ResponseEntity<?> response = badgeController.getMyBadge(session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(badgeService, times(1)).getBadgeByUser(userId);
    }

    @Test
    @DisplayName("Get my badge - Initialize if not exists")
    void testGetMyBadge_InitializeIfNotExists() {
        // Given
        Long userId = 1L;
        Badge newBadge = new Badge(testUser, "BRONZE", 0);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(badgeService.getBadgeByUser(userId)).thenReturn(Optional.empty());
        when(badgeService.initializeBadge(userId)).thenReturn(newBadge);

        // When
        ResponseEntity<?> response = badgeController.getMyBadge(session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(badgeService, times(1)).initializeBadge(userId);
    }

    @Test
    @DisplayName("Get my badge - Not logged in")
    void testGetMyBadge_NotLoggedIn() {
        // Given
        when(session.getAttribute("userId")).thenReturn(null);

        // When
        ResponseEntity<?> response = badgeController.getMyBadge(session);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(badgeService, never()).getBadgeByUser(anyLong());
    }

    @Test
    @DisplayName("Get user badge - Success")
    void testGetUserBadge_Success() {
        // Given
        Long userId = 2L;
        Badge badge = new Badge(testUser, "SILVER", 30);

        when(badgeService.getBadgeByUser(userId)).thenReturn(Optional.of(badge));

        // When
        ResponseEntity<?> response = badgeController.getUserBadge(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get user badge - Not found")
    void testGetUserBadge_NotFound() {
        // Given
        Long userId = 999L;

        when(badgeService.getBadgeByUser(userId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = badgeController.getUserBadge(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
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

        when(badgeService.getAllBadges()).thenReturn(badges);

        // When
        ResponseEntity<?> response = badgeController.getAllBadges();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get badges by level - Success")
    void testGetBadgesByLevel_Success() {
        // Given
        Badge badge = new Badge(testUser, "GOLD", 60);
        List<Badge> goldBadges = Arrays.asList(badge);

        when(badgeService.getBadgesByLevel("GOLD")).thenReturn(goldBadges);

        // When
        ResponseEntity<?> response = badgeController.getBadgesByLevel("gold");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(badgeService, times(1)).getBadgesByLevel("GOLD");
    }

    @Test
    @DisplayName("Get badge stats - Success")
    void testGetBadgeStats_Success() {
        // Given
        BadgeService.BadgeStats stats = new BadgeService.BadgeStats(10, 5, 3, 1);
        when(badgeService.getBadgeStats()).thenReturn(stats);

        // When
        ResponseEntity<?> response = badgeController.getBadgeStats();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Set badge level - Success (Admin)")
    void testSetBadgeLevel_Success() {
        // Given
        Long adminId = 1L;
        Long targetUserId = 2L;
        String level = "PLATINUM";
        Badge badge = new Badge(testUser, level, 100);

        when(session.getAttribute("userId")).thenReturn(adminId);
        when(session.getAttribute("isAdmin")).thenReturn(true);
        when(badgeService.setBadgeLevel(targetUserId, "PLATINUM")).thenReturn(badge);

        // When
        ResponseEntity<?> response = badgeController.setBadgeLevel(targetUserId, level, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(badgeService, times(1)).setBadgeLevel(targetUserId, "PLATINUM");
    }

    @Test
    @DisplayName("Set badge level - Not admin")
    void testSetBadgeLevel_NotAdmin() {
        // Given
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("isAdmin")).thenReturn(false);

        // When
        ResponseEntity<?> response = badgeController.setBadgeLevel(2L, "GOLD", session);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(badgeService, never()).setBadgeLevel(anyLong(), anyString());
    }

    @Test
    @DisplayName("Set badge level - Not logged in")
    void testSetBadgeLevel_NotLoggedIn() {
        // Given
        when(session.getAttribute("userId")).thenReturn(null);

        // When
        ResponseEntity<?> response = badgeController.setBadgeLevel(2L, "GOLD", session);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(badgeService, never()).setBadgeLevel(anyLong(), anyString());
    }

    @Test
    @DisplayName("Set contribution count - Success (Admin)")
    void testSetContributionCount_Success() {
        // Given
        Long adminId = 1L;
        Long targetUserId = 2L;
        int count = 75;
        Badge badge = new Badge(testUser, "GOLD", count);

        when(session.getAttribute("userId")).thenReturn(adminId);
        when(session.getAttribute("isAdmin")).thenReturn(true);
        when(badgeService.setContributionCount(targetUserId, count)).thenReturn(badge);

        // When
        ResponseEntity<?> response = badgeController.setContributionCount(targetUserId, count, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(badgeService, times(1)).setContributionCount(targetUserId, count);
    }

    @Test
    @DisplayName("Set contribution count - Not admin")
    void testSetContributionCount_NotAdmin() {
        // Given
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("isAdmin")).thenReturn(false);

        // When
        ResponseEntity<?> response = badgeController.setContributionCount(2L, 50, session);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(badgeService, never()).setContributionCount(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Delete badge - Success (Admin)")
    void testDeleteBadge_Success() {
        // Given
        Long adminId = 1L;
        Long targetUserId = 2L;

        when(session.getAttribute("userId")).thenReturn(adminId);
        when(session.getAttribute("isAdmin")).thenReturn(true);
        when(badgeService.deleteBadge(targetUserId)).thenReturn(true);

        // When
        ResponseEntity<?> response = badgeController.deleteBadge(targetUserId, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(badgeService, times(1)).deleteBadge(targetUserId);
    }

    @Test
    @DisplayName("Delete badge - Not found")
    void testDeleteBadge_NotFound() {
        // Given
        Long adminId = 1L;
        Long targetUserId = 999L;

        when(session.getAttribute("userId")).thenReturn(adminId);
        when(session.getAttribute("isAdmin")).thenReturn(true);
        when(badgeService.deleteBadge(targetUserId)).thenReturn(false);

        // When
        ResponseEntity<?> response = badgeController.deleteBadge(targetUserId, session);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Delete badge - Not admin")
    void testDeleteBadge_NotAdmin() {
        // Given
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("isAdmin")).thenReturn(false);

        // When
        ResponseEntity<?> response = badgeController.deleteBadge(2L, session);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(badgeService, never()).deleteBadge(anyLong());
    }
}

