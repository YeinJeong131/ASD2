// Badges Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const myBadgeCard = document.getElementById('myBadgeCard');
    const leaderboardList = document.getElementById('leaderboardList');
    const adminBadgeForm = document.getElementById('adminBadgeForm');
    const deleteBadgeBtn = document.getElementById('deleteBadgeBtn');

    // Load user's badge if logged in
    if (IS_LOGGED_IN) {
        loadMyBadge();
    }

    // Load leaderboard
    loadLeaderboard();

    // Admin form submission
    if (adminBadgeForm && IS_ADMIN) {
        adminBadgeForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const targetUserId = document.getElementById('targetUserId').value;
            const level = document.getElementById('badgeLevel').value;
            const count = document.getElementById('contributionCount').value;

            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Updating...';

            try {
                // Set badge level
                const levelFormData = new URLSearchParams();
                levelFormData.append('targetUserId', targetUserId);
                levelFormData.append('level', level);

                const levelResponse = await fetch('/api/badges/admin/set-level', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: levelFormData
                });

                if (!levelResponse.ok) {
                    const errorText = await levelResponse.text();
                    showMessage(errorText || 'Failed to set badge level', 'error');
                    return;
                }

                // Set contribution count if provided
                if (count) {
                    const countFormData = new URLSearchParams();
                    countFormData.append('targetUserId', targetUserId);
                    countFormData.append('count', count);

                    const countResponse = await fetch('/api/badges/admin/set-contributions', {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: countFormData
                    });

                    if (!countResponse.ok) {
                        const errorText = await countResponse.text();
                        showMessage(errorText || 'Failed to set contribution count', 'error');
                        return;
                    }
                }

                showMessage('Badge updated successfully!', 'success');
                adminBadgeForm.reset();
                loadLeaderboard(); // Refresh leaderboard
                
                // Refresh my badge if it's the current user
                if (targetUserId == USER_ID) {
                    loadMyBadge();
                }
            } catch (error) {
                console.error('Error updating badge:', error);
                showMessage('An error occurred while updating the badge', 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Update Badge';
            }
        });

        // Delete badge button
        if (deleteBadgeBtn) {
            deleteBadgeBtn.addEventListener('click', async function() {
                const targetUserId = document.getElementById('targetUserId').value;
                
                if (!targetUserId) {
                    showMessage('Please enter a user ID', 'error');
                    return;
                }

                if (!confirm(`Are you sure you want to delete the badge for user ${targetUserId}?`)) {
                    return;
                }

                this.disabled = true;
                this.textContent = 'Deleting...';

                try {
                    const response = await fetch(`/api/badges/admin/${targetUserId}`, {
                        method: 'DELETE'
                    });

                    if (response.ok) {
                        showMessage('Badge deleted successfully!', 'success');
                        adminBadgeForm.reset();
                        loadLeaderboard(); // Refresh leaderboard
                        
                        // Refresh my badge if it's the current user
                        if (targetUserId == USER_ID) {
                            loadMyBadge();
                        }
                    } else {
                        const errorText = await response.text();
                        showMessage(errorText || 'Failed to delete badge', 'error');
                    }
                } catch (error) {
                    console.error('Error deleting badge:', error);
                    showMessage('An error occurred while deleting the badge', 'error');
                } finally {
                    this.disabled = false;
                    this.textContent = 'Delete Badge';
                }
            });
        }
    }

    // Load user's badge
    async function loadMyBadge() {
        try {
            const response = await fetch('/api/badges/my-badge');
            
            if (response.ok) {
                const badge = await response.json();
                displayMyBadge(badge);
            } else {
                myBadgeCard.innerHTML = '<p>No badge found. Start contributing to earn your badge!</p>';
                myBadgeCard.classList.remove('loading');
            }
        } catch (error) {
            console.error('Error loading badge:', error);
            myBadgeCard.innerHTML = '<p class="error">Failed to load badge</p>';
            myBadgeCard.classList.remove('loading');
        }
    }

    // Display user's badge
    function displayMyBadge(badge) {
        const icon = getBadgeIcon(badge.level);
        const nextLevel = getNextLevel(badge.level);
        const nextThreshold = getThresholdForLevel(nextLevel);
        const progress = nextLevel ? Math.min((badge.contributionCount / nextThreshold) * 100, 100) : 100;

        myBadgeCard.className = `badge-card ${badge.level}`;
        myBadgeCard.innerHTML = `
            <div class="badge-icon">${icon}</div>
            <div class="badge-details">
                <div class="badge-level">${badge.level}</div>
                <p><strong>${badge.contributionCount}</strong> Contributions</p>
                <p>Badge earned on ${formatDate(new Date(badge.awardedDate))}</p>
                ${nextLevel ? `
                    <div class="badge-progress">
                        <p><strong>${nextThreshold - badge.contributionCount}</strong> more contributions to reach ${nextLevel}</p>
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${progress}%"></div>
                        </div>
                    </div>
                ` : '<p style="margin-top: 10px;">🎉 You\'ve reached the highest level!</p>'}
            </div>
        `;
    }

    // Load leaderboard
    async function loadLeaderboard() {
        try {
            const response = await fetch('/api/badges/all');
            
            if (response.ok) {
                const badges = await response.json();
                displayLeaderboard(badges);
            } else {
                leaderboardList.innerHTML = '<p class="error">Failed to load leaderboard</p>';
            }
        } catch (error) {
            console.error('Error loading leaderboard:', error);
            leaderboardList.innerHTML = '<p class="error">An error occurred while loading the leaderboard</p>';
        }
    }

    // Display leaderboard
    function displayLeaderboard(badges) {
        leaderboardList.innerHTML = '';
        
        if (badges.length === 0) {
            leaderboardList.innerHTML = '<p class="loading">No badges to display yet</p>';
            return;
        }

        badges.slice(0, 20).forEach((badge, index) => {
            const item = document.createElement('div');
            item.className = 'leaderboard-item';
            
            const rankDisplay = index < 3 ? 
                `<span class="rank-icon">${['🥇', '🥈', '🥉'][index]}</span>` :
                `<span class="rank">#${index + 1}</span>`;

            item.innerHTML = `
                ${rankDisplay}
                <div class="rank-icon">${getBadgeIcon(badge.level)}</div>
                <div class="user-details">
                    <div class="user-email">${escapeHtml(badge.user.email)}</div>
                    <div class="user-stats">
                        ${badge.level} Badge • ${badge.contributionCount} Contributions
                    </div>
                </div>
            `;
            
            leaderboardList.appendChild(item);
        });
    }

    // Get badge icon
    function getBadgeIcon(level) {
        const icons = {
            'BRONZE': '🥉',
            'SILVER': '🥈',
            'GOLD': '🥇',
            'PLATINUM': '💎'
        };
        return icons[level] || '🏅';
    }

    // Get next badge level
    function getNextLevel(currentLevel) {
        const levels = ['BRONZE', 'SILVER', 'GOLD', 'PLATINUM'];
        const currentIndex = levels.indexOf(currentLevel);
        return currentIndex < levels.length - 1 ? levels[currentIndex + 1] : null;
    }

    // Get threshold for a badge level
    function getThresholdForLevel(level) {
        const thresholds = {
            'BRONZE': 0,
            'SILVER': 20,
            'GOLD': 50,
            'PLATINUM': 100
        };
        return thresholds[level] || 0;
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
        return date.toLocaleDateString('en-US', { 
            year: 'numeric', 
            month: 'long', 
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

