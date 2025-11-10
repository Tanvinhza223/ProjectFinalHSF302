package huynhtanvinh.projectfinalhsf302.service;

import huynhtanvinh.projectfinalhsf302.model.Departments;
import huynhtanvinh.projectfinalhsf302.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    // ========== BASIC QUERIES ==========

    public List<Departments> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Page<Departments> getAllDepartments(int pageNumber, int pageSize) {
        return departmentRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public Departments getById(int id) {
        return departmentRepository.findById(id).orElse(null);
    }

    // ✅ THÊM: Tìm theo tên
    public Departments findByName(String departmentName) {
        return departmentRepository.findByDepartmentName(departmentName).orElse(null);
    }

    // ========== CRUD WITH VALIDATION ==========

    /**
     * ✅ ADD Department - CHECK TRÙNG TÊN
     */
    public Departments addDepartment(Departments department) {
        // Kiểm tra tên đã tồn tại chưa
        if (departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new RuntimeException("Department name already exists!");
        }
        return departmentRepository.save(department);
    }

    /**
     * ✅ UPDATE Department - CHECK TRÙNG TÊN
     */
    public Departments updateDepartment(int id, Departments updatedDepartment) {
        Departments existingDept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found!"));

        // Kiểm tra tên mới có trùng với department khác không
        Departments deptWithSameName = findByName(updatedDepartment.getDepartmentName());
        if (deptWithSameName != null && deptWithSameName.getId() != id) {
            throw new RuntimeException("Department name already exists!");
        }

        existingDept.setDepartmentName(updatedDepartment.getDepartmentName());
        return departmentRepository.save(existingDept);
    }

    /**
     * ✅ HOẶC GIỮ CODE CŨ - NHƯNG THÊM VALIDATION
     */
    public Departments saveDepartment(Departments department) {
        // Nếu có ID => Update
        if (department.getId() != 0) {
            return updateDepartment(department.getId(), department);
        }
        // Không có ID => Add
        return addDepartment(department);
    }

    public void deleteDepartment(int id) {
        departmentRepository.deleteById(id);
    }

    // ========== HELPER METHODS ==========

    /**
     * ✅ THÊM: Kiểm tra tên tồn tại
     */
    public boolean isDepartmentNameExists(String departmentName) {
        return departmentRepository.existsByDepartmentName(departmentName);
    }
}