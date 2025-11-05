package huynhtanvinh.projectfinalhsf302.controller;

import huynhtanvinh.projectfinalhsf302.model.Student;
import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import huynhtanvinh.projectfinalhsf302.service.DepartmentService;
import huynhtanvinh.projectfinalhsf302.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

@Controller
public class StudentManagerController {
    private static final Logger logger = LoggerFactory.getLogger(StudentManagerController.class);
    @Autowired
    private StudentService studentService;
    @Autowired
    private DepartmentService departmentService;
    @GetMapping("/studentManagement")
    public String studentManagementPage(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Integer editId,
            @RequestParam(required = false) String action) {
        //lấy ra thông tin user từ session
        UserAccount user = (UserAccount) session.getAttribute("user");
        //kiểm tra nếu user chưa đăng nhập thì chuyển hướng về trang chủ
        if (user == null) {
            logger.warn("Unauthorized access attempt to studentManagement page");
            return "redirect:/";
        }
        //ghi log thông tin user và các tham số truy vấn
        logger.info("User {} (role: {}) accessing studentManagement page - page: {}, size: {}, editId: {}, action: {}", 
                    user.getUsername(), user.getRole(), page, size, editId, action);
        Student student = new Student();
        //nếu editId khác null và action khác "add" thì tải thông tin sinh viên để chỉnh sửa
        if (editId != null && !"add".equals(action)) {
            logger.debug("Loading student for edit - studentId: {}", editId);
            student = studentService.getById(editId).orElse(new Student());
        } else if ("add".equals(action)) {
            logger.debug("Resetting form for adding new student");
            student = new Student();
        }
        model.addAttribute("student", student);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userRole", user.getRole());
        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 100);
        if (user.getRole() == 2) {
            logger.debug("Staff user {} viewing students with pagination - page: {}, size: {}", user.getUsername(), page, size);
            model.addAttribute("students", studentService.getStudents(page, size));
            return "studentManagement";
        } else if (user.getRole() == 1) {
            logger.debug("Manager user {} viewing top 5 students by GPA", user.getUsername());
            var top5 = studentService.getTop5ByGpaDesc();
            model.addAttribute("students", new PageImpl<>(top5));
            return "studentManagement";
        } else  {
            logger.warn("User {} (role: {}) attempted to access studentManagement without permission", user.getUsername(), user.getRole());
            model.addAttribute("message", "you have no permission to access");
            return "studentManagement";
        }
    }

    @PostMapping("/students/save")
    public String saveStudent(@Valid @ModelAttribute("student") Student student,
                              BindingResult bindingResult,
                              @RequestParam("departmentId") int departmentId,
                              HttpSession session,
                              Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            logger.warn("Unauthorized save attempt - no user in session");
            return "redirect:/";
        }
        boolean isNewStudent = student.getId() == 0;
        logger.info("User {} attempting to {} student - studentId: {}, name: {}, gpa: {}, departmentId: {}", 
                    user.getUsername(), isNewStudent ? "create" : "update", 
                    student.getStudentId(), student.getName(), student.getGpa(), departmentId);
        
        // Set department trước khi validate để tránh lỗi NotNull
        var deptOpt = departmentService.getById(departmentId);
        if (deptOpt==null){
            logger.warn("Department not found - departmentId: {}", departmentId);
            bindingResult.rejectValue("department", "error.department", "Department id not found");
        }else {
            student.setDepartment(deptOpt);
            logger.debug("Department found - departmentId: {}, name: {}", departmentId, deptOpt.getDepartmentName());
            // Xóa lỗi validation về department nếu đã set thành công
//            if (bindingResult.hasFieldErrors("department")) {
//                bindingResult.getFieldErrors("department").clear();
//            }
        }
        if (student.getId()==0) {
            student.setCreatedBy(user.getUsername());
            logger.debug("New student will be created by: {}", user.getUsername());
        }
        if(bindingResult.hasErrors()){
            logger.warn("Validation errors when {} student by user {} - errors: {}", 
                       isNewStudent ? "creating" : "updating", user.getUsername(), bindingResult.getAllErrors());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("username", user.getUsername());
            page= Math.max(0, page);
            size= Math.min(Math.max(1, size), 100);
            if (user.getRole()==2){
                model.addAttribute("students", studentService.getStudents(page, size));
            }else if (user.getRole()==1){
                var top5= studentService.getTop5ByGpaDesc();
                model.addAttribute("students", new PageImpl<>(top5));
        }
            return "studentManagement";
        }
        if (student.getId()!=0) {
            logger.info("Updating student - id: {}, by user: {}", student.getId(), user.getUsername());
            boolean isOwner = studentService.updateStudentIfOwner(student.getId(), user.getUsername(), s->{
                    s.setName(student.getName());
                    s.setDepartment(student.getDepartment());
                    s.setGpa(student.getGpa());
                    s.setStudentId(student.getStudentId());
            }).isPresent();
            if (!isOwner) {
                logger.warn("Update denied - User {} attempted to update student {} but is not the owner", 
                           user.getUsername(), student.getId());
                model.addAttribute("message", "Bạn chỉ được phép sửa sinh viên do bạn tạo");
                return "redirect:/studentManagement?action=add";
            } else {
                logger.info("Student {} updated successfully by user {}", student.getId(), user.getUsername());
                return "redirect:/studentManagement?action=add";
            }
        }else  {
            logger.info("Creating new student - studentId: {}, name: {}, by user: {}", 
                        student.getStudentId(), student.getName(), user.getUsername());
            studentService.saveStudent(student);
            logger.info("New student created successfully - id: {}, studentId: {}", student.getId(), student.getStudentId());
            return "redirect:/studentManagement?action=add";
        }
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable("id") int id, HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            logger.warn("Unauthorized delete attempt - no user in session");
            return "redirect:/";
        }
        logger.info("User {} attempting to delete student - id: {}", user.getUsername(), id);
        boolean ok = studentService.deleteByIdForUser(id, user.getUsername());
        if (!ok) {
            logger.warn("Delete denied - User {} attempted to delete student {} but is not the owner", 
                       user.getUsername(), id);
            model.addAttribute("message", "Bạn chỉ được phép xoá sinh viên do bạn tạo");
        } else {
            logger.info("Student {} deleted successfully by user {}", id, user.getUsername());
        }
        return "redirect:/studentManagement";
    }
}
