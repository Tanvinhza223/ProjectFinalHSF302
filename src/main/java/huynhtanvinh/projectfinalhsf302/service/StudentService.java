package huynhtanvinh.projectfinalhsf302.service;

import huynhtanvinh.projectfinalhsf302.model.Student;
import huynhtanvinh.projectfinalhsf302.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private  StudentRepository studentRepository;
    // DepartmentService có thể được dùng ở controller để load dropdown

    public Page<Student> getStudents(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100),
                Sort.by("id").ascending());
        return studentRepository.findAll(pageable);
    }

    public Optional<Student> getById(int id) {
        return studentRepository.findById(id);
    }
    @Transactional
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    public List<Student> getTop5ByGpaDesc() {
        return studentRepository.findTop5ByOrderByGpaDesc();
    }
    @Transactional
    public boolean deleteByIdForUser(int id, String username) {
        if (!studentRepository.existsByIdAndCreatedBy(id, username)) {
            return false;
        }
        studentRepository.deleteById(id);
        return true;
    }

    public Page<Student> getStudentsByCreatedBy(String createdBy, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by("id").ascending());
        return studentRepository.findByCreatedBy(createdBy, pageable);
    }

    @Transactional
    public Optional<Student> updateStudentIfOwner(int id, String username, java.util.function.Consumer<Student> updater) {
        Optional<Student> optional = studentRepository.findByIdAndCreatedBy(id, username);
        optional.ifPresent(stu -> {
            updater.accept(stu);
            studentRepository.save(stu);
        });
        return optional;
    }

}
