package huynhtanvinh.projectfinalhsf302.service;

import huynhtanvinh.projectfinalhsf302.model.Departments;
import huynhtanvinh.projectfinalhsf302.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public Page<Departments> getAllDepartments(int pageNumber, int pageSize) {
        // Logic to retrieve paginated list of departments from the database
        return departmentRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }
    public Departments getById(int id) {
        return departmentRepository.findById(id).orElse(null);
    }
    public List<Departments> getAllDepartments() {
        return departmentRepository.findAll();
    }
   public  Departments saveDepartment(Departments department) {
        return departmentRepository.save(department);
    }
    public  void deleteDepartment(int id) {
        departmentRepository.deleteById(id);
    }
    public Departments updateDepartment(Departments department) {
        return departmentRepository.save(department);
    }

}
