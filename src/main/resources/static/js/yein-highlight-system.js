
document.addEventListener('DOMContentLoaded', function() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    }
});
class YeinHighlightSystem {
    constructor(options = {}) {
        // 설정 옵션
        this.config = {
            containerSelector: options.containerSelector || '.wiki-content',
            apiEndpoint: options.apiEndpoint || '/api/notes',
            userId: options.userId || 1,
            cssPrefix: 'yein-highlight',
            ...options
        };

        // 내부 상태
        this.highlights = [];
        this.highlightCounter = 1;
        this.selectedRange = null;
        this.selectedText = '';
        this.tooltip = null;
        this.isInitialized = false;

        this.init();
    }

    /**
     * 시스템 초기화
     */
    init() {
        if (this.isInitialized) return;

        console.log('Yein Highlight System 초기화 중...');

        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this._initialize());
        } else {
            this._initialize();
        }
    }

    /**
     * 실제 초기화 로직
     */
    _initialize() {
        const container = document.querySelector(this.config.containerSelector);
        if (!container) {
            console.log('Wiki content not found. Highlight system standby.');
            return;
        }

        try {
            this.createTooltip();
            this.attachEventListeners();
            this.loadExistingHighlights();

            this.isInitialized = true;
            console.log('Highlight system ready!');

        } catch (error) {
            console.error('Highlight system initialization failed:', error);
        }
    }

    /**
     * 툴팁 UI 생성
     */
    createTooltip() {
        const existingTooltip = document.getElementById(`${this.config.cssPrefix}-tooltip`);
        if (existingTooltip) {
            existingTooltip.remove();
        }

        this.tooltip = document.createElement('div');
        this.tooltip.id = `${this.config.cssPrefix}-tooltip`;
        this.tooltip.className = `${this.config.cssPrefix}-tooltip`;
        this.tooltip.innerHTML = this.getTooltipHTML();

        document.body.appendChild(this.tooltip);
    }

    /**
     * 툴팁 HTML 생성
     */
    getTooltipHTML() {
        return `
            <button onclick="yeinHighlight.createHighlight('yellow')" class="tooltip-btn yellow">
                📝 Yellow
            </button>
            <button onclick="yeinHighlight.createHighlight('blue')" class="tooltip-btn blue">
                🔵 Blue
            </button>
            <button onclick="yeinHighlight.createHighlight('green')" class="tooltip-btn green">
                🟢 Green
            </button>
            <button onclick="yeinHighlight.addNote()" class="tooltip-btn note">
                📋 Note
            </button>
        `;
    }

    /**
     * 이벤트 리스너 등록
     */
    attachEventListeners() {
        document.addEventListener('mouseup', (e) => this.handleTextSelection(e));
        document.addEventListener('mousedown', (e) => this.handleClickOutside(e));
        window.addEventListener('beforeunload', () => this.cleanup());
    }

    /**
     * 텍스트 선택 처리
     */
    handleTextSelection(event) {
        setTimeout(() => {
            const selection = window.getSelection();
            const text = selection.toString().trim();

            if (!text) {
                this.hideTooltip();
                return;
            }

            const container = document.querySelector(this.config.containerSelector);
            if (!container || !this.isSelectionInContainer(selection, container)) {
                this.hideTooltip();
                return;
            }

            if (this.isInsideHighlight(selection)) {
                this.showEditTooltip(event, selection);
                return;
            }

            this.selectedText = text;
            this.selectedRange = selection.getRangeAt(0).cloneRange();

            console.log('📝 Text selected:', text.substring(0, 50) + '...');
            this.showTooltip(event);

        }, 100);
    }

    /**
     * 선택이 지정된 컨테이너 내에 있는지 확인
     */
    isSelectionInContainer(selection, container) {
        if (selection.rangeCount === 0) return false;
        const range = selection.getRangeAt(0);
        return container.contains(range.commonAncestorContainer);
    }

    /**
     * 선택이 기존 하이라이트 내부에 있는지 확인
     */
    isInsideHighlight(selection) {
        if (selection.rangeCount === 0) return false;

        const range = selection.getRangeAt(0);
        let parent = range.commonAncestorContainer;

        if (parent.nodeType === Node.TEXT_NODE) {
            parent = parent.parentElement;
        }

        return parent.closest(`.${this.config.cssPrefix}`) !== null;
    }

    /**
     * 새 하이라이트용 툴팁 표시
     */
    showTooltip(event) {
        if (!this.tooltip) return;

        this.tooltip.innerHTML = this.getTooltipHTML();
        this.positionTooltip(event);
        this.tooltip.style.display = 'block';
    }

    /**
     * 기존 하이라이트 편집용 툴팁
     */
    showEditTooltip(event, selection) {
        const highlightElement = selection.getRangeAt(0).commonAncestorContainer.parentElement.closest(`.${this.config.cssPrefix}`);
        if (!highlightElement) return;

        const highlightId = highlightElement.id;

        this.tooltip.innerHTML = `
            <button onclick="yeinHighlight.deleteHighlight('${highlightId}')" class="tooltip-btn delete">
                🗑️ 삭제
            </button>
            <button onclick="yeinHighlight.editNote('${highlightId}')" class="tooltip-btn edit">
                ✏️ 노트 편집
            </button>
        `;

        this.positionTooltip(event);
        this.tooltip.style.display = 'block';
    }

    /**
     * 툴팁 위치 설정
     */
    positionTooltip(event) {
        const x = event.pageX;
        const y = event.pageY;

        this.tooltip.style.left = (x - 60) + 'px';
        this.tooltip.style.top = (y - 60) + 'px';

        // 화면 경계 조정
        requestAnimationFrame(() => {
            const rect = this.tooltip.getBoundingClientRect();

            if (rect.right > window.innerWidth - 10) {
                this.tooltip.style.left = (window.innerWidth - rect.width - 10) + 'px';
            }

            if (rect.top < 10) {
                this.tooltip.style.top = (y + 20) + 'px';
            }
        });
    }

    /**
     * 툴팁 숨기기
     */
    hideTooltip() {
        if (this.tooltip) {
            this.tooltip.style.display = 'none';
        }
        this.selectedRange = null;
        this.selectedText = '';
    }

    /**
     * 외부 클릭 처리
     */
    handleClickOutside(event) {
        if (!event.target.closest(`.${this.config.cssPrefix}-tooltip`)) {
            this.hideTooltip();
        }
    }

    /**
     * 하이라이트 생성
     */
    createHighlight(color = 'yellow') {
        if (!this.selectedRange || !this.selectedText) {
            this.showMessage('텍스트를 먼저 선택해주세요.', 'warning');
            return;
        }

        try {
            const highlightId = `${this.config.cssPrefix}-${this.highlightCounter++}`;

            // span 요소 생성
            const span = document.createElement('span');
            span.className = `${this.config.cssPrefix} ${color}`;
            span.id = highlightId;
            span.title = '클릭하여 편집';

            // 클릭 이벤트 추가
            span.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                this.showHighlightMenu(e, highlightId);
            });

            // 텍스트 감싸기
            this.selectedRange.surroundContents(span);

            // 데이터 생성 및 저장
            const highlightData = this.createHighlightData(highlightId, color);
            this.highlights.push(highlightData);

            console.log('✨ Highlight created:', highlightId);

            // UI 정리
            window.getSelection().removeAllRanges();
            this.hideTooltip();

            // 서버 저장
            this.saveToServer(highlightData);

            this.showMessage('highlight is generated!', 'success');

        } catch (error) {
            console.error('Highlight creation failed:', error);
            this.showMessage('failed to generated highlight.', 'error');
        }
    }

    /**
     * 하이라이트 데이터 객체 생성
     */
    createHighlightData(highlightId, color, noteContent = null) {
        const positionData = {
            highlightId: highlightId,
            startOffset: this.selectedRange.startOffset,
            endOffset: this.selectedRange.endOffset,
            textLength: this.selectedText.length,
            timestamp: new Date().toISOString()
        };

        return {
            userId: this.config.userId,
            pageUrl: window.location.pathname,
            highlightedText: this.selectedText,
            noteContent: noteContent,
            position: JSON.stringify(positionData),
            highlightColour: color,
            frontendId: highlightId,
            createdAt: new Date(),
            isUnsaved: true
        };
    }

    /**
     * 노트 추가
     */
    addNote() {
        if (!this.selectedRange || !this.selectedText) {
            this.showMessage('텍스트를 먼저 선택해주세요.', 'warning');
            return;
        }

        const noteContent = prompt('노트를 입력하세요:', '');
        if (!noteContent) return;

        this.createHighlightWithNote('yellow', noteContent);
    }

    /**
     * 하이라이트와 노트 함께 생성
     */
    createHighlightWithNote(color, noteContent) {
        try {
            const highlightId = `${this.config.cssPrefix}-${this.highlightCounter++}`;

            const span = document.createElement('span');
            span.className = `${this.config.cssPrefix} ${color} has-note`;
            span.id = highlightId;
            span.title = `노트: ${noteContent.substring(0, 50)}${noteContent.length > 50 ? '...' : ''}`;

            span.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                this.showNotePopup(highlightId, noteContent);
            });

            this.selectedRange.surroundContents(span);

            const highlightData = this.createHighlightData(highlightId, color, noteContent);
            this.highlights.push(highlightData);

            console.log('✨ Highlight + Note created:', highlightId);

            // UI 정리
            window.getSelection().removeAllRanges();
            this.hideTooltip();

            // 서버 저장
            this.saveToServer(highlightData);

            this.showMessage('highlight and note are generated!', 'success');

        } catch (error) {
            console.error('Highlight + Note creation failed:', error);
            this.showMessage('failed to generate.', 'error');
        }
    }

    /**
     * 서버에 저장
     */
    async saveToServer(highlightData) {
        try {
            console.log('Saving to server:', highlightData.frontendId);

            const formData = new FormData();
            formData.append('pageUrl', highlightData.pageUrl);
            formData.append('highlightedText', highlightData.highlightedText);
            formData.append('noteContent', highlightData.noteContent || '');
            formData.append('position', highlightData.position);
            formData.append('highlightColor', highlightData.highlightColour);

            const response = await fetch(this.config.apiEndpoint, {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                const savedNote = await response.json();

                // 저장 성공 처리
                const highlight = this.highlights.find(h => h.frontendId === highlightData.frontendId);
                if (highlight) {
                    highlight.noteId = savedNote.noteId;
                    highlight.isUnsaved = false;
                    highlight.savedAt = new Date();
                }

                console.log('Saved successfully:', savedNote.noteId);

            } else {
                console.error('Save failed:', response.status);
                this.showMessage('failed to store in server.', 'error');
            }

        } catch (error) {
            console.error('Save error:', error);
            this.showMessage('저장 중 오류가 발생했습니다.', 'error');
        }
    }

    /**
     * 하이라이트 삭제
     */
    deleteHighlight(highlightId) {
        if (!confirm('do u want to delete this highlight?')) return;

        const element = document.getElementById(highlightId);
        if (!element) return;

        try {
            // DOM에서 제거
            const parent = element.parentNode;
            while (element.firstChild) {
                parent.insertBefore(element.firstChild, element);
            }
            parent.removeChild(element);

            // 데이터에서 제거
            const highlightIndex = this.highlights.findIndex(h => h.frontendId === highlightId);
            if (highlightIndex > -1) {
                const highlight = this.highlights[highlightIndex];
                this.highlights.splice(highlightIndex, 1);

                console.log('🗑️ Highlight deleted:', highlightId);

                // 서버에서도 삭제 (noteId가 있는 경우)
                if (highlight.noteId) {
                    this.deleteFromServer(highlight.noteId);
                }
            }

            this.hideTooltip();

        } catch (error) {
            console.error('Delete failed:', error);
            this.showMessage('failed to delete.', 'error');
        }
    }

    /**
     * 서버에서 삭제
     */
    async deleteFromServer(noteId) {
        try {
            const response = await fetch(`${this.config.apiEndpoint}/${noteId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                console.log('Deleted from server:', noteId);
            } else {
                console.error(' Server delete failed:', response.status);
            }
        } catch (error) {
            console.error('Delete error:', error);
        }
    }

    /**
     * 기존 하이라이트 로드
     */
    async loadExistingHighlights() {
        try {
            console.log('Loading existing highlights...');

            const response = await fetch(`${this.config.apiEndpoint}/page?url=${encodeURIComponent(window.location.pathname)}`);

            if (response.ok) {
                const notes = await response.json();
                notes.forEach(note => this.restoreHighlight(note));
                console.log(`Loaded ${notes.length} highlights`);
            }
        } catch (error) {
            console.error('Load failed:', error);
        }
    }

    /**
     * 하이라이트 복원 (서버 데이터로부터)
     */
    restoreHighlight(noteData) {
        try {
            console.log('Restoring highlight:', noteData.noteId);
            // TODO: position 데이터를 사용해서 텍스트 위치 찾고 하이라이트 적용
        } catch (error) {
            console.error(' Restore failed:', error);
        }
    }

    /**
     * 메시지 표시 (토스트)
     */
    showMessage(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `${this.config.cssPrefix}-toast ${type}`;
        toast.textContent = message;

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3000);
    }

    /**
     * 정리 작업
     */
    cleanup() {
        this.hideTooltip();
        if (this.tooltip) {
            this.tooltip.remove();
        }
    }

    /**
     * 현재 하이라이트 목록 반환
     */
    getHighlights() {
        return [...this.highlights];
    }

    /**
     * 모든 하이라이트 제거
     */
    clearAllHighlights() {
        if (!confirm(`Do you want to remove all highlights (${this.highlights.length})?`)) return;

        document.querySelectorAll(`.${this.config.cssPrefix}`).forEach(el => {
            const parent = el.parentNode;
            while (el.firstChild) {
                parent.insertBefore(el.firstChild, el);
            }
            parent.removeChild(el);
        });

        this.highlights = [];
        this.hideTooltip();

        console.log('🧹 All highlights cleared');
    }

    /**
     * 통계 정보 반환
     */
    getStats() {
        return {
            total: this.highlights.length,
            saved: this.highlights.filter(h => !h.isUnsaved).length,
            unsaved: this.highlights.filter(h => h.isUnsaved).length,
            withNotes: this.highlights.filter(h => h.noteContent).length
        };
    }
}

// ========================================
// 글로벌 인스턴스 및 초기화
// ========================================

let yeinHighlight = null;

// 자동 초기화
document.addEventListener('DOMContentLoaded', function() {
    if (document.querySelector('.wiki-content')) {
        yeinHighlight = new YeinHighlightSystem({
            containerSelector: '.wiki-content',
            userId: window.currentUserId || 1,
            apiEndpoint: '/api/notes'
        });

        console.log("Yein's Highlight System has been activated!");



    }
});

// 글로벌 API
window.YeinHighlightSystem = {
    getInstance: () => yeinHighlight,
    createHighlight: (color) => yeinHighlight?.createHighlight(color),
    getHighlights: () => yeinHighlight?.getHighlights() || [],
    clearAll: () => yeinHighlight?.clearAllHighlights(),
    getStats: () => yeinHighlight?.getStats() || {}
};