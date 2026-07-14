package lv.superchef.app.controller;

import lv.superchef.app.dto.CreateRecipeDto;
import lv.superchef.app.dto.IngredientUnit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static lv.superchef.app.config.TempData.RECIPE_DATA;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @GetMapping
    public String listOfRecipes(@RequestParam(defaultValue = "") String query,
                                @RequestParam(defaultValue = "") String category,
                                @RequestParam(defaultValue = "") String difficulty,
                                @RequestParam(required = false) Integer maxCalories,
                                @RequestParam(required = false) Integer maxPrepTime,
                                @RequestParam(required = false) Integer maxCookTime, Model model) {
        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);

        List<Integer> recipeIds = IntStream.range(0, RECIPE_DATA.size())
                .filter(index -> {
                    CreateRecipeDto recipe = RECIPE_DATA.get(index);

                    return matchesSearch(recipe, normalizedQuery)
                            && (category.isBlank()
                            || category.equalsIgnoreCase(recipe.getCategory()))
                            && (difficulty.isBlank()
                            || difficulty.equalsIgnoreCase(recipe.getDifficulty()))
                            && (maxCalories == null
                            || recipe.getCalories() <= maxCalories)
                            && (maxPrepTime == null
                            || recipe.getPreparationTime() <= maxPrepTime)
                            && (maxCookTime == null
                            || recipe.getCookingTime() <= maxCookTime);
                })
                .boxed()
                .toList();

        List<CreateRecipeDto> filteredRecipes = recipeIds.stream()
                .map(RECIPE_DATA::get)
                .toList();

        model.addAttribute("activePage", "recipes");
        model.addAttribute("recipes", filteredRecipes);
        model.addAttribute("recipeIds", recipeIds);

        return "recipe/list";
    }

    private boolean matchesSearch(CreateRecipeDto recipe, String query) {
        if (query.isBlank()) {
            return true;
        }

        boolean matchesRecipeText =
                containsIgnoreCase(recipe.getTitle(), query)
                        || containsIgnoreCase(recipe.getDescription(), query);

        boolean matchesIngredient = recipe.getIngredients() != null
                && recipe.getIngredients().stream()
                .anyMatch(ingredient ->
                        containsIgnoreCase(ingredient.getName(), query));

        return matchesRecipeText || matchesIngredient;
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(query);
    }

    @GetMapping("/{id}")
    public String recipeDetails(@PathVariable int id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("recipe", RECIPE_DATA.get(id));
        return "recipe/details";
    }

    @GetMapping("/create")
    public String showCreateRecipePage(Model model) {
        model.addAttribute("activePage", "createRecipe");
        model.addAttribute("createRecipeDto", new CreateRecipeDto());
        model.addAttribute("ingredientUnits", IngredientUnit.values());
        return "recipe/create";
    }

    @PostMapping
    public String handleCreateRecipe(
            @ModelAttribute CreateRecipeDto createRecipeDto,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        // TODO: Pass to service layer once backend is wired
        System.out.println("Received recipe: " + createRecipeDto.getTitle());
        return "redirect:/recipes/create?success=true";
    }

}
