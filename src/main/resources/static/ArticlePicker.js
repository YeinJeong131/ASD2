const url = 'http://localhost:8080/articles';
const pick = document.getElementById('pick');
const det = document.getElementById('det');
const voiceBtn = document.getElementById('voice');
voiceBtn.disabled = true;

let articles = [];

async function loadArticleTitles() {
    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('fail');
        articles = await response.json();

        for (const a of articles) {
            const option = document.createElement('option');
            option.value = a.id;
            option.textContent = a.title;
            pick.appendChild(option);
        }
    } catch (e) {
        console.error(e);
        det.textContent = 'could not load articles';
    }
}

function showArticleByID(id){
    const a = articles.find(x => String(x.id) === String(id));
    if (!a) { det.innerHTML = ''; voiceBtn.disabled = true; return; }   // NEW

    det.innerHTML = `
    <h2>${a.title}</h2>
    <p><em>by ${a.author}</em></p>
    <p>${(a.body).replace(/\n/g,'<br>')}</p>
    <p>published: ${a.publishDate}</p>
  `;

    const body = (a.body).replace(/\s+/g,' ').trim();
    voiceBtn.dataset.text = body;
    voiceBtn.disabled = !body;
    document.getElementById('readingTime').textContent = readingTime(body);  // <-- add this
}

pick.addEventListener('change', () => showArticleByID(pick.value));

loadArticleTitles();
