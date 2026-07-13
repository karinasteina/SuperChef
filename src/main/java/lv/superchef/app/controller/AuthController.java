package lv.superchef.app.controller;

import jakarta.validation.Valid;
import lv.superchef.app.dto.RegisterRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    @GetMapping("/register")
    public String getControllerRegisterUser(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register-form-view";
    }

    @PostMapping("/register")
    public String postControllerRegisterUser(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest, BindingResult result) {
        if (result.hasErrors()) {
            return "register-form-view";
        }

        return "redirect:/home";
    }

}
