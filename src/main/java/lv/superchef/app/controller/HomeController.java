package lv.superchef.app.controller;

import lv.superchef.app.model.Recipe;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private IRecipeService recipeService;

    @GetMapping("/")
    public String getControllerShowHomePage(Model model) {
        model.addAttribute("activePage", "home");
        return "home-view";
    }

    @GetMapping("/feed")
    public String feed(Model model) {
        model.addAttribute("activePage", "feed");
        List<Recipe> recipes = recipeService.searchRecipes("", "", null, null, null, null);
        model.addAttribute("recipes", recipes);
        return "feed";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("activePage", "profile");
        return "profile";
    }
}