package huynhtanvinh.projectfinalhsf302.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import huynhtanvinh.projectfinalhsf302.service.UserService;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String showLoginPage() {
        return "Login";
    }
    @PostMapping("/login")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          HttpSession session,
                          Model model) {
        logger.info("Login attempt for username: {}", username);
        UserAccount user = userService.checkLogin(username, password);
        if (user == null) {
            logger.warn("Login failed: Invalid username or password for user: {}", username);
            model.addAttribute("error", "username or password is invalid!");
            return "Login";
        }
        if (user.getRole() != 1 && user.getRole() != 2) {
            logger.warn("Login denied: User {} (role: {}) has no permission to access", username, user.getRole());
            model.addAttribute("error", "You have no permission to access this function!");
            return "Login";
        }
        session.setAttribute("user", user);
        logger.info("Login successful: User {} (role: {}) logged in", username, user.getRole());
        return "redirect:/studentManagement";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            logger.info("User {} logged out", user.getUsername());
        }
        session.invalidate();
        return "redirect:/";
    }
}
