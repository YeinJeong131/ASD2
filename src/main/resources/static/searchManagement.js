// searchManagement.js — minimal & robust (author/body/date)

const apply     = document.getElementById('apply');
const clearBtn  = document.getElementById('clear');
const fAuthor   = document.getElementById('fAuthor');
const fBody     = document.getElementById('fBody');
const fDate     = document.getElementById('fDate');
const results   = document.getElementById('results');
const matchInfo = document.getElementById('matchInfo');

function renderTitles(titles = []) {
    results.innerHTML = titles.map(t => `<li>${t}</li>`).join('') || '<li>No matches</li>';
    matchInfo.textContent = `${titles.length} match${titles.length === 1 ? '' : 'es'}`;
}

apply.onclick = async (e) => {
    e.preventDefault();

    const author = (fAuthor.value || '').trim();
    const body   = (fBody.value   || '').trim();
    const date   = fDate.value || ''; // yyyy-mm-dd or ''

    // nothing filled -> clear shown results, leave picker untouched
    if (!author && !body && !date) {
        results.innerHTML = '';
        matchInfo.textContent = '';
        return;
    }

    // choose endpoint
    let url;
    if (author && !body && !date) {
        // current backend: titles by author
        url = `/articles/search/author?authorName=${encodeURIComponent(author)}`;
    } else {
        // future/combined backend: any combo
        const qs = new URLSearchParams();
        if (author) qs.set('authorName', author);
        if (body)   qs.set('body', body);
        if (date)   qs.set('date', date); // already ISO
        url = `/articles/search?${qs.toString()}`;
    }

    // simple loading hint
    matchInfo.textContent = 'Searching…';

    try {
        const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
        const data = res.ok ? await res.json() : [];

        // both endpoints return a list of titles (strings)
        const titles = Array.isArray(data) ? data.filter(Boolean) : [];
        renderTitles(titles);
    } catch (err) {
        console.error(err);
        results.innerHTML = '<li>Search failed</li>';
        matchInfo.textContent = '';
    }
};

clearBtn.onclick = (e) => {
    e.preventDefault();
    fAuthor.value = '';
    fBody.value   = '';
    fDate.value   = '';
    results.innerHTML = '';
    matchInfo.textContent = '';
};
