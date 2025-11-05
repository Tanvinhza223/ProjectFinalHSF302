package huynhtanvinh.projectfinalhsf302.dto;

// package huynhtanvinh.projectfinalhsf302.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StudentForm {
    private int id; // null => create, not null => update

    @NotBlank
    @Size(min = 3, max = 30)
    private String studentId;

    @NotBlank
    @Size(min = 5, max = 50)
    private String name;

    @NotNull
    @DecimalMin("0.00") @DecimalMax("10.00")
    private float gpa; // nhận từ form; service sẽ convert sang BigDecimal

    @NotNull
    private int departmentId; // chọn từ dropdown
}

