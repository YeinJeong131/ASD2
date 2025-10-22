package betterpedia.discussion.controller;

import betterpedia.discussion.entity.Comment;
import betterpedia.discussion.service.CommentService;
import betterpedia.badge.service.BadgeService;
import betterpedia.badge.entity.Badge;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Comment Controller Tests")
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private BadgeService badgeService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CommentController commentController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Create comment - Success")
    void testCreateComment_Success() {
        // Given
        Long userId = 1L;
        Long articleId = 100L;
        String content = "Test comment";

        Comment comment = new Comment(testUser, articleId, content);
        Badge badge = new Badge(testUser, "BRONZE", 1);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(commentService.createComment(userId, articleId, content)).thenReturn(comment);
        when(badgeService.incrementContribution(userId)).thenReturn(badge);

        // When
        ResponseEntity<?> response = commentController.createComment(articleId, content, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).createComment(userId, articleId, content);
        verify(badgeService, times(1)).incrementContribution(userId);
    }

    @Test
    @DisplayName("Create comment - Not logged in")
    void testCreateComment_NotLoggedIn() {
        // Given
        when(session.getAttribute("userId")).thenReturn(null);

        // When
        ResponseEntity<?> response = commentController.createComment(100L, "Test", session);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(commentService, never()).createComment(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Reply to comment - Success")
    void testReplyToComment_Success() {
        // Given
        Long userId = 1L;
        Long articleId = 100L;
        Long parentCommentId = 50L;
        String content = "Reply";

        Comment parentComment = new Comment(testUser, articleId, "Parent");
        Comment reply = new Comment(testUser, articleId, content, parentComment);
        Badge badge = new Badge(testUser, "BRONZE", 1);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(commentService.replyToComment(userId, articleId, parentCommentId, content)).thenReturn(reply);
        when(badgeService.incrementContribution(userId)).thenReturn(badge);

        // When
        ResponseEntity<?> response = commentController.replyToComment(articleId, parentCommentId, content, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).replyToComment(userId, articleId, parentCommentId, content);
        verify(badgeService, times(1)).incrementContribution(userId);
    }

    @Test
    @DisplayName("Get comments by article - Success")
    void testGetCommentsByArticle_Success() {
        // Given
        Long articleId = 100L;
        Comment comment1 = new Comment(testUser, articleId, "Comment 1");
        Comment comment2 = new Comment(testUser, articleId, "Comment 2");
        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentService.getCommentsByArticle(articleId)).thenReturn(comments);

        // When
        ResponseEntity<?> response = commentController.getCommentsByArticle(articleId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get top-level comments - Success")
    void testGetTopLevelComments_Success() {
        // Given
        Long articleId = 100L;
        Comment comment = new Comment(testUser, articleId, "Top-level");
        List<Comment> comments = Arrays.asList(comment);

        when(commentService.getTopLevelComments(articleId)).thenReturn(comments);

        // When
        ResponseEntity<?> response = commentController.getTopLevelComments(articleId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get replies - Success")
    void testGetReplies_Success() {
        // Given
        Long commentId = 50L;
        Comment parentComment = new Comment(testUser, 100L, "Parent");
        Comment reply = new Comment(testUser, 100L, "Reply", parentComment);
        List<Comment> replies = Arrays.asList(reply);

        when(commentService.getReplies(commentId)).thenReturn(replies);

        // When
        ResponseEntity<?> response = commentController.getReplies(commentId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Update comment - Success")
    void testUpdateComment_Success() {
        // Given
        Long userId = 1L;
        Long commentId = 10L;
        String newContent = "Updated";

        Comment comment = new Comment(testUser, 100L, "Old");
        comment.setId(commentId);
        comment.setContent(newContent);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(commentService.updateComment(userId, commentId, newContent)).thenReturn(comment);

        // When
        ResponseEntity<?> response = commentController.updateComment(commentId, newContent, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).updateComment(userId, commentId, newContent);
    }

    @Test
    @DisplayName("Update comment - Not logged in")
    void testUpdateComment_NotLoggedIn() {
        // Given
        when(session.getAttribute("userId")).thenReturn(null);

        // When
        ResponseEntity<?> response = commentController.updateComment(10L, "Updated", session);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(commentService, never()).updateComment(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Delete comment - Success")
    void testDeleteComment_Success() {
        // Given
        Long userId = 1L;
        Long commentId = 10L;

        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("isAdmin")).thenReturn(false);
        when(commentService.deleteComment(userId, commentId, false)).thenReturn(true);

        // When
        ResponseEntity<?> response = commentController.deleteComment(commentId, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).deleteComment(userId, commentId, false);
    }

    @Test
    @DisplayName("Delete comment - Admin")
    void testDeleteComment_Admin() {
        // Given
        Long userId = 2L;
        Long commentId = 10L;

        when(session.getAttribute("userId")).thenReturn(userId);
        when(session.getAttribute("isAdmin")).thenReturn(true);
        when(commentService.deleteComment(userId, commentId, true)).thenReturn(true);

        // When
        ResponseEntity<?> response = commentController.deleteComment(commentId, session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(commentService, times(1)).deleteComment(userId, commentId, true);
    }

    @Test
    @DisplayName("Get comment count - Success")
    void testGetCommentCount_Success() {
        // Given
        Long articleId = 100L;
        when(commentService.getCommentCount(articleId)).thenReturn(5L);

        // When
        ResponseEntity<?> response = commentController.getCommentCount(articleId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get my comments - Success")
    void testGetMyComments_Success() {
        // Given
        Long userId = 1L;
        Comment comment = new Comment(testUser, 100L, "My comment");
        List<Comment> comments = Arrays.asList(comment);

        when(session.getAttribute("userId")).thenReturn(userId);
        when(commentService.getCommentsByUser(userId)).thenReturn(comments);

        // When
        ResponseEntity<?> response = commentController.getMyComments(session);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

