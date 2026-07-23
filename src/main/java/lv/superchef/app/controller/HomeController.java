package lv.superchef.app.controller;

import lv.superchef.app.dto.ProfileDTO;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IFavoriteRecipeService;
import lv.superchef.app.service.IProfileService;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@Controller
public class HomeController {

    private final IRecipeService recipeService;
    private final IFavoriteRecipeService favoriteRecipeService;
    private final IProfileService profileService;

    public HomeController(IRecipeService recipeService, IFavoriteRecipeService favoriteRecipeService, IProfileService profileService) {
        this.recipeService = recipeService;
        this.favoriteRecipeService = favoriteRecipeService;
        this.profileService = profileService;
    }

    @GetMapping("/")
    public String showHomePage(Model model, @AuthenticationPrincipal AppUserDetails userDetails) {
        model.addAttribute("activePage", "home");
        model.addAttribute("dinnerRecipes",
                recipeService.searchRecipes("", "Dinner", "", null, null, null, 3));
        model.addAttribute("tenMinuteRecipes",
                recipeService.searchRecipes("", "", "", null, 10, null, 3));
        model.addAttribute("hardRecipes",
                recipeService.searchRecipes("", "", "Hard", null, null, null, 3));
        model.addAttribute("chefs", profileService.getAllProfiles().stream()
                .map(ProfileDTO::mapToDto)
                .toList());
        addFavoriteState(userDetails, model);
        return "home-view";
    }

    @GetMapping("/feed")
    public String feed(@AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        model.addAttribute("activePage", "feed");
        List<Recipe> recipes = recipeService.getRecipesByFollowedProfilesForUser(userDetails.getUserId());

        model.addAttribute("recipes", recipes);
        addFavoriteState(userDetails, model);
        model.addAttribute("loggedIn", userDetails != null);
        return "feed";
    }


    private void addFavoriteState(AppUserDetails userDetails, Model model) {
        boolean loggedIn = userDetails != null;

        Set<Long> favoriteRecipeIds = loggedIn ? favoriteRecipeService.getFavoriteRecipeIdsByUserId(userDetails.getUserId()) : Set.of();

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("favoriteRecipeIds", favoriteRecipeIds);
    }
}
