package betterpedia.user.controller;

import betterpedia.user.entity.User;
import betterpedia.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // If already logged in, skip login page
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            return "redirect:/wiki";
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam("username") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model
    ) {
        Optional<User> found = userRepository.findByEmail(email);

        if (found.isPresent()) {
            User u = found.get();
            // Plain-text password check for now (hash later if needed)
            if (Boolean.TRUE.equals(u.isEnabled()) && password.equals(u.getPassword())) {
                session.setAttribute("userId", u.getId());
                session.setAttribute("userEmail", u.getEmail());

                boolean isAdmin = u.getRoles() != null
                        && u.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));

                return isAdmin ? "redirect:/admin" : "redirect:/account";
            }
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate();
        model.addAttribute("msg", "You have been signed out");
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession session, Model model) {
        session.invalidate();
        model.addAttribute("msg", "You have been signed out");
        return "redirect:/login";
    }
}
