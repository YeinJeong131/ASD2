package betterpedia.user.controller;

import betterpedia.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class F101UserManagementController {
    private final UserService userService;

    public F101UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        return "redirect:/wiki";
    }

    // 추가: /account GET 매핑 (Session 사용)
    @GetMapping("/account")
    public String accountPage(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Account Settings");
        model.addAttribute("user", userService.getById(userId));
        return "account-settings";
    }

    // ----- Account (POST only) -----
    @PostMapping("/account/profile")
    public String saveProfile(@RequestParam Long uid,
                              @RequestParam String email,
                              RedirectAttributes ra) {
        userService.updateProfile(uid, email);
        ra.addFlashAttribute("msg", "Profile updated");
        return "redirect:/account";
    }

    @PostMapping("/account/password")
    public String changePassword(@RequestParam Long uid,
                                 @RequestParam String newPassword,
                                 RedirectAttributes ra) {
        userService.changePassword(uid, newPassword);
        ra.addFlashAttribute("msg", "Password changed");
        return "redirect:/account";
    }

    @PostMapping("/account/delete")
    public String deleteMyAccount(@RequestParam Long uid, RedirectAttributes ra) {
        userService.deleteUser(uid);
        ra.addFlashAttribute("msg", "Your account was deleted");
        return "redirect:/login";
    }

    // ----- Admin -----
    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("pageTitle", "Admin");
        model.addAttribute("users", userService.listAll());
        return "admin";
    }

    @PostMapping("/admin/users")
    public String createUser(@RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(defaultValue = "false") boolean enabled,
                             RedirectAttributes ra) {
        userService.createUser(email, password, List.of("ROLE_USER"), enabled);
        ra.addFlashAttribute("msg", "User created");
        return "redirect:/admin";
    }

    @PostMapping("/admin/users/{id}/toggle")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes ra) {
        boolean nowEnabled = userService.toggleEnabled(id);
        ra.addFlashAttribute("msg", "User " + id + " is now " + (nowEnabled ? "ENABLED" : "DISABLED"));
        return "redirect:/admin";
    }

    @GetMapping("/admin/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Edit User");
        model.addAttribute("user", userService.getById(id));
        return "admin-edit-user";
    }

    @PostMapping("/admin/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             RedirectAttributes ra) {
        userService.updateProfile(id, email);
        if (password != null && !password.isBlank()) {
            userService.changePassword(id, password);
        }
        ra.addFlashAttribute("msg", "User updated");
        return "redirect:/admin";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("msg", "User deleted");
        return "redirect:/admin";
    }
}