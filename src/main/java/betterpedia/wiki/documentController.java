package betterpedia.wiki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/wiki")
public class documentController {

    @Autowired
    private documentService documentService;

    /**
     * Wiki 홈페이지 - 문서 목록
     */
    @GetMapping("")
    public String home(Model model) {
        // 샘플 데이터 생성 (개발용)
        documentService.createSampleData();

        List<documentEntity> documents = documentService.getAllDocuments();
        model.addAttribute("documents", documents);
        return "wiki/home";
    }

    /**
     * 특정 문서 보기
     * 예: /wiki/spring-framework
     */
    @GetMapping("/{slug}")
    public String viewDocument(@PathVariable String slug, Model model) {
        documentEntity document = documentService.getBySlugOrDefault(slug);
        model.addAttribute("document", document);
        return "wiki/document";
    }
}