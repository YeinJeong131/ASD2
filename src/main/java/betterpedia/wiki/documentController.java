package betterpedia.wiki;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Wiki Document Controller (for testing)
 * In reality, another teammate will implement this part
 */
@Controller
@RequestMapping("/wiki")
public class documentController {

    /**
     * Document view page
     * Example: /wiki/spring-framework
     */
    @GetMapping("/{documentId}")
    public String viewDocument(@PathVariable String documentId, Model model) {

        // In reality, fetch document from the database
        // Document document = documentService.findById(documentId);

        // Dummy data for testing
        model.addAttribute("document", createDummyDocument(documentId));

        // JSP page with your highlight system applied
        return "wiki/document";
    }

    /**
     * Home page (document list)
     */
    @GetMapping("")
    public String home(Model model) {
        // Document list
        model.addAttribute("documents", createDummyDocumentList());
        return "wiki/home";
    }

    /**
     * Create a dummy document for testing
     */
    private DocumentDto createDummyDocument(String documentId) {
        DocumentDto doc = new DocumentDto();

        switch (documentId) {
            case "spring-framework":
                doc.setTitle("Spring Framework");
                doc.setCategory("Framework");
                doc.setContent(null); // Use default content in JSP
                break;

            case "java-programming":
                doc.setTitle("Java Programming");
                doc.setCategory("Programming Language");
                doc.setContent("<h2>What is Java?</h2><p>Java is an object-oriented programming language. " +
                        "It is platform-independent and provides strong memory management. " +
                        "It is widely used in web development, mobile apps, and enterprise applications.</p>");
                break;

            case "spring-boot":
                doc.setTitle("Spring Boot");
                doc.setCategory("Framework");
                doc.setContent("<h2>Introduction to Spring Boot</h2><p>Spring Boot is a framework " +
                        "that makes it easy to build applications based on Spring. " +
                        "It provides auto-configuration and an embedded server, " +
                        "allowing rapid application development without complex setup.</p>");
                break;

            case "database-design":
                doc.setTitle("Database Design");
                doc.setCategory("Database");
                doc.setContent("<h2>Principles of Database Design</h2><p>Good database design includes " +
                        "normalization, integrity constraints, and efficient indexing. " +
                        "Following these principles results in high performance and easy-to-maintain databases.</p>");
                break;

            default:
                doc.setTitle("Test Document");
                doc.setCategory("General");
                doc.setContent("<h2>Test Document</h2><p>This is a test document. " +
                        "Try selecting text to create a highlight! " +
                        "You can use different colors to mark important content and add personal notes.</p>");
        }

        return doc;
    }

    /**
     * Create a dummy document list for testing
     */
    private java.util.List<DocumentDto> createDummyDocumentList() {
        java.util.List<DocumentDto> docs = new java.util.ArrayList<>();

        DocumentDto doc1 = new DocumentDto();
        doc1.setTitle("Spring Framework");
        doc1.setCategory("Framework");
        docs.add(doc1);

        DocumentDto doc2 = new DocumentDto();
        doc2.setTitle("Java Programming");
        doc2.setCategory("Programming Language");
        docs.add(doc2);

        DocumentDto doc3 = new DocumentDto();
        doc3.setTitle("Spring Boot");
        doc3.setCategory("Framework");
        docs.add(doc3);

        DocumentDto doc4 = new DocumentDto();
        doc4.setTitle("Database Design");
        doc4.setCategory("Database");
        docs.add(doc4);

        return docs;
    }

    /**
     * Simple DTO class
     */
    public static class DocumentDto {
        private String title;
        private String category;
        private String content;

        // Constructor
        public DocumentDto() {}

        public DocumentDto(String title, String category, String content) {
            this.title = title;
            this.category = category;
            this.content = content;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "DocumentDto{" +
                    "title='" + title + '\'' +
                    ", category='" + category + '\'' +
                    ", content='" + (content != null ? content.substring(0, Math.min(50, content.length())) + "..." : "null") + '\'' +
                    '}';
        }
    }
}
