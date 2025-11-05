package huynhtanvinh.projectfinalhsf302.controller;

import huynhtanvinh.projectfinalhsf302.model.Departments;
import huynhtanvinh.projectfinalhsf302.model.UserAccount;
import huynhtanvinh.projectfinalhsf302.service.DepartmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/department")
    public String departmentPage(HttpSession session, Model model,
                                 @RequestParam(required = false) Integer editId) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) return "redirect:/";
        if (user.getRole() != 1) {
            model.addAttribute("message", "You have no permission to access this function!");
            return "department";
        }
        Departments dept = new Departments();
        if (editId != null) {
            dept = departmentService.getById(editId);
            if (dept == null) dept = new Departments();
        }
        model.addAttribute("department", dept);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("username", user.getUsername());
        return "department";
    }

    @PostMapping("/department/save")
    public String saveDepartment(@Valid @ModelAttribute("department") Departments department,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) return "redirect:/";
        if (user.getRole() != 1) {
            model.addAttribute("message", "You have no permission to access this function!");
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "department";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("username", user.getUsername());
            return "department";
        }
       Departments departments = departmentService.getById(department.getId());
        departments.setDepartmentName(department.getDepartmentName());
        departmentService.saveDepartment(departments);
        return "redirect:/department";
    }

    @PostMapping("/department/delete/{id}")
    public String deleteDepartment(@PathVariable("id") int id,
                                    HttpSession session,
                                    Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) return "redirect:/";
        if (user.getRole() != 1) {
            model.addAttribute("message", "You have no permission to access this function!");
            return "department";
        }
       Departments departments= departmentService.getById(id);
        if(!departments.getStudents().isEmpty()){
            model.addAttribute("message", "Cannot delete department with assigned students!");
            return "department";
        }
        departmentService.deleteDepartment(id);
        return "redirect:/department";
    }
}
