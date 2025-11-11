package huynhtanvinh.projectfinalhsf302.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * Băm mật khẩu sử dụng BCrypt
     * @param plainPassword mật khẩu dạng text thường
     * @return mật khẩu đã được băm
     */
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }
    
    /**
     * Kiểm tra mật khẩu có khớp với hash không
     * @param plainPassword mật khẩu dạng text thường
     * @param hashedPassword mật khẩu đã được băm trong database
     * @return true nếu khớp, false nếu không khớp
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
