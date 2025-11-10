package huynhtanvinh.projectfinalhsf302.repository;

import huynhtanvinh.projectfinalhsf302.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface StudentRepository extends JpaRepository<Student,Integer> {
    boolean existsByStudentId(String studentId);
    Optional<Student> findByStudentId(String studentId);
    List<Student> findTop5ByOrderByGpaDesc();
    Page<Student> findByCreatedBy(String createdBy, Pageable pageable);
    Optional<Student> findByIdAndCreatedBy(int id, String createdBy);
    boolean existsByIdAndCreatedBy(int id, String createdBy);
    long countByCreatedBy(String createdBy);              // Đếm số students
    List<Student> searchByName(String name);              // Search
    List<Student> findByDepartmentId(Integer deptId);     // Filter by dept
}
