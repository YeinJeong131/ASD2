package wikipedia.appearance.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*") // need to remove for security reason
public class UserSettingController {

    @Value("${spring.application.name}")
    private String appName;

    // simple test method

    @GetMapping("/test")
    public String test() {
        System.out.println("appName: " + appName);
        return "UserSettings API is working! Hello from " + appName;
    }
}
