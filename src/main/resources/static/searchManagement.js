const apply     = document.getElementById('apply');
const clearBtn  = document.getElementById('clear');
const fAuthor   = document.getElementById('fAuthor');
const fBody     = document.getElementById('fBody');
const fDate     = document.getElementById('fDate');
const results   = document.getElementById('results');
const matchInfo = document.getElementById('matchInfo');

function Title(titles = []) {
    results.innerHTML = titles.map(t => `<li>${t}</li>`).join('') || '<li>No matches</li>';
    matchInfo.textContent = `${titles.length} match${titles.length === 1 ? '' : 'es'}`;
}

apply.onclick = async (e) => {
    e.preventDefault();
    const author = (fAuthor.value || '').trim();
    const body   = (fBody.value   || '').trim();
    const date   = fDate.value || '';
    if (!author && !body && !date) {
        results.innerHTML = '';
        matchInfo.textContent = '';
        return;
    }
    let url;
    if (author && !body && !date) {
        url = `/articles/search/author?authorName=${encodeURIComponent(author)}`;
    }
    else {
        const qs = new URLSearchParams();
        if (author) qs.set('authorName', author);
        if (body)   qs.set('body', body);
        if (date)   qs.set('date', date);
        url = `/articles/search?${qs.toString()}`;
    }
    matchInfo.textContent = 'Searching…';
    try {
        const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
        const data = res.ok ? await res.json() : [];
        const titles = Array.isArray(data) ? data.filter(Boolean) : [];
        Title(titles);
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
