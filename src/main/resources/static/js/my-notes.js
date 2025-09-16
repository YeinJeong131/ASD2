// My Notes 페이지 JavaScript
console.log('My Notes page loaded');

/**
 * 뷰 토글 (All Notes vs By Page)
 */
function toggleView() {
    const allNotesSection = document.getElementById('all-notes');
    const byPageSection = document.getElementById('by-page');

    if (allNotesSection.style.display === 'none') {
        allNotesSection.style.display = 'block';
        byPageSection.style.display = 'none';
        updateActiveNav('all-notes');
    } else {
        allNotesSection.style.display = 'none';
        byPageSection.style.display = 'block';
        updateActiveNav('by-page');
    }
}

/**
 * 네비게이션 활성 상태 업데이트
 */
function updateActiveNav(activeId) {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });

    const activeLink = document.querySelector(`a[href="#${activeId}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }
}

/**
 * 노트 편집
 */
function editNote(noteId) {
    console.log('Editing note:', noteId);

    // 현재 노트 데이터 찾기
    const noteCard = document.querySelector(`[data-note-id="${noteId}"]`);
    if (!noteCard) {
        alert('Note not found');
        return;
    }

    // 현재 값들 가져오기
    const currentNoteContent = noteCard.querySelector('.note-content p')?.textContent || '';
    const currentColor = noteCard.querySelector('.highlight-badge')?.textContent.trim() || 'yellow';

    // 모달에 현재 값들 설정
    document.getElementById('editNoteId').value = noteId;
    document.getElementById('editNoteContent').value = currentNoteContent;
    document.getElementById('editHighlightColor').value = currentColor;

    // 모달 표시
    const modal = new bootstrap.Modal(document.getElementById('editNoteModal'));
    modal.show();
}

/**
 * 노트 변경사항 저장
 */
async function saveNoteChanges() {
    const noteId = document.getElementById('editNoteId').value;
    const noteContent = document.getElementById('editNoteContent').value;
    const highlightColor = document.getElementById('editHighlightColor').value;

    try {
        const formData = new FormData();
        if (noteContent.trim()) {
            formData.append('noteContent', noteContent);
        }
        formData.append('highlightColor', highlightColor);

        const response = await fetch(`/api/notes/${noteId}`, {
            method: 'PUT',
            body: formData
        });

        if (response.ok) {
            const updatedNote = await response.json();

            // 페이지에서 해당 노트 카드 업데이트
            updateNoteCardInDOM(noteId, noteContent, highlightColor);

            // 모달 닫기
            const modal = bootstrap.Modal.getInstance(document.getElementById('editNoteModal'));
            modal.hide();

            showToast('Note updated successfully!', 'success');

        } else {
            throw new Error('Failed to update note');
        }

    } catch (error) {
        console.error('Error updating note:', error);
        showToast('Failed to update note', 'error');
    }
}

/**
 * DOM에서 노트 카드 업데이트
 */
function updateNoteCardInDOM(noteId, noteContent, highlightColor) {
    const noteCards = document.querySelectorAll(`[data-note-id="${noteId}"]`);

    noteCards.forEach(card => {
        // 노트 내용 업데이트
        const noteContentDiv = card.querySelector('.note-content p');
        if (noteContentDiv) {
            noteContentDiv.textContent = noteContent;
        }

        // 하이라이트 색상 업데이트
        const badge = card.querySelector('.highlight-badge');
        if (badge) {
            badge.className = `badge highlight-badge ${highlightColor}`;
            badge.textContent = highlightColor;
        }

        // 하이라이트된 텍스트 div 색상도 업데이트
        const highlightedTextDiv = card.querySelector('.highlighted-text');
        if (highlightedTextDiv) {
            highlightedTextDiv.setAttribute('data-color', highlightColor);
        }
    });
}

/**
 * 노트 삭제
 */
async function deleteNote(noteId) {
    if (!confirm('Are you sure you want to delete this note? This action cannot be undone.')) {
        return;
    }

    try {
        const response = await fetch(`/api/notes/${noteId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            // DOM에서 해당 노트 카드들 제거
            const noteCards = document.querySelectorAll(`[data-note-id="${noteId}"]`);
            noteCards.forEach(card => {
                card.remove();
            });

            // 카운트 업데이트
            updateNoteCounts();

            showToast('Note deleted successfully!', 'success');

        } else {
            throw new Error('Failed to delete note');
        }

    } catch (error) {
        console.error('Error deleting note:', error);
        showToast('Failed to delete note', 'error');
    }
}

/**
 * 노트 개수 업데이트
 */
function updateNoteCounts() {
    const allNotes = document.querySelectorAll('#all-notes .note-card').length;
    const pageGroups = document.querySelectorAll('#by-page .page-group').length;

    // 사이드바 카운트 업데이트
    const allNotesLink = document.querySelector('a[href="#all-notes"] span');
    const byPageLink = document.querySelector('a[href="#by-page"] span');

    if (allNotesLink) allNotesLink.textContent = allNotes;
    if (byPageLink) byPageLink.textContent = pageGroups;

    // 빈 상태 체크
    if (allNotes === 0) {
        document.getElementById('all-notes').innerHTML = `
            <div class="text-center py-5">
                <i class="fas fa-sticky-note fa-3x text-muted mb-3"></i>
                <h4 class="text-muted">No notes yet</h4>
                <p class="text-muted">Start highlighting text on wiki pages to create your first note!</p>
                <a href="/wiki" class="btn btn-primary">
                    <i class="fas fa-book me-2"></i>Go to Wiki
                </a>
            </div>
        `;
    }
}

/**
 * 토스트 메시지 표시
 */
function showToast(message, type = 'info') {
    // 기존 토스트 제거
    const existingToast = document.querySelector('.custom-toast');
    if (existingToast) {
        existingToast.remove();
    }

    // 새 토스트 생성
    const toast = document.createElement('div');
    toast.className = `custom-toast alert alert-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'info'} alert-dismissible`;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
    `;

    toast.innerHTML = `
        ${message}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
    `;

    document.body.appendChild(toast);

    // 3초 후 자동 제거
    setTimeout(() => {
        if (toast.parentElement) {
            toast.remove();
        }
    }, 3000);
}

/**
 * 사이드바 네비게이션
 */
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 링크 클릭 처리
    document.querySelectorAll('.sidebar .nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');

            if (href === '#all-notes') {
                e.preventDefault();
                document.getElementById('all-notes').style.display = 'block';
                document.getElementById('by-page').style.display = 'none';
                updateActiveNav('all-notes');
            } else if (href === '#by-page') {
                e.preventDefault();
                document.getElementById('all-notes').style.display = 'none';
                document.getElementById('by-page').style.display = 'block';
                updateActiveNav('by-page');
            }
        });
    });
});