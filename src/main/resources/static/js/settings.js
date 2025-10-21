// Settings page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    }
});

document.addEventListener('DOMContentLoaded', async function() {
    // const USER_ID = 1; // Default user ID for testing
    // 나중에 세션 방식으로 변경
    // const USER_ID = await getCurrentUserId();
    // bring user id
    let USER_ID;

    try {
        const userResponse = await fetch('/api/settings/current-user');
        if (userResponse.ok) {
            const userData = await userResponse.json();
            USER_ID = userData.id;
            console.log('Logged in as user:', USER_ID);
        } else {
            console.error('Not logged in, redirecting...');
            window.location.href = '/login';
            return;
        }
    } catch (error) {
        console.error('Failed to get current user:', error);
        window.location.href = '/login';
        return;
    }

    // DOM elements
    const form = document.getElementById('settingsForm');
    const darkModeToggle = document.getElementById('darkMode');
    const fontSizeSelect = document.getElementById('fontSize');
    const fontStyleSelect = document.getElementById('fontStyle');
    const lineSpacingSelect = document.getElementById('lineSpacing');
    const pageWidthRadios = document.querySelectorAll('input[name="pageWidth"]');
    const dateFormatSelect = document.getElementById('dateFormat');
    const timeOffsetSelect = document.getElementById('timeOffset');
    const saveBtn = document.getElementById('saveBtn');
    const resetBtn = document.getElementById('resetBtn');
    const statusMessage = document.getElementById('statusMessage');

    // Preview elements
    const previewTexts = document.querySelectorAll('.preview-text');
    const datePreview = document.getElementById('datePreview');
    const previewDate = document.getElementById('previewDate');

    // Load settings on page load
    loadCurrentSettings();

    // Event listeners for real-time preview
    darkModeToggle.addEventListener('change', applyDarkMode);
    fontSizeSelect.addEventListener('change', applyFontSize);
    fontStyleSelect.addEventListener('change', applyFontStyle);
    lineSpacingSelect.addEventListener('change', applyLineSpacing);
    pageWidthRadios.forEach(radio => radio.addEventListener('change', applyPageWidth));
    dateFormatSelect.addEventListener('change', updateDatePreview);

    // Button event listeners
    saveBtn.addEventListener('click', saveSettings);
    resetBtn.addEventListener('click', resetToDefaults);

    // Load current settings from server
    async function loadCurrentSettings() {
        try {
            showStatus('Loading settings...', 'info');

            // ✅ 수정: USER_ID 제거
            const response = await fetch('/api/settings');

            if (response.status === 401) {
                window.location.href = '/login';
                return;
            }

            if (!response.ok) {
                throw new Error('Failed to load settings');
            }

            const settings = await response.json();

            // Apply settings to form (기본값 적용)
            darkModeToggle.checked = settings.darkMode || false;
            fontSizeSelect.value = settings.fontSize || 'medium';
            fontStyleSelect.value = settings.fontStyle || 'arial';
            lineSpacingSelect.value = settings.lineSpacing || 'normal';
            dateFormatSelect.value = settings.dateFormat || 'DD/MM/YYYY';
            timeOffsetSelect.value = settings.timeOffset || 9;

            // Set page width radio
            const pageWidth = settings.pageWidth || 'fixed';
            const pageWidthRadio = document.querySelector(`input[name="pageWidth"][value="${pageWidth}"]`);
            if (pageWidthRadio) {
                pageWidthRadio.checked = true;
            }

            // Apply all settings immediately
            applyAllSettings();
            hideStatus();

        } catch (error) {
            console.error('Failed to load settings:', error);
            showStatus('Failed to load settings: ' + error.message, 'error');
        }
    }

    // Apply all current settings
    function applyAllSettings() {
        applyDarkMode();
        applyFontSize();
        applyFontStyle();
        applyLineSpacing();
        applyPageWidth();
        updateDatePreview();
    }

    // Apply dark mode
    function applyDarkMode() {
        if (darkModeToggle.checked) {
            document.documentElement.setAttribute('data-theme', 'dark');
        } else {
            document.documentElement.removeAttribute('data-theme');
        }
    }

    // Apply font size
    function applyFontSize() {
        const size = fontSizeSelect.value;
        document.body.className = document.body.className.replace(/font-\w+/g, '');
        document.body.classList.add(`font-${size}`);

        previewTexts.forEach(text => {
            text.className = text.className.replace(/font-\w+/g, '');
            text.classList.add(`font-${size}`);
        });
    }

    // Apply font style
    function applyFontStyle() {
        const style = fontStyleSelect.value;
        document.body.className = document.body.className.replace(/font-(arial|serif|sans-serif|monospace)/g, '');
        document.body.classList.add(`font-${style}`);

        previewTexts.forEach(text => {
            text.className = text.className.replace(/font-(arial|serif|sans-serif|monospace)/g, '');
            text.classList.add(`font-${style}`);
        });
    }

    // Apply line spacing
    function applyLineSpacing() {
        const spacing = lineSpacingSelect.value;
        document.body.className = document.body.className.replace(/line-\w+/g, '');
        document.body.classList.add(`line-${spacing}`);

        previewTexts.forEach(text => {
            text.className = text.className.replace(/line-\w+/g, '');
            text.classList.add(`line-${spacing}`);
        });
    }

    // Apply page width
    function applyPageWidth() {
        const width = document.querySelector('input[name="pageWidth"]:checked').value;
        const container = document.querySelector('.container');

        if (width === 'wide') {
            container.classList.add('wide');
        } else {
            container.classList.remove('wide');
        }
    }

    // Update date preview
    function updateDatePreview() {
        const format = dateFormatSelect.value;
        const today = new Date();

        let formattedDate;
        const day = today.getDate().toString().padStart(2, '0');
        const month = (today.getMonth() + 1).toString().padStart(2, '0');
        const year = today.getFullYear();

        switch (format) {
            case 'DD/MM/YYYY':
                formattedDate = `${day}/${month}/${year}`;
                break;
            case 'MM/DD/YYYY':
                formattedDate = `${month}/${day}/${year}`;
                break;
            case 'YYYY-MM-DD':
                formattedDate = `${year}-${month}-${day}`;
                break;
            default:
                formattedDate = `${day}/${month}/${year}`;
        }

        datePreview.textContent = formattedDate;
        previewDate.textContent = formattedDate;
    }

    // Save settings to server
    async function saveSettings() {
        try {
            showStatus('Saving settings...', 'info');

            const settings = {
                darkMode: darkModeToggle.checked,
                fontSize: fontSizeSelect.value,
                fontStyle: fontStyleSelect.value,
                lineSpacing: lineSpacingSelect.value,
                pageWidth: document.querySelector('input[name="pageWidth"]:checked').value,
                dateFormat: dateFormatSelect.value,
                timeOffset: parseInt(timeOffsetSelect.value)
            };

            const response = await fetch('/api/settings', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(settings)
            });

            if (response.status === 401) {
                window.location.href = '/login';
                return;
            }

            if (response.ok) {
                const savedSettings = await response.json();
                console.log('Settings saved:', savedSettings);
                showStatus('Settings saved successfully!', 'success');
            } else {
                const errorText = await response.text();
                throw new Error(errorText);
            }

        } catch (error) {
            console.error('Failed to save settings:', error);
            showStatus('Failed to save settings: ' + error.message, 'error');
        }
    }

    // Reset to default settings
    async function resetToDefaults() {
        if (confirm('Are you sure you want to reset all settings to defaults?')) {
            try {
                showStatus('Resetting to defaults...', 'info');

                // Reset form to defaults
                darkModeToggle.checked = false;
                fontSizeSelect.value = 'medium';
                fontStyleSelect.value = 'arial';
                lineSpacingSelect.value = 'normal';
                dateFormatSelect.value = 'DD/MM/YYYY';
                timeOffsetSelect.value = '9';
                document.querySelector('input[name="pageWidth"][value="fixed"]').checked = true;

                // Apply default settings
                applyAllSettings();

                // Save to server
                await saveSettings();

                showStatus('Settings reset to defaults!', 'success');

            } catch (error) {
                console.error('Failed to reset settings:', error);
                showStatus('Failed to reset settings', 'error');
            }
        }
    }

    // Show status message
    function showStatus(message, type) {
        statusMessage.textContent = message;
        statusMessage.className = `status-message ${type}`;
        statusMessage.style.display = 'block';

        if (type === 'success') {
            setTimeout(hideStatus, 3000);
        }
    }

    // Hide status message
    function hideStatus() {
        statusMessage.style.display = 'none';
    }
});