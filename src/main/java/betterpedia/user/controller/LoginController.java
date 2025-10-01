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

    // GET /login - 로그인 페이지 표시
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        // 이미 로그인되어 있으면 wiki로 리다이렉트
        if (session.getAttribute("userId") != null) {
            return "redirect:/wiki";
        }

        model.addAttribute("pageTitle", "Login");
        return "login";
    }

    // POST /login - 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username,  // email 또는 username
                        @RequestParam String password,
                        @RequestParam(required = false) boolean remember,
                        HttpSession session,
                        Model model) {

        // 이메일로 사용자 찾기
        Optional<User> userOpt = userRepository.findByEmail(username);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        User user = userOpt.get();

        // 비밀번호 확인 (나중에 BCrypt로 변경)
        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        // 계정 활성화 확인
        if (!user.isEnabled()) {
            model.addAttribute("error", "Your account has been disabled");
            return "login";
        }

        // 로그인 성공 - 세션에 저장
        session.setAttribute("userId", user.getId());
        session.setAttribute("email", user.getEmail());

        // Remember me 처리 (선택사항)
        if (remember) {
            session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30일
        } else {
            session.setMaxInactiveInterval(2 * 60 * 60); // 2시간
        }

        // 로그인 후 wiki 홈으로 리다이렉트
        return "redirect:/wiki";
    }

    // GET /logout - 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate(); // 세션 삭제
        model.addAttribute("msg", "You have been signed out");
        return "redirect:/login";
    }

    // POST /logout (form에서 POST 사용하는 경우)
    @PostMapping("/logout")
    public String logoutPost(HttpSession session, Model model) {
        session.invalidate();
        model.addAttribute("msg", "You have been signed out");
        return "redirect:/login";
    }
}
