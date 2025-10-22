package betterpedia.discussion.repository;

import betterpedia.discussion.entity.Comment;
import betterpedia.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Find all comments for a specific article
    List<Comment> findByArticleIdAndDeletedFalseOrderByCreatedDateAsc(Long articleId);
    
    // Find all comments by a specific user
    List<Comment> findByUserAndDeletedFalseOrderByCreatedDateDesc(User user);
    
    // Find top-level comments (no parent) for an article
    List<Comment> findByArticleIdAndParentCommentIsNullAndDeletedFalseOrderByCreatedDateAsc(Long articleId);
    
    // Find replies to a specific comment
    List<Comment> findByParentCommentAndDeletedFalseOrderByCreatedDateAsc(Comment parentComment);
    
    // Count comments for an article
    long countByArticleIdAndDeletedFalse(Long articleId);
    
    // Count all comments by a user
    long countByUserAndDeletedFalse(User user);
}

