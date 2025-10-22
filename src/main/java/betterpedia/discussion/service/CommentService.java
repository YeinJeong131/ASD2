package betterpedia.discussion.service;

import betterpedia.discussion.entity.Comment;
import betterpedia.discussion.repository.CommentRepository;
import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new comment for an article
     * U142a: User submits a comment through a form
     */
    @Transactional
    public Comment createComment(Long userId, Long articleId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        Comment comment = new Comment(user, articleId, content);
        return commentRepository.save(comment);
    }

    /**
     * Create a reply to an existing comment
     * U143: User replies to another user's comment
     */
    @Transactional
    public Comment replyToComment(Long userId, Long articleId, Long parentCommentId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Reply content cannot be empty");
        }

        Comment reply = new Comment(user, articleId, content, parentComment);
        return commentRepository.save(reply);
    }

    /**
     * Get all comments for a specific article
     * U142b: User sees comments saved and displayed under the correct article
     */
    public List<Comment> getCommentsByArticle(Long articleId) {
        return commentRepository.findByArticleIdAndDeletedFalseOrderByCreatedDateAsc(articleId);
    }

    /**
     * Get top-level comments (no parent) for an article
     */
    public List<Comment> getTopLevelComments(Long articleId) {
        return commentRepository.findByArticleIdAndParentCommentIsNullAndDeletedFalseOrderByCreatedDateAsc(articleId);
    }

    /**
     * Get replies to a specific comment
     */
    public List<Comment> getReplies(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentRepository.findByParentCommentAndDeletedFalseOrderByCreatedDateAsc(comment);
    }

    /**
     * Get all comments by a user
     */
    public List<Comment> getCommentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return commentRepository.findByUserAndDeletedFalseOrderByCreatedDateDesc(user);
    }

    /**
     * Update a comment (only by the owner)
     */
    @Transactional
    public Comment updateComment(Long userId, Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("You can only edit your own comments");
        }

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    /**
     * Delete a comment (soft delete)
     * U103: Admin deletes inappropriate comments
     * Also allows users to delete their own comments
     */
    @Transactional
    public boolean deleteComment(Long userId, Long commentId, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user is the owner or an admin
        if (!comment.getUser().getId().equals(userId) && !isAdmin) {
            throw new SecurityException("You can only delete your own comments");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
        return true;
    }

    /**
     * Get comment count for an article
     */
    public long getCommentCount(Long articleId) {
        return commentRepository.countByArticleIdAndDeletedFalse(articleId);
    }

    /**
     * Get total comments by a user (for badge calculation)
     */
    public long getUserCommentCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return commentRepository.countByUserAndDeletedFalse(user);
    }

    /**
     * Get a single comment by ID
     */
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }
}

