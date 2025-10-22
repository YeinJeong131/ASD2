const btnTxt  = document.getElementById('button_download_as_txt');
const select  = document.getElementById('pick');
const details = document.getElementById('det'); // optional fallback if you store an id here

function getSelectedArticleId() {
    const idFromSelect = (select?.value || '').trim();
    if (idFromSelect) return idFromSelect;

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
    const url = kind === 'txt'
        ? `/download/txt/${id}`
        : `/api/download/${id}/pdf`;

    window.location.href = url;
}

btnTxt?.addEventListener('click', () => navigateTo('txt'));

function updateButtonsState() {
    const hasId = !!getSelectedArticleId();
    btnTxt && (btnTxt.disabled = !hasId);
}
updateButtonsState();
select?.addEventListener('change', updateButtonsState);
