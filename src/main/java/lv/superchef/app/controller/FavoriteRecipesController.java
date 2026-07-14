package lv.superchef.app.controller;

import lv.superchef.app.model.FavoriteRecipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.impl.FavoriteRecipeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/recipes/favorites")
public class FavoriteRecipesController {

    private final FavoriteRecipeService favoriteRecipeService;


    public FavoriteRecipesController(FavoriteRecipeService favoriteRecipeService) {
        this.favoriteRecipeService = favoriteRecipeService;

    }

    @GetMapping
    public String listFavoriteRecipes(@AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        List<FavoriteRecipe> favoriteRecipes = favoriteRecipeService.getAllFavoriteRecipesByUserId(userId);

        model.addAttribute("favoriteRecipes", favoriteRecipes);

        return "favorite-recipe/list";
    }
}