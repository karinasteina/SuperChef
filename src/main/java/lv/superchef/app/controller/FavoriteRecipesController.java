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
import java.util.Set;
import java.util.stream.Collectors;

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
        if (userDetails == null) {
            return ResponseEntity
                    .status(401)
                    .build();
        }

        Recipe recipe = recipeService.getRecipeById(recipeId);

        if (recipe == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        Profile profile = getProfile(userDetails);

        favoriteRecipeService.addToFavorites(recipe, profile);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/{recipeId}/unfavorite")
    public ResponseEntity<Void> removeFavoriteRecipe(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long recipeId) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(401)
                    .build();
        }

        Recipe recipe = recipeService.getRecipeById(recipeId);

        if (recipe == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        Profile profile = getProfile(userDetails);

        favoriteRecipeService.removeFromFavorites(recipe, profile);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping
    public String listFavoriteRecipes(@AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        List<FavoriteRecipe> favoriteRecipes = favoriteRecipeService.getAllFavoriteRecipesByUserId(userDetails.getUserId());

        List<Recipe> recipes = favoriteRecipes
                .stream()
                .map(FavoriteRecipe::getRecipe)
                .toList();

        Set<Long> favoriteRecipeIds = recipes
                .stream()
                .map(Recipe::getId)
                .collect(Collectors.toSet());

        model.addAttribute("recipes", recipes);
        model.addAttribute("favoriteRecipeIds", favoriteRecipeIds);
        model.addAttribute("loggedIn", true);

        return "favorite-recipe/favorite-list";
    }

    private Profile getProfile(AppUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        return profileService
                .getProfileByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Profile not found for user ID: " + userId));
    }
}