package huynhtanvinh.projectfinalhsf302.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
     private String username;
     private String password;
     private int role;
}
