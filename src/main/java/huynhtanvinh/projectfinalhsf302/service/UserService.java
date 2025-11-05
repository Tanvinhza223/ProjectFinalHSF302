package huynhtanvinh.projectfinalhsf302.service;

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
    public UserAccount checkLogin(String username, String password) {
        // Logic to check username and password against the database
        // Return true if valid, false otherwise
        Optional<UserAccount> userAccount = userAccountRepository.findByUsernameAndPassword(username, password);
        return userAccount.orElse(null);
    }
}
