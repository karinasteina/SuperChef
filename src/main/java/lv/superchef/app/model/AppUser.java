package lv.superchef.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name="AppUser")
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    @Column(name="UserId")
    private Long id;

    @Column(name="Username")
    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9_]{5,15}$",
            message = "Username must be 5-15 characters and contain only letters, numbers, and underscores"
    )
    private String username;

    @Column(name="Email")
    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Please enter a valid email address"
    )
    private String email;

    @NotBlank
    @Column(name = "Password")
    @Size(min = 12, message = "Password must be at least 12 characters")
    private String password;

    @NotBlank
    @Column(name="Role")
    private String role;

    public AppUser(String username, String password, String email, String role){
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setRole(role);
    }

}
