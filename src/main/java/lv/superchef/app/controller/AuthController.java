package lv.superchef.app.controller;

import jakarta.validation.Valid;
import lv.superchef.app.dto.RegisterRequest;
import lv.superchef.app.service.IAppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final IAppUserService userService;

    public AuthController(IAppUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getControllerShowLogInPage() {
        return "login-view";
    }

    @GetMapping("/register")
    public String getRegisterUser(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register-form-view";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegisterRequest registerRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register-form-view";
        }
        try {
            userService.register(registerRequest);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register-form-view";
        }

        return "redirect:/login?registered";
    }

}
