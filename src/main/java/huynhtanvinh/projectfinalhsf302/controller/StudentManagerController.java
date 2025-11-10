package huynhtanvinh.projectfinalhsf302.controller;

import huynhtanvinh.projectfinalhsf302.model.Student;
import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import huynhtanvinh.projectfinalhsf302.service.DepartmentService;
import huynhtanvinh.projectfinalhsf302.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentManagerController {

    private static final Logger logger = LoggerFactory.getLogger(StudentManagerController.class);

    private final StudentService studentService;
    private final DepartmentService departmentService;

    @Autowired
    public StudentManagerController(StudentService studentService, DepartmentService departmentService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
    }

    /**
     * Hiển thị trang quản lý sinh viên
     * - Manager: Xem top 5 GPA cao nhất (không phân trang)
     * - Staff: Xem tất cả students với phân trang
     */
    @GetMapping("/studentManagement")
    public String studentManagementPage(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer editId,
            @RequestParam(required = false) String action) {

        // Kiểm tra đăng nhập
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            logger.warn("Unauthorized access attempt to studentManagement page");
            return "redirect:/";
        }

        logger.info("User {} (role: {}) accessing studentManagement - page: {}, size: {}, editId: {}, action: {}",
                user.getUsername(), user.getRole(), page, size, editId, action);

        // Xử lý form student (add hoặc edit)
        Student student = new Student();
        if (editId != null && !"add".equals(action)) {
            logger.debug("Loading student for edit - studentId: {}", editId);
            student = studentService.getById(editId).orElse(new Student());
        } else if ("add".equals(action)) {
            logger.debug("Resetting form for adding new student");
            student = new Student();
        }

        // Add common attributes
        model.addAttribute("student", student);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userRole", user.getRole());

        // Validate page và size
        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 100);

        // Load students theo role
        if (user.getRole() == 2) { // Staff - CRUD với phân trang
            logger.debug("Staff user {} viewing all students with pagination - page: {}, size: {}",
                    user.getUsername(), page, size);
            model.addAttribute("students", studentService.getStudents(page, size));
            return "studentManagement";

        } else if (user.getRole() == 1) { // Manager - Xem top 5
            logger.debug("Manager user {} viewing top 5 students by GPA", user.getUsername());
            var top5 = studentService.getTop5ByGpaDesc();
            model.addAttribute("students", new PageImpl<>(top5));
            model.addAttribute("isManagerView", true); // Flag để UI biết là Manager view
            return "studentManagement";

        } else { // Guest hoặc role khác
            logger.warn("User {} (role: {}) attempted to access studentManagement without permission",
                    user.getUsername(), user.getRole());
            model.addAttribute("error", "You have no permission to access this function!");
            return "redirect:/";
        }
    }

    /**
     * Lưu student (Add hoặc Update)
     */
    @PostMapping("/students/save")
    public String saveStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult bindingResult,
            @RequestParam("departmentId") int departmentId,
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        // Kiểm tra đăng nhập
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            logger.warn("Unauthorized save attempt - no user in session");
            return "redirect:/";
        }

        // Chỉ Staff mới được phép thêm/sửa
        if (user.getRole() != 2) {
            logger.warn("User {} (role: {}) attempted to save student without permission",
                    user.getUsername(), user.getRole());
            return "redirect:/studentManagement";
        }

        boolean isNewStudent = student.getId() == 0;
        logger.info("User {} attempting to {} student - studentId: {}, name: {}, gpa: {}, departmentId: {}",
                user.getUsername(), isNewStudent ? "create" : "update",
                student.getStudentId(), student.getName(), student.getGpa(), departmentId);

        // Set department trước khi validate
        var deptOpt = departmentService.getById(departmentId);
        if (deptOpt == null) {
            logger.warn("Department not found - departmentId: {}", departmentId);
            bindingResult.rejectValue("department", "error.department", "Department not found");
        } else {
            student.setDepartment(deptOpt);
            logger.debug("Department set - departmentId: {}, name: {}", departmentId, deptOpt.getDepartmentName());
        }

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors when {} student by user {} - errors: {}",
                    isNewStudent ? "creating" : "updating", user.getUsername(), bindingResult.getAllErrors());

            // Reload data cho form
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userRole", user.getRole());

            page = Math.max(0, page);
            size = Math.min(Math.max(1, size), 100);
            model.addAttribute("students", studentService.getStudents(page, size));

            return "studentManagement";
        }

        // Lưu student
        try {
            if (isNewStudent) {
                // Add new student
                logger.info("Creating new student - studentId: {}, name: {}, by user: {}",
                        student.getStudentId(), student.getName(), user.getUsername());
                studentService.addStudent(student, user.getUsername());
                logger.info("Student created successfully");
            } else {
                // Update existing student
                logger.info("Updating student - id: {}, by user: {}", student.getId(), user.getUsername());
                studentService.updateStudent(student.getId(), student, user.getUsername());
                logger.info("Student {} updated successfully", student.getId());
            }

            return "redirect:/studentManagement?action=add";

        } catch (RuntimeException e) {
            logger.error("Error saving student: {}", e.getMessage());

            // Hiển thị lỗi cho user
            // Nếu lỗi liên quan đến trùng mã SV, đẩy lỗi về đúng field để hiển thị bên dưới
            // input
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("student id already exists")) {
                bindingResult.rejectValue("studentId", "duplicate", e.getMessage());
            }
            model.addAttribute("error", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userRole", user.getRole());

            page = Math.max(0, page);
            size = Math.min(Math.max(1, size), 100);
            model.addAttribute("students", studentService.getStudents(page, size));

            return "studentManagement";
        }
    }

    /**
     * Xóa student
     */
    @PostMapping("/students/delete/{id}")
    public String deleteStudent(
            @PathVariable("id") int id,
            HttpSession session,
            Model model) {

        // Kiểm tra đăng nhập
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            logger.warn("Unauthorized delete attempt - no user in session");
            return "redirect:/";
        }

        // Chỉ Staff mới được phép xóa
        if (user.getRole() != 2) {
            logger.warn("User {} (role: {}) attempted to delete student without permission",
                    user.getUsername(), user.getRole());
            return "redirect:/studentManagement";
        }

        logger.info("User {} attempting to delete student - id: {}", user.getUsername(), id);

        // Xóa student (với ownership check)
        boolean deleted = studentService.deleteByIdForUser(id, user.getUsername());

        if (!deleted) {
            logger.warn(
                    "Delete denied - User {} attempted to delete student {} but is not the owner or student not found",
                    user.getUsername(), id);
            model.addAttribute("error", "You can only delete students created by you!");
        } else {
            logger.info("Student {} deleted successfully by user {}", id, user.getUsername());
            model.addAttribute("message", "Student deleted successfully!");
        }

        return "redirect:/studentManagement";
    }
}