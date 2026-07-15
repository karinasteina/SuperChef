package lv.superchef.app.controller;

import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.security.AppUserDetails;
import lv.superchef.app.service.IRecipeService;
import lv.superchef.app.service.impl.FavoriteRecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    private final IRecipeService recipeService;
    private final FavoriteRecipeService favoriteRecipeService;

    public RecipeController(IRecipeService recipeService, FavoriteRecipeService favoriteRecipeService) {
        this.recipeService = recipeService;
        this.favoriteRecipeService = favoriteRecipeService;
    }

    @GetMapping
    public String listOfRecipes(@AuthenticationPrincipal AppUserDetails userDetails, @RequestParam(required = false) String query, @RequestParam(required = false) String category, @RequestParam(required = false) String difficulty, @RequestParam(required = false) Integer maxCalories, @RequestParam(required = false) Integer maxPrepTime, @RequestParam(required = false) Integer maxCookTime, Model model) {
        List<Recipe> recipes = recipeService.searchRecipes(query, category, difficulty, maxCalories, maxPrepTime, maxCookTime);

        model.addAttribute("recipes", recipes);
        addFavoriteState(userDetails, model);

        return "recipe/list";
    }

    @GetMapping("/{id}")
    public String recipeDetails(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails userDetails, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);

        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        model.addAttribute("recipe", recipe);
        addFavoriteState(userDetails, model);

        return "recipe/details";
    }

    @GetMapping("/create")
    public String showCreateRecipePage(Model model) {
        model.addAttribute("activePage", "createRecipe");
        model.addAttribute("createRecipeDto", new RecipeCreateDTO());
        model.addAttribute("ingredientUnits", IngredientUnit.values());

        return "recipe/create";
    }

    @PostMapping
    public String handleCreateRecipe(@ModelAttribute RecipeCreateDTO createRecipeDto, @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        // TODO: Pass to service layer once backend is wired
        System.out.println("Received recipe: " + createRecipeDto.getTitle());

        return "redirect:/recipes/create?success=true";
    }

    private void addFavoriteState(AppUserDetails userDetails, Model model) {
        boolean loggedIn = userDetails != null;

        Set<Long> favoriteRecipeIds;

        if (loggedIn) {
            favoriteRecipeIds = favoriteRecipeService.getFavoriteRecipeIdsByUserId(userDetails.getUserId());
        } else {
            favoriteRecipeIds = Set.of();
        }

        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("favoriteRecipeIds", favoriteRecipeIds);
    }
}