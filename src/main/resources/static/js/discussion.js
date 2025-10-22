// Discussion Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const commentForm = document.getElementById('commentForm');
    const commentContent = document.getElementById('commentContent');
    const commentsList = document.getElementById('commentsList');
    const noCommentsDiv = document.getElementById('noComments');
    const charCount = document.querySelector('.char-count');

    // Load comments on page load
    loadComments();

    // Character counter for main comment form
    if (commentContent && charCount) {
        commentContent.addEventListener('input', function() {
            const length = this.value.length;
            charCount.textContent = `${length} / 2000`;
            
            if (length > 2000) {
                charCount.style.color = '#e74c3c';
            } else {
                charCount.style.color = '#999';
            }
        });
    }

    // Submit comment form
    if (commentForm) {
        commentForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const content = commentContent.value.trim();
            if (!content) {
                showMessage('Please enter a comment', 'error');
                return;
            }

            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Posting...';

            try {
                const formData = new URLSearchParams();
                formData.append('articleId', ARTICLE_ID);
                formData.append('content', content);

                const response = await fetch('/api/comments', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData
                });

                if (response.ok) {
                    showMessage('Comment posted successfully!', 'success');
                    commentContent.value = '';
                    charCount.textContent = '0 / 2000';
                    loadComments(); // Reload comments
                    updateCommentCount();
                } else {
                    const errorText = await response.text();
                    showMessage(errorText || 'Failed to post comment', 'error');
                }
            } catch (error) {
                console.error('Error posting comment:', error);
                showMessage('An error occurred while posting your comment', 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Post Comment';
            }
        });
    }

    // Load all comments for the article
    async function loadComments() {
        try {
            const response = await fetch(`/api/comments/article/${ARTICLE_ID}/top-level`);
            
            if (response.ok) {
                const comments = await response.json();
                displayComments(comments);
            } else {
                console.error('Failed to load comments');
                commentsList.innerHTML = '<p class="error">Failed to load comments</p>';
            }
        } catch (error) {
            console.error('Error loading comments:', error);
            commentsList.innerHTML = '<p class="error">An error occurred while loading comments</p>';
        }
    }

    // Display comments
    async function displayComments(comments) {
        commentsList.innerHTML = '';
        
        if (comments.length === 0) {
            commentsList.style.display = 'none';
            noCommentsDiv.style.display = 'block';
            return;
        }

        commentsList.style.display = 'flex';
        noCommentsDiv.style.display = 'none';

        for (const comment of comments) {
            const commentElement = await createCommentElement(comment);
            commentsList.appendChild(commentElement);
            
            // Load and display replies
            await loadReplies(comment.id, commentElement);
        }
    }

    // Create comment element
    async function createCommentElement(comment, isReply = false) {
        const div = document.createElement('div');
        div.className = 'comment-item' + (isReply ? ' reply' : '') + (comment.deleted ? ' comment-deleted' : '');
        div.dataset.commentId = comment.id;

        const date = new Date(comment.createdDate);
        const formattedDate = formatDate(date);

        const isOwner = IS_LOGGED_IN && comment.user.id === USER_ID;
        const canDelete = isOwner || IS_ADMIN;

        div.innerHTML = `
            <div class="comment-header">
                <span class="comment-author">${escapeHtml(comment.user.email)}</span>
                <span class="comment-date">${formattedDate}</span>
            </div>
            <div class="comment-content">${comment.deleted ? '[This comment has been deleted]' : escapeHtml(comment.content)}</div>
            ${!comment.deleted ? `
                <div class="comment-actions">
                    ${IS_LOGGED_IN ? `<button class="btn-secondary reply-btn" data-comment-id="${comment.id}">Reply</button>` : ''}
                    ${isOwner ? `<button class="btn-secondary edit-btn" data-comment-id="${comment.id}">Edit</button>` : ''}
                    ${canDelete ? `<button class="btn-danger delete-btn" data-comment-id="${comment.id}">Delete</button>` : ''}
                </div>
            ` : ''}
            <div class="replies-container"></div>
        `;

        // Add event listeners
        const replyBtn = div.querySelector('.reply-btn');
        const editBtn = div.querySelector('.edit-btn');
        const deleteBtn = div.querySelector('.delete-btn');

        if (replyBtn) {
            replyBtn.addEventListener('click', () => showReplyForm(comment.id, div));
        }

        if (editBtn) {
            editBtn.addEventListener('click', () => showEditForm(comment.id, comment.content, div));
        }

        if (deleteBtn) {
            deleteBtn.addEventListener('click', () => deleteComment(comment.id));
        }

        return div;
    }

    // Load replies for a comment
    async function loadReplies(commentId, parentElement) {
        try {
            const response = await fetch(`/api/comments/${commentId}/replies`);
            
            if (response.ok) {
                const replies = await response.json();
                const repliesContainer = parentElement.querySelector('.replies-container');
                
                for (const reply of replies) {
                    const replyElement = await createCommentElement(reply, true);
                    repliesContainer.appendChild(replyElement);
                }
            }
        } catch (error) {
            console.error('Error loading replies:', error);
        }
    }

    // Show reply form
    function showReplyForm(parentCommentId, parentElement) {
        // Remove any existing reply forms
        const existingForm = parentElement.querySelector('.reply-form');
        if (existingForm) {
            existingForm.remove();
            return;
        }

        const replyForm = document.createElement('div');
        replyForm.className = 'reply-form';
        replyForm.innerHTML = `
            <textarea placeholder="Write your reply..." rows="3" maxlength="2000"></textarea>
            <div class="form-actions">
                <button class="btn-success submit-reply">Post Reply</button>
                <button class="btn-secondary cancel-reply">Cancel</button>
            </div>
        `;

        const actions = parentElement.querySelector('.comment-actions');
        actions.after(replyForm);

        const textarea = replyForm.querySelector('textarea');
        const submitBtn = replyForm.querySelector('.submit-reply');
        const cancelBtn = replyForm.querySelector('.cancel-reply');

        textarea.focus();

        submitBtn.addEventListener('click', async () => {
            const content = textarea.value.trim();
            if (!content) {
                showMessage('Please enter a reply', 'error');
                return;
            }

            submitBtn.disabled = true;
            submitBtn.textContent = 'Posting...';

            try {
                const formData = new URLSearchParams();
                formData.append('articleId', ARTICLE_ID);
                formData.append('parentCommentId', parentCommentId);
                formData.append('content', content);

                const response = await fetch('/api/comments/reply', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData
                });

                if (response.ok) {
                    showMessage('Reply posted successfully!', 'success');
                    replyForm.remove();
                    loadComments(); // Reload all comments
                    updateCommentCount();
                } else {
                    const errorText = await response.text();
                    showMessage(errorText || 'Failed to post reply', 'error');
                }
            } catch (error) {
                console.error('Error posting reply:', error);
                showMessage('An error occurred while posting your reply', 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Post Reply';
            }
        });

        cancelBtn.addEventListener('click', () => {
            replyForm.remove();
        });
    }

    // Show edit form
    function showEditForm(commentId, currentContent, commentElement) {
        const contentDiv = commentElement.querySelector('.comment-content');
        const actionsDiv = commentElement.querySelector('.comment-actions');

        const editForm = document.createElement('div');
        editForm.className = 'edit-form';
        editForm.innerHTML = `
            <textarea rows="3" maxlength="2000">${escapeHtml(currentContent)}</textarea>
            <div class="form-actions">
                <button class="btn-success save-edit">Save</button>
                <button class="btn-secondary cancel-edit">Cancel</button>
            </div>
        `;

        contentDiv.style.display = 'none';
        actionsDiv.style.display = 'none';
        contentDiv.after(editForm);

        const textarea = editForm.querySelector('textarea');
        const saveBtn = editForm.querySelector('.save-edit');
        const cancelBtn = editForm.querySelector('.cancel-edit');

        textarea.focus();

        saveBtn.addEventListener('click', async () => {
            const newContent = textarea.value.trim();
            if (!newContent) {
                showMessage('Comment cannot be empty', 'error');
                return;
            }

            saveBtn.disabled = true;
            saveBtn.textContent = 'Saving...';

            try {
                const formData = new URLSearchParams();
                formData.append('content', newContent);

                const response = await fetch(`/api/comments/${commentId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData
                });

                if (response.ok) {
                    showMessage('Comment updated successfully!', 'success');
                    loadComments(); // Reload comments
                } else {
                    const errorText = await response.text();
                    showMessage(errorText || 'Failed to update comment', 'error');
                }
            } catch (error) {
                console.error('Error updating comment:', error);
                showMessage('An error occurred while updating your comment', 'error');
            } finally {
                saveBtn.disabled = false;
                saveBtn.textContent = 'Save';
            }
        });

        cancelBtn.addEventListener('click', () => {
            editForm.remove();
            contentDiv.style.display = 'block';
            actionsDiv.style.display = 'flex';
        });
    }

    // Delete comment
    async function deleteComment(commentId) {
        if (!confirm('Are you sure you want to delete this comment?')) {
            return;
        }

        try {
            const response = await fetch(`/api/comments/${commentId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                showMessage('Comment deleted successfully!', 'success');
                loadComments(); // Reload comments
                updateCommentCount();
            } else {
                const errorText = await response.text();
                showMessage(errorText || 'Failed to delete comment', 'error');
            }
        } catch (error) {
            console.error('Error deleting comment:', error);
            showMessage('An error occurred while deleting the comment', 'error');
        }
    }

    // Update comment count
    async function updateCommentCount() {
        try {
            const response = await fetch(`/api/comments/article/${ARTICLE_ID}/count`);
            if (response.ok) {
                const data = await response.json();
                document.getElementById('totalComments').textContent = data.count;
            }
        } catch (error) {
            console.error('Error updating comment count:', error);
        }
    }

    // Show message
    function showMessage(text, type = 'success') {
        // Remove any existing messages
        const existingMsg = document.querySelector('.message');
        if (existingMsg) {
            existingMsg.remove();
        }

        const message = document.createElement('div');
        message.className = `message ${type}`;
        message.textContent = text;

        const container = document.querySelector('.container');
        container.insertBefore(message, container.firstChild);

        // Auto-hide after 5 seconds
        setTimeout(() => {
            message.remove();
        }, 5000);
    }

    // Format date
    function formatDate(date) {
        const now = new Date();
        const diff = now - date;
        const seconds = Math.floor(diff / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (seconds < 60) return 'Just now';
        if (minutes < 60) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;
        if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`;
        if (days < 7) return `${days} day${days > 1 ? 's' : ''} ago`;
        
        return date.toLocaleDateString('en-US', { 
            year: 'numeric', 
            month: 'short', 
            day: 'numeric' 
        });
    }

    // Escape HTML to prevent XSS
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});

