package betterpedia.discussion.service;

import betterpedia.discussion.entity.Comment;
import betterpedia.discussion.repository.CommentRepository;
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

@DisplayName("Comment Service Tests")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@example.com");
    }

    @Test
    @DisplayName("Create comment - Success")
    void testCreateComment_Success() {
        // Given
        Long userId = 1L;
        Long articleId = 100L;
        String content = "This is a test comment";

        Comment comment = new Comment(testUser, articleId, content);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        Comment created = commentService.createComment(userId, articleId, content);

        // Then
        assertNotNull(created);
        assertEquals(articleId, created.getArticleId());
        assertEquals(content, created.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Create comment - User not found")
    void testCreateComment_UserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            commentService.createComment(userId, 100L, "Test comment");
        });
    }

    @Test
    @DisplayName("Create comment - Empty content")
    void testCreateComment_EmptyContent() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            commentService.createComment(userId, 100L, "");
        });
    }

    @Test
    @DisplayName("Reply to comment - Success")
    void testReplyToComment_Success() {
        // Given
        Long userId = 1L;
        Long articleId = 100L;
        Long parentCommentId = 50L;
        String content = "This is a reply";

        Comment parentComment = new Comment(testUser, articleId, "Parent comment");
        parentComment.setId(parentCommentId);

        Comment reply = new Comment(testUser, articleId, content, parentComment);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(reply);

        // When
        Comment created = commentService.replyToComment(userId, articleId, parentCommentId, content);

        // Then
        assertNotNull(created);
        assertEquals(content, created.getContent());
        assertEquals(parentComment, created.getParentComment());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Reply to comment - Parent not found")
    void testReplyToComment_ParentNotFound() {
        // Given
        Long userId = 1L;
        Long parentCommentId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            commentService.replyToComment(userId, 100L, parentCommentId, "Reply");
        });
    }

    @Test
    @DisplayName("Get comments by article - Success")
    void testGetCommentsByArticle_Success() {
        // Given
        Long articleId = 100L;
        Comment comment1 = new Comment(testUser, articleId, "Comment 1");
        Comment comment2 = new Comment(testUser, articleId, "Comment 2");

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.findByArticleIdAndDeletedFalseOrderByCreatedDateAsc(articleId))
                .thenReturn(comments);

        // When
        List<Comment> found = commentService.getCommentsByArticle(articleId);

        // Then
        assertNotNull(found);
        assertEquals(2, found.size());
    }

    @Test
    @DisplayName("Get top-level comments - Success")
    void testGetTopLevelComments_Success() {
        // Given
        Long articleId = 100L;
        Comment comment1 = new Comment(testUser, articleId, "Top-level comment");

        List<Comment> comments = Arrays.asList(comment1);

        when(commentRepository.findByArticleIdAndParentCommentIsNullAndDeletedFalseOrderByCreatedDateAsc(articleId))
                .thenReturn(comments);

        // When
        List<Comment> found = commentService.getTopLevelComments(articleId);

        // Then
        assertNotNull(found);
        assertEquals(1, found.size());
    }

    @Test
    @DisplayName("Get replies - Success")
    void testGetReplies_Success() {
        // Given
        Long parentCommentId = 50L;
        Comment parentComment = new Comment(testUser, 100L, "Parent");
        parentComment.setId(parentCommentId);

        Comment reply = new Comment(testUser, 100L, "Reply", parentComment);

        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(commentRepository.findByParentCommentAndDeletedFalseOrderByCreatedDateAsc(parentComment))
                .thenReturn(Arrays.asList(reply));

        // When
        List<Comment> replies = commentService.getReplies(parentCommentId);

        // Then
        assertNotNull(replies);
        assertEquals(1, replies.size());
    }

    @Test
    @DisplayName("Update comment - Success")
    void testUpdateComment_Success() {
        // Given
        Long userId = 1L;
        Long commentId = 10L;
        String newContent = "Updated content";

        Comment existingComment = new Comment(testUser, 100L, "Old content");
        existingComment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(existingComment);

        // When
        Comment updated = commentService.updateComment(userId, commentId, newContent);

        // Then
        assertNotNull(updated);
        assertEquals(newContent, updated.getContent());
        verify(commentRepository, times(1)).save(existingComment);
    }

    @Test
    @DisplayName("Update comment - Not owner")
    void testUpdateComment_NotOwner() {
        // Given
        Long userId = 2L; // Different user
        Long commentId = 10L;

        Comment existingComment = new Comment(testUser, 100L, "Old content");
        existingComment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // When & Then
        assertThrows(SecurityException.class, () -> {
            commentService.updateComment(userId, commentId, "New content");
        });
    }

    @Test
    @DisplayName("Delete comment by owner - Success")
    void testDeleteComment_ByOwner_Success() {
        // Given
        Long userId = 1L;
        Long commentId = 10L;

        Comment comment = new Comment(testUser, 100L, "Content");
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        boolean deleted = commentService.deleteComment(userId, commentId, false);

        // Then
        assertTrue(deleted);
        assertTrue(comment.isDeleted());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("Delete comment by admin - Success")
    void testDeleteComment_ByAdmin_Success() {
        // Given
        Long adminId = 2L;
        Long commentId = 10L;

        Comment comment = new Comment(testUser, 100L, "Content");
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        boolean deleted = commentService.deleteComment(adminId, commentId, true);

        // Then
        assertTrue(deleted);
        assertTrue(comment.isDeleted());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("Delete comment - Not authorized")
    void testDeleteComment_NotAuthorized() {
        // Given
        Long userId = 2L; // Different user, not admin
        Long commentId = 10L;

        Comment comment = new Comment(testUser, 100L, "Content");
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // When & Then
        assertThrows(SecurityException.class, () -> {
            commentService.deleteComment(userId, commentId, false);
        });
    }

    @Test
    @DisplayName("Get comment count - Success")
    void testGetCommentCount_Success() {
        // Given
        Long articleId = 100L;
        when(commentRepository.countByArticleIdAndDeletedFalse(articleId)).thenReturn(5L);

        // When
        long count = commentService.getCommentCount(articleId);

        // Then
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Get user comment count - Success")
    void testGetUserCommentCount_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(commentRepository.countByUserAndDeletedFalse(testUser)).thenReturn(10L);

        // When
        long count = commentService.getUserCommentCount(userId);

        // Then
        assertEquals(10L, count);
    }

    @Test
    @DisplayName("Get comments by user - Success")
    void testGetCommentsByUser_Success() {
        // Given
        Long userId = 1L;
        Comment comment1 = new Comment(testUser, 100L, "Comment 1");
        Comment comment2 = new Comment(testUser, 101L, "Comment 2");

        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(commentRepository.findByUserAndDeletedFalseOrderByCreatedDateDesc(testUser))
                .thenReturn(comments);

        // When
        List<Comment> found = commentService.getCommentsByUser(userId);

        // Then
        assertNotNull(found);
        assertEquals(2, found.size());
    }
}

