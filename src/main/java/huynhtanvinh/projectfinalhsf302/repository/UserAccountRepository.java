package huynhtanvinh.projectfinalhsf302.repository;

import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount,Integer> {
    Optional<UserAccount> findByUsernameAndPassword(String username, String password);
}
