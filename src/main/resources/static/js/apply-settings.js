// 모든 페이지에서 사용자 설정 적용
(async function() {
    try {
        // 로그인 확인
        const userResponse = await fetch('/api/settings/current-user');
        if (!userResponse.ok) {
            // 로그인 안 되어 있으면 기본 설정 유지
            return;
        }

        // 설정 불러오기
        const settingsResponse = await fetch('/api/settings');
        if (!settingsResponse.ok) {
            return;
        }

        const settings = await settingsResponse.json();

        // Dark Mode 적용
        if (settings.darkMode) {
            document.documentElement.setAttribute('data-theme', 'dark');
        }

        // Font Size 적용
        if (settings.fontSize) {
            document.body.classList.add(`font-${settings.fontSize}`);
        }

        // Font Style 적용
        if (settings.fontStyle) {
            document.body.classList.add(`font-${settings.fontStyle}`);
        }

        // Line Spacing 적용
        if (settings.lineSpacing) {
            document.body.classList.add(`line-${settings.lineSpacing}`);
        }

        // Page Width 적용
        if (settings.pageWidth === 'wide') {
            const container = document.querySelector('.container, .wiki-content, .wrap');
            if (container) {
                container.classList.add('wide');
            }
        }

    } catch (error) {
        console.error('Failed to apply user settings:', error);
    }
})();