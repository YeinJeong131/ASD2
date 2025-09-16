package betterpedia.user;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class F101UserManagementController {

//    // Default home → goes to login page for R0
//    @GetMapping("/")
//    public String home() {
//        return "login";
//    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "login";
    }

    @GetMapping("/read")
    public String readOnly(Model model) {
        model.addAttribute("pageTitle", "Betterpedia • Read");
        return "read";
    }

    @GetMapping("/account")
    public String accountSettings(Model model) {
        model.addAttribute("pageTitle", "Account Settings");
        return "account-settings";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("pageTitle", "Admin • User Management");
        return "admin";
    }

}
