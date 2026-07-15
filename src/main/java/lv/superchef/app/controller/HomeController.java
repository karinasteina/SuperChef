package lv.superchef.app.controller;

import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IRecipeService;
import lv.superchef.app.service.impl.FavoriteRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@Controller
public class HomeController {
    @Autowired
    private IRecipeService recipeService;
    @Autowired
    private FavoriteRecipeService favoriteRecipeService;

    @GetMapping("/")
    public String getControllerShowHomePage(Model model) {
        model.addAttribute("activePage", "home");
        return "home-view";
    }

    @GetMapping("/feed")
    public String feed(@AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        model.addAttribute("activePage", "feed");
        List<Recipe> recipes = recipeService.searchRecipes("", "", null, null, null, null);

        model.addAttribute("recipes", recipes);
        addFavoriteState(userDetails, model);
        model.addAttribute("loggedIn", userDetails != null);
        return "feed";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("activePage", "profile");
        return "profile";
    }

    private void addFavoriteState(AppUserDetails userDetails, Model model) {
        boolean loggedIn = userDetails != null;

        Set<Long> favoriteRecipeIds = loggedIn ? favoriteRecipeService.getFavoriteRecipeIdsByUserId(userDetails.getUserId()) : Set.of();

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("favoriteRecipeIds", favoriteRecipeIds);
    }
}