export function textToVoice(
    {
        b = '#voice',
        i = '#text',
        defaultText = 'hello, thanks for using Betterpedia',
        volume = 1,
        pauseBtn, resumeBtn, stopBtn
    } = {}
) {
    const btn = document.querySelector(b);
    const input = document.querySelector(i);
    const p = pauseBtn ? document.querySelector(pauseBtn) : null;
    const r = resumeBtn ? document.querySelector(resumeBtn) : null;
    const s = stopBtn   ? document.querySelector(stopBtn)   : null;

    let currentUtterance = null;

    function setState(state) {
        const speaking = state === 'speaking';
        const paused   = state === 'paused';

        if (p) p.disabled = !speaking;          //pause only while speaking
        if (r) r.disabled = !paused;            //resume only while paused
        if (s) s.disabled = !(speaking || paused); //stop while speaking/paused

        if (btn) btn.disabled = speaking || paused;
    }

    function pickVoice() {
        const vs = speechSynthesis.getVoices() || [];
        return (
            vs.find(v => v.lang && v.lang.toLowerCase() === 'en-us') ||
            vs.find(v => v.lang && v.lang.toLowerCase().startsWith('en-')) ||
            null
        );
    }

    function speak(text) {
        const t = (text ?? '').trim() || defaultText;

        // If already talking or queued, cancel and start fresh
        if (speechSynthesis.speaking || speechSynthesis.pending) speechSynthesis.cancel();

        const utter = new SpeechSynthesisUtterance(t);
        currentUtterance = utter;

        const voice = pickVoice();
        if (voice) { utter.voice = voice; utter.lang = voice.lang; }
        else { utter.lang = 'en-us'; }

        // lock rate & pitch to 1
        utter.rate = 1;
        utter.pitch = 1;
        utter.volume = volume;

        const original = btn?.textContent ?? '';
        if (btn) btn.textContent = 'speaking...';

        const reset = () => {
            if (btn) btn.textContent = original;
            setState('idle');
            currentUtterance = null;
        };

        utter.onstart  = () => setState('speaking');
        utter.onpause  = () => setState('paused');
        utter.onresume = () => setState('speaking');
        utter.onend    = reset;
        utter.onerror  = reset;

        speechSynthesis.speak(utter);
    }

    const primeVoices = () => speechSynthesis.getVoices();
    speechSynthesis.addEventListener?.('voiceschanged', primeVoices);

    primeVoices();

    btn?.addEventListener('click', () => speak(input ? input.value : btn?.dataset?.text));

    p?.addEventListener('click', () => {
        if (speechSynthesis.speaking && !speechSynthesis.paused) {
            speechSynthesis.pause();
        }
    });

    r?.addEventListener('click', () => {
        if (speechSynthesis.paused) {
            speechSynthesis.resume();
        }
    });

    s?.addEventListener('click', () => {
        if (speechSynthesis.speaking || speechSynthesis.paused || speechSynthesis.pending) {
            speechSynthesis.cancel();
            if (currentUtterance) currentUtterance = null;
            setState('idle');
            if (btn) btn.textContent = btn.dataset?.originalText || 'Text to voice';
        }
    });

    setState('idle');

    return { speak };
}
