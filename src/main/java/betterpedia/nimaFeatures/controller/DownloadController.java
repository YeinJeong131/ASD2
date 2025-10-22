package betterpedia.nimaFeatures.controller;

import betterpedia.nimaFeatures.services.DownloadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/download")
public class DownloadController {
    private final DownloadService downloadService;
    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping(value="/txt/{id}", produces= MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        byte[] b = downloadService.make_txt_file(id);
        if (b == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=article-" + id + ".txt").body(b);
    }

}
