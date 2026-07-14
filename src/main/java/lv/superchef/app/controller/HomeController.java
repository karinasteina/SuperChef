package lv.superchef.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/home")
    public String getControllerShowHomePage(){
        return "home-view";
    }
}
