package huynhtanvinh.projectfinalhsf302.repository;

import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {

    /**
     * Tìm user theo username
     * @param username tên đăng nhập
     * @return Optional chứa UserAccount nếu tìm thấy
     */
    Optional<UserAccount> findByUsername(String username);

    /**
     * Kiểm tra username đã tồn tại chưa
     * @param username tên đăng nhập
     * @return true nếu tồn tại, false nếu chưa
     */
    boolean existsByUsername(String username);
}
