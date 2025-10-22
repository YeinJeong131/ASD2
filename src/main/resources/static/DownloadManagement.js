// DownloadManagement.js

const btnTxt  = document.getElementById('button_download_as_txt');
const btnPdf  = document.getElementById('button_download_as_pdf');
const select  = document.getElementById('pick');
const details = document.getElementById('det'); // optional fallback if you store an id here

function getSelectedArticleId() {
    // primary: from the <select>
    const idFromSelect = (select?.value || '').trim();
    if (idFromSelect) return idFromSelect;

    // fallback: if you render details with a data-attribute like: <div id="det" data-article-id="123">
    const idFromDetails = details?.dataset?.articleId;
    if (idFromDetails) return idFromDetails;

    return null;
}

function navigateTo(kind) {
    const id = getSelectedArticleId();
    if (!id) {
        alert('Pick an article first.');
        return;
    }
    // use your actual routes
    const url = kind === 'txt'
        ? `/download/txt/${id}`
        : `/api/download/${id}/pdf`;

    window.location.href = url;
}

// wire up buttons
btnTxt?.addEventListener('click', () => navigateTo('txt'));
btnPdf?.addEventListener('click', () => navigateTo('pdf'));

// (nice UX) disable buttons when no article selected
function updateButtonsState() {
    const hasId = !!getSelectedArticleId();
    btnTxt && (btnTxt.disabled = !hasId);
    btnPdf && (btnPdf.disabled = !hasId);
}
updateButtonsState();
select?.addEventListener('change', updateButtonsState);
