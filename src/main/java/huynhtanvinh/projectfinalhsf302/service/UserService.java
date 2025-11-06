package huynhtanvinh.projectfinalhsf302.service;

import huynhtanvinh.projectfinalhsf302.config.PasswordUtil;
import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import huynhtanvinh.projectfinalhsf302.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserAccountRepository userAccountRepository;
    
    public UserService(UserAccountRepository userAccountRepository) {}
    
    /**
     * Kiểm tra đăng nhập bằng cách so sánh username và password đã băm
     * @param username tên đăng nhập
     * @param plainPassword mật khẩu dạng text thường
     * @return UserAccount nếu đăng nhập thành công, null nếu thất bại
     */
    public UserAccount checkLogin(String username, String plainPassword) {
        // Tìm user theo username
        Optional<UserAccount> userOptional = userAccountRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            UserAccount user = userOptional.get();
            
            // So sánh mật khẩu đã băm
            if (PasswordUtil.verifyPassword(plainPassword, user.getPassword())) {
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Tạo user mới với mật khẩu đã băm
     * @param username tên đăng nhập
     * @param plainPassword mật khẩu dạng text thường
     * @param role vai trò của user
     * @return UserAccount đã được tạo
     */
    public UserAccount createUser(String username, String plainPassword, int role) {
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        UserAccount newUser = UserAccount.builder()
                .username(username)
                .password(hashedPassword)
                .role(role)
                .build();
        
        return userAccountRepository.save(newUser);
    }
}