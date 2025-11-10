package huynhtanvinh.projectfinalhsf302.repository;

import huynhtanvinh.projectfinalhsf302.model.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  DepartmentRepository extends JpaRepository<Departments, Integer> {
    Optional<Departments> findByDepartmentName(String departmentName);
    boolean existsByDepartmentName(String departmentName);
}
