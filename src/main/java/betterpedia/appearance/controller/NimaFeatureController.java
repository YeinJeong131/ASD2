package betterpedia.appearance.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NimaFeatureController {
    @GetMapping("/nima-features")
    public String nimaFeatures() {
        return "forward:/index.html";
    }
}