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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    // ========== PAGINATION ==========

    /**
     * Lấy tất cả students với phân trang (cho Staff)
     * Sắp xếp theo GPA giảm dần
     */
    public Page<Student> getStudents(int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.min(Math.max(1, size), 100),
                Sort.by("gpa").descending()
        );
        return studentRepository.findAll(pageable);
    }

    /**
     * Lấy danh sách students với phân trang (cho Staff)
     * Chỉ lấy students do user hiện tại tạo (created_by)
     */
    public Page<Student> getStudentsByCreatedBy(String createdBy, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.min(Math.max(1, size), 100),
                Sort.by("gpa").descending()
        );
        return studentRepository.findByCreatedBy(createdBy, pageable);
    }

    /**
     * Lấy top 5 students có GPA cao nhất (cho Manager)
     */
    public List<Student> getTop5ByGpaDesc() {
        return studentRepository.findTop5ByOrderByGpaDesc();
    }

    // ========== BASIC QUERIES ==========

    public Optional<Student> getById(int id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    // ========== ADD STUDENT ==========

    /**
     * Thêm student mới với đầy đủ validation
     * @param student Student object cần thêm
     * @param loggedInUsername Username của user đang đăng nhập
     * @return Student đã được lưu
     * @throws RuntimeException nếu studentId đã tồn tại
     */
    @Transactional
    public Student addStudent(Student student, String loggedInUsername) {
        // Kiểm tra studentId đã tồn tại chưa
        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new RuntimeException("Student ID already exists!");
        }

        // Set thông tin tạo mới
        student.setCreatedAt(LocalDate.now());
        student.setUpdatedAt(LocalDate.now());
        student.setCreatedBy(loggedInUsername);

        return studentRepository.save(student);
    }

    // ========== UPDATE STUDENT ==========

    /**
     * Cập nhật student với kiểm tra ownership
     * @param id ID của student cần update
     * @param updatedStudent Student object chứa thông tin mới
     * @param loggedInUsername Username của user đang đăng nhập
     * @return Student đã được cập nhật
     * @throws RuntimeException nếu không tìm thấy, không có quyền, hoặc studentId trùng
     */
    @Transactional
    public Student updateStudent(int id, Student updatedStudent, String loggedInUsername) {
        // Tìm student cần update
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        // Kiểm tra ownership - chỉ update student do mình tạo
        if (!existingStudent.getCreatedBy().equals(loggedInUsername)) {
            throw new RuntimeException("You can only update students created by you!");
        }

        // Kiểm tra studentId mới có trùng với student khác không
        Optional<Student> studentWithSameId = studentRepository.findByStudentId(updatedStudent.getStudentId());
        if (studentWithSameId.isPresent() && studentWithSameId.get().getId() != id) {
            throw new RuntimeException("Student ID already exists!");
        }

        // Cập nhật các field
        existingStudent.setStudentId(updatedStudent.getStudentId());
        existingStudent.setName(updatedStudent.getName());
        existingStudent.setGpa(updatedStudent.getGpa());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setUpdatedAt(LocalDate.now());

        // Không thay đổi created_at và created_by

        return studentRepository.save(existingStudent);
    }

    // ========== DELETE STUDENT ==========

    /**
     * Xóa student với kiểm tra ownership
     * @param id ID của student cần xóa
     * @param username Username của user đang đăng nhập
     * @return true nếu xóa thành công, false nếu không tìm thấy hoặc không có quyền
     */
    @Transactional
    public boolean deleteStudent(int id, String username) {
        // Kiểm tra student có tồn tại và có được tạo bởi user hiện tại không
        if (!studentRepository.existsByIdAndCreatedBy(id, username)) {
            return false;
        }

        studentRepository.deleteById(id);
        return true;
    }

    /**
     * Method alias cho deleteStudent (backward compatibility)
     */
    @Transactional
    public boolean deleteByIdForUser(int id, String username) {
        return deleteStudent(id, username);
    }

    // ========== HELPER METHODS ==========

    /**
     * Kiểm tra student có được tạo bởi user hiện tại không
     */
    public boolean isCreatedByUser(int id, String username) {
        return studentRepository.existsByIdAndCreatedBy(id, username);
    }

    /**
     * Lấy student theo ID và created_by (để đảm bảo ownership)
     */
    public Optional<Student> getStudentByIdAndCreatedBy(int id, String username) {
        return studentRepository.findByIdAndCreatedBy(id, username);
    }
}