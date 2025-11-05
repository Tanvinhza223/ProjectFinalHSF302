package huynhtanvinh.projectfinalhsf302.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key, auto-increment

    @NotBlank
    @Column(name = "studentid", unique = true, nullable = false)
    private String studentId; // Unique student code

    @NotBlank
    @Size(min = 5, max = 50)
    @Column(nullable = false)
    private String name; // 5–50 characters

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    @Column(nullable = false)
    private Float gpa; // 0 ≤ gpa ≤ 10
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt; // Creation date
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt; // Last update date

    @Column(name = "created_by", nullable = false)
    private String createdBy; // Username of creator
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false )
    private Departments department;
}
