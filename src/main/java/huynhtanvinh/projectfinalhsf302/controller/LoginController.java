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

import java.util.Optional;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Hiển thị trang đăng nhập
     */
    @GetMapping("/")
    public String showLoginPage(HttpSession session) {
        // Nếu đã đăng nhập rồi thì redirect đến trang quản lý
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            return "redirect:/students";
        }
        return "login";
    }

    /**
     * Xử lý đăng nhập
     */
    @PostMapping("/login")
    public String doLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        logger.info("Login attempt for username: {}", username);

        // Authenticate user
        Optional<UserAccount> userOpt = userService.authenticate(username, password);

        if (userOpt.isEmpty()) {
            logger.warn("Login failed: Invalid username or password for user: {}", username);
            model.addAttribute("error", "username or password is invalid!");
            model.addAttribute("username", username); // Giữ lại username đã nhập
            return "login";
        }

        UserAccount user = userOpt.get();

        // Check permission - chỉ Manager (role=1) và Staff (role=2) được truy cập
        if (!userService.hasSystemAccess(user)) {
            logger.warn("Login denied: User {} (role: {}) has no permission to access",
                    username, user.getRole());
            model.addAttribute("error", "You have no permission to access this function!");
            return "login";
        }

        // Lưu thông tin vào session
        session.setAttribute("user", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        logger.info("Login successful: User {} (role: {}) logged in", username, user.getRole());

        // Redirect dựa vào role
        if (userService.isManager(user)) {
            return "redirect:/studentManagement"; // Manager xem top 5 trong cùng trang
        } else {
            return "redirect:/studentManagement?action=add"; // Staff làm CRUD với form hiển thị
        }
    }

    /**
     * Xử lý đăng xuất
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user != null) {
            logger.info("User {} logged out", user.getUsername());
        }

        // Xóa toàn bộ session
        session.invalidate();

        model.addAttribute("message", "You have been logged out successfully!");
        return "redirect:/";
    }
}