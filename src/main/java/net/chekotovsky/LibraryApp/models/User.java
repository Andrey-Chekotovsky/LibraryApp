package net.chekotovsky.LibraryApp.models;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
public class User implements Persistable<Long> {

    @Id
    @Column(name = "user_id", updatable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude()
    private Long id = null;
//    @NotEmpty(message = "First name shouldn't be empty")
//    @Size(min = 2, max = 30, message = "First name should include between 2 and 30 characters")
    @Column(name = "first_name", updatable = true)
    private String firstName;

//    @NotEmpty(message = "Last name shouldn't be empty")
//    @Size(min = 2, max = 30, message = "Last name should include between 2 and 30 characters")
    @Column(name = "last_name", updatable = true)
    private  String lastName;
//    @NotEmpty(message = "Username shouldn't be empty")
//    @Size(min = 2, max = 30, message = "Username should include between 2 and 30 characters")
    @Column(name = "username", updatable = true)
    private String username;
//    @NotEmpty(message = "Password shouldn't be empty")
//    @ValidPassword(message = "Password should contain upper  and lower case characters, digit and special symbol")
    @Column(name = "password", updatable = true)
    private String password;
    @Column(name = "enabled", updatable = true)
    private boolean enabled;
    @Column(name = "role", updatable = true)
    private Role role = Role.Customer;

    @ToString.Include(name = "password")
    private String maskPassword()
    {
        return "********";
    }


    @Override
    public boolean isNew() {
        if (id == null)
            return false;
        else return true;
    }
}
