package lv.superchef.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static lv.superchef.app.config.TempData.RECIPE_DATA;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getControllerShowHomePage(Model model) {
        model.addAttribute("activePage", "home");
        return "home-view";
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        model.addAttribute("activePage", "feed");
        model.addAttribute("recipes", RECIPE_DATA);
        return "feed";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("activePage", "profile");
        return "profile";
    }
}
