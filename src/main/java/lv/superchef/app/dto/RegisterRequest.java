package lv.superchef.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9_]{5,15}$",
            message = "Username must be 5-15 characters and contain only letters, numbers, and underscores"
    )
    private String username;

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Please enter a valid email address"
    )
    private String email;

    @NotBlank
    @Size(min = 12, message = "Password must be at least 12 characters")
    private String password;

    @NotBlank
    private String confirmPassword;

    public RegisterRequest(String username, String password, String email, String confirmPassword){
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setConfirmPassword(confirmPassword);
    }

}
