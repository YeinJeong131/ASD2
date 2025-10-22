const download_simple_txt = document.getElementById("button_download_as_txt");
const download_pdf = document.getElementById("button_download_as_pdf");
console.log(download_pdf);
console.log("TEST");

download_simple_txt.onclick = () => {
    window.location.href = "/download/txt/1";
    console.log("\nThe button was clicked\n");
}

download_pdf.onclick = () => {
    console.log("fdgdgfgg");

    window.location.href = "/api/download/1/pdf";
}



const result = document.getElementById("results");
