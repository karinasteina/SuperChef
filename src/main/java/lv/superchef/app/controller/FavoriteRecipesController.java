package lv.superchef.app.controller;

import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.model.Profile;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.RecipeService;
import lv.superchef.app.service.impl.FavoriteRecipeService;
import lv.superchef.app.service.impl.ProfileServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/recipes/favorites")
public class FavoriteRecipesController {

    private final FavoriteRecipeService favoriteRecipeService;
    private final RecipeService recipeService;
    private final ProfileServiceImpl profileService;

    public FavoriteRecipesController(FavoriteRecipeService favoriteRecipeService, RecipeService recipeService, ProfileServiceImpl profileService) {
        this.favoriteRecipeService = favoriteRecipeService;
        this.recipeService = recipeService;
        this.profileService = profileService;

    }


    @PostMapping("/{recipeId}")
    public ResponseEntity<Void> addFavoriteRecipe(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long recipeId) {
        Long userId = userDetails.getUserId();

        Profile profile = profileService.getProfileByUserId(userId).orElse(null);
        Recipe recipe = recipeService.getRecipeById(recipeId);

        favoriteRecipeService.addToFavorites(recipe, profile);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping
    public String listFavoriteRecipes(@AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        List<FavoriteRecipe> favoriteRecipes = favoriteRecipeService.getAllFavoriteRecipesByUserId(userId);

        model.addAttribute("favoriteRecipes", favoriteRecipes);

        return "favorite-recipe/list";
    }
}