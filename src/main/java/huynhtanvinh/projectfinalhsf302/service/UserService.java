package huynhtanvinh.projectfinalhsf302.service;

import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import huynhtanvinh.projectfinalhsf302.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Kiểm tra đăng nhập với username và password
     * @param username Username
     * @param password Password (plain text)
     * @return Optional<UserAccount> - có user nếu đăng nhập thành công
     */
    public Optional<UserAccount> authenticate(String username, String password) {
        // Validate input
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return Optional.empty();
        }

        // Tìm user theo username và password
        return userAccountRepository.findByUsernameAndPassword(username.trim(), password);
    }

    /**
     * Kiểm tra user có phải Manager không (role = 1)
     */
    public boolean isManager(UserAccount user) {
        return user != null && user.getRole() == 1;
    }

    /**
     * Kiểm tra user có phải Staff không (role = 2)
     */
    public boolean isStaff(UserAccount user) {
        return user != null && user.getRole() == 2;
    }

    /**
     * Kiểm tra user có quyền truy cập hệ thống không (Manager hoặc Staff)
     */
    public boolean hasSystemAccess(UserAccount user) {
        return user != null && (user.getRole() == 1 || user.getRole() == 2);
    }

    /**
     * Lấy user theo username
     */
    public Optional<UserAccount> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userAccountRepository.findByUsername(username.trim());
    }

    /**
     * Lấy user theo ID
     */
    public Optional<UserAccount> findById(int id) {
        return userAccountRepository.findById(id);
    }
}