package huynhtanvinh.projectfinalhsf302.repository;

import huynhtanvinh.projectfinalhsf302.model.Departments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  DepartmentRepository extends JpaRepository<Departments, Integer> {
}
