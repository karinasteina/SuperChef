package lv.superchef.app.controller;

import lv.superchef.app.dto.IngredientInputDTO;
import lv.superchef.app.dto.RecipeCreateDTO;
import lv.superchef.app.enums.IngredientUnit;
import lv.superchef.app.model.Recipe;
import lv.superchef.app.service.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private IRecipeService recipeService;

    @GetMapping
    public String listOfRecipes(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) Integer maxPrepTime,
            @RequestParam(required = false) Integer maxCookTime,
            Model model) {

        List<Recipe> recipes = recipeService.searchRecipes(query, category,
                difficulty, maxCalories, maxPrepTime, maxCookTime);

        model.addAttribute("recipes", recipes);

        return "recipe/list";
    }

    @GetMapping("/{id}")
    public String recipeDetails(@PathVariable Long id, Model model) {
        model.addAttribute("recipe", recipeService.getRecipeById(id));

        return "recipe/details";
    }

    @GetMapping("/create")
    public String showCreateRecipePage(Model model) {
        RecipeCreateDTO createRecipeDto = new RecipeCreateDTO();
        createRecipeDto.setIngredients(new ArrayList<>(List.of(new IngredientInputDTO())));
        createRecipeDto.setSteps(new ArrayList<>(List.of("")));

        model.addAttribute("activePage", "createRecipe");
        model.addAttribute("createRecipeDto", createRecipeDto);
        model.addAttribute("ingredientUnits", IngredientUnit.values());
        return "recipe/create";
    }

    @PostMapping
    public String handleCreateRecipe(
            @ModelAttribute RecipeCreateDTO createRecipeDto,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        recipeService.createRecipe(createRecipeDto, coverImage);
        return "redirect:/recipes?success=true";
    }
}