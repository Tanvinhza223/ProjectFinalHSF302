package huynhtanvinh.projectfinalhsf302.config;

import huynhtanvinh.projectfinalhsf302.model.Departments;
import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import huynhtanvinh.projectfinalhsf302.repository.DepartmentRepository;
import huynhtanvinh.projectfinalhsf302.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        if (userAccountRepository.count() == 0) {
            UserAccount m1 = UserAccount.builder().username("manager1").password("123456").role(1).build();
            UserAccount m2 = UserAccount.builder().username("manager2").password("123456").role(1).build();
            UserAccount s1 = UserAccount.builder().username("staff1").password("123456").role(2).build();
            UserAccount g1 = UserAccount.builder().username("guest1").password("123456").role(3).build();
            userAccountRepository.saveAll(Arrays.asList(m1, m2, s1, g1));
        }

        if (departmentRepository.count() == 0) {
            Departments d1 = Departments.builder().departmentName("Computer Science").build();
            Departments d2 = Departments.builder().departmentName("Business Administration").build();
            Departments d3 = Departments.builder().departmentName("Design").build();
            departmentRepository.saveAll(Arrays.asList(d1, d2, d3));
        }
    }
}


