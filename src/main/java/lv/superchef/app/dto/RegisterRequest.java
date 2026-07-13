package lv.superchef.app.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
